package com.martodev.atoute.authentication.domain.usecase

import com.martodev.atoute.authentication.domain.model.AuthResult
import com.martodev.atoute.authentication.domain.model.UserPreferences
import com.martodev.atoute.authentication.domain.repository.AuthRepository

/**
 * Cas d'utilisation pour mettre à jour les préférences de l'utilisateur
 *
 * @property authRepository Repository d'authentification
 */
class UpdateUserPreferencesUseCase(private val authRepository: AuthRepository) {
    /**
     * Exécute le cas d'utilisation
     *
     * @param userId ID de l'utilisateur
     * @param preferences Nouvelles préférences
     * @return Résultat de l'opération
     */
    suspend operator fun invoke(userId: String, preferences: UserPreferences): AuthResult {
        if (userId.isBlank()) {
            return AuthResult.Error("L'ID utilisateur ne peut pas être vide")
        }
        
        return authRepository.updateUserPreferences(userId, preferences)
    }
} 