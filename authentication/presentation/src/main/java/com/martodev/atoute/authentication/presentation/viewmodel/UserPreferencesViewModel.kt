package com.martodev.atoute.authentication.presentation.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martodev.atoute.authentication.domain.model.AuthResult
import com.martodev.atoute.authentication.domain.model.User
import com.martodev.atoute.authentication.domain.model.UserPreferences
import com.martodev.atoute.authentication.domain.usecase.GetCurrentUserUseCase
import com.martodev.atoute.authentication.domain.usecase.SignOutUseCase
import com.martodev.atoute.authentication.domain.usecase.UpdatePremiumStatusUseCase
import com.martodev.atoute.authentication.domain.usecase.UpdateUserPreferencesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * État des préférences utilisateur
 *
 * @property isLoading Indique si une opération est en cours
 * @property user Utilisateur actuel ou null
 * @property preferences Préférences de l'utilisateur
 * @property error Message d'erreur ou null
 */
data class UserPreferencesState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val preferences: UserPreferences = UserPreferences(),
    val error: String? = null
)

/**
 * ViewModel pour les préférences utilisateur
 *
 * @property getCurrentUserUseCase Use case pour récupérer l'utilisateur actuel
 * @property updateUserPreferencesUseCase Use case pour mettre à jour les préférences de l'utilisateur
 * @property updatePremiumStatusUseCase Use case pour mettre à jour le statut premium de l'utilisateur
 * @property signOutUseCase Use case pour déconnecter l'utilisateur
 */
class UserPreferencesViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateUserPreferencesUseCase: UpdateUserPreferencesUseCase,
    private val updatePremiumStatusUseCase: UpdatePremiumStatusUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(UserPreferencesState())
    val state: StateFlow<UserPreferencesState> = _state.asStateFlow()

    init {
        // Récupérer l'utilisateur actuel au démarrage
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                _state.update {
                    it.copy(
                        user = user,
                        preferences = user?.preferences ?: UserPreferences()
                    )
                }
            }
        }
    }

    /**
     * Met à jour les préférences de l'utilisateur
     *
     * @param preferences Nouvelles préférences
     */
    fun updatePreferences(preferences: UserPreferences) {

        // Si l'utilisateur n'a pas d'ID, on se contente de la mise à jour UI (mode temporaire/anonyme)
        val userId = _state.value.user?.id
        if (userId.isNullOrBlank()) {
            // On ne peut pas sauvegarder pour un utilisateur sans ID, mais l'UI est mise à jour
            Log.e(TAG, "updatePreferences: ID SHOULD NEVER BE NULL, WATCH OUT FOR THE CRASHES!")
            return
        }

        // Mise à jour optimiste de l'UI immédiatement - toujours faire cette mise à jour
        _state.update {
            it.copy(
                preferences = preferences,
                error = null,
                isLoading = true
            )
        }

        viewModelScope.launch {
            try {
                val result = updateUserPreferencesUseCase(userId, preferences)

                when (result) {
                    is AuthResult.Success -> {
                        _state.update {
                            it.copy(
                                user = result.user,
                                preferences = result.user.preferences,
                                isLoading = false
                            )
                        }
                    }

                    is AuthResult.Error -> {
                        _state.update {
                            it.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Erreur lors de la mise à jour: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Met à jour le statut premium de l'utilisateur
     *
     * @param isPremium Nouveau statut premium
     */
    fun updatePremiumStatus(isPremium: Boolean) {
        // Mise à jour optimiste de l'UI
        _state.update {
            it.copy(
                user = it.user?.copy(isPremium = isPremium),
                error = null
            )
        }

        val userId = _state.value.user?.id
        if (userId.isNullOrBlank()) {
            // On ne peut pas sauvegarder pour un utilisateur sans ID
            _state.update { it.copy(error = "Vous devez être connecté pour modifier le statut premium") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val result = updatePremiumStatusUseCase(userId, isPremium)

                when (result) {
                    is AuthResult.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                user = result.user
                            )
                        }
                    }

                    is AuthResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message,
                                // En cas d'erreur, on revient à l'état précédent
                                user = it.user?.copy(isPremium = !isPremium)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Erreur lors de la mise à jour: ${e.message}",
                        // En cas d'erreur, on revient à l'état précédent
                        user = it.user?.copy(isPremium = !isPremium)
                    )
                }
            }
        }
    }

    /**
     * Met à jour la préférence pour l'alcool
     *
     * @param drinksAlcohol Indique si l'utilisateur consomme de l'alcool
     */
    fun updateDrinksAlcohol(drinksAlcohol: Boolean) {
        val currentPreferences = _state.value.preferences
        val updatedPreferences = currentPreferences.copy(drinksAlcohol = drinksAlcohol)
        updatePreferences(updatedPreferences)
    }

    /**
     * Met à jour la préférence pour le régime halal
     *
     * @param isHalal Indique si l'utilisateur suit un régime halal
     */
    fun updateIsHalal(isHalal: Boolean) {
        val currentPreferences = _state.value.preferences
        updatePreferences(currentPreferences.copy(isHalal = isHalal))
    }

    /**
     * Met à jour la préférence pour le régime végétarien
     *
     * @param isVegetarian Indique si l'utilisateur est végétarien
     */
    fun updateIsVegetarian(isVegetarian: Boolean) {
        val currentPreferences = _state.value.preferences
        updatePreferences(currentPreferences.copy(isVegetarian = isVegetarian))
    }

    /**
     * Met à jour la préférence pour le régime végétalien
     *
     * @param isVegan Indique si l'utilisateur est végétalien
     */
    fun updateIsVegan(isVegan: Boolean) {
        val currentPreferences = _state.value.preferences
        updatePreferences(currentPreferences.copy(isVegan = isVegan))
    }

    /**
     * Ajoute une allergie
     *
     * @param allergy Allergie à ajouter
     */
    fun addAllergy(allergy: String) {
        val currentPreferences = _state.value.preferences
        val currentAllergies = currentPreferences.hasAllergies.toMutableList()

        if (allergy.isNotBlank() && !currentAllergies.contains(allergy)) {
            currentAllergies.add(allergy)
            updatePreferences(currentPreferences.copy(hasAllergies = currentAllergies))
        }
    }

    /**
     * Supprime une allergie
     *
     * @param allergy Allergie à supprimer
     */
    fun removeAllergy(allergy: String) {
        val currentPreferences = _state.value.preferences
        val currentAllergies = currentPreferences.hasAllergies.toMutableList()

        if (currentAllergies.contains(allergy)) {
            currentAllergies.remove(allergy)
            updatePreferences(currentPreferences.copy(hasAllergies = currentAllergies))
        }
    }

    /**
     * Efface le message d'erreur
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    /**
     * Déconnecte l'utilisateur actuel
     */
    fun signOut() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                signOutUseCase()
                _state.update {
                    it.copy(
                        isLoading = false,
                        user = null,
                        preferences = UserPreferences()
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Erreur lors de la déconnexion: ${e.message}"
                    )
                }
            }
        }
    }
} 