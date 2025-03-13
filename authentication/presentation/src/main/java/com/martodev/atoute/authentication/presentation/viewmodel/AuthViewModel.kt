package com.martodev.atoute.authentication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martodev.atoute.authentication.domain.model.AuthResult
import com.martodev.atoute.authentication.domain.model.User
import com.martodev.atoute.authentication.domain.usecase.CreateAccountUseCase
import com.martodev.atoute.authentication.domain.usecase.CreateAnonymousUserUseCase
import com.martodev.atoute.authentication.domain.usecase.GetCurrentUserUseCase
import com.martodev.atoute.authentication.domain.usecase.SignInUseCase
import com.martodev.atoute.authentication.domain.usecase.SignOutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * État de l'authentification
 *
 * @property isLoading Indique si une opération est en cours
 * @property user Utilisateur actuel ou null
 * @property error Message d'erreur ou null
 */
data class AuthState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)

/**
 * ViewModel pour l'authentification
 *
 * @property getCurrentUserUseCase Use case pour récupérer l'utilisateur actuel
 * @property createAnonymousUserUseCase Use case pour créer un utilisateur anonyme
 * @property createAccountUseCase Use case pour créer un compte utilisateur
 * @property signInUseCase Use case pour connecter un utilisateur
 * @property signOutUseCase Use case pour déconnecter un utilisateur
 */
class AuthViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val createAnonymousUserUseCase: CreateAnonymousUserUseCase,
    private val createAccountUseCase: CreateAccountUseCase,
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        // Récupérer l'utilisateur actuel au démarrage
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                _state.update { it.copy(user = user) }
            }
        }
    }

    /**
     * Crée un utilisateur anonyme
     *
     * @param username Pseudo de l'utilisateur
     */
    fun createAnonymousUser(username: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            val result = createAnonymousUserUseCase(username)
            
            when (result) {
                is AuthResult.Success -> {
                    _state.update { it.copy(isLoading = false, user = result.user) }
                }
                is AuthResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    /**
     * Crée un compte utilisateur
     *
     * @param username Pseudo de l'utilisateur
     * @param email Email de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     */
    fun createAccount(username: String, email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            val result = createAccountUseCase(username, email, password)
            
            when (result) {
                is AuthResult.Success -> {
                    _state.update { it.copy(isLoading = false, user = result.user) }
                }
                is AuthResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    /**
     * Connecte un utilisateur
     *
     * @param email Email de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     */
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            val result = signInUseCase(email, password)
            
            when (result) {
                is AuthResult.Success -> {
                    _state.update { it.copy(isLoading = false, user = result.user) }
                }
                is AuthResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    /**
     * Déconnecte l'utilisateur actuel
     */
    fun signOut() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            signOutUseCase()
            
            _state.update { it.copy(isLoading = false, user = null) }
        }
    }

    /**
     * Efface le message d'erreur
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
} 