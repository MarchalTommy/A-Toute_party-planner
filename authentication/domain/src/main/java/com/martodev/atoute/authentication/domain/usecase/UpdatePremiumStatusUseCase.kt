package com.martodev.atoute.authentication.domain.usecase

import com.martodev.atoute.authentication.domain.model.AuthResult
import com.martodev.atoute.authentication.domain.repository.AuthRepository

/**
 * Cas d'utilisation pour mettre à jour le statut premium de l'utilisateur
 *
 * @property authRepository Repository d'authentification
 */
class UpdatePremiumStatusUseCase(private val authRepository: AuthRepository) {
    /**
     * Exécute le cas d'utilisation
     *
     * @param userId ID de l'utilisateur
     * @param isPremium Nouveau statut premium
     * @return Résultat de l'opération
     */
    suspend operator fun invoke(userId: String, isPremium: Boolean): AuthResult {
        if (userId.isBlank()) {
            return AuthResult.Error("L'ID utilisateur ne peut pas être vide")
        }
        
        return authRepository.updatePremiumStatus(userId, isPremium)
    }
} 