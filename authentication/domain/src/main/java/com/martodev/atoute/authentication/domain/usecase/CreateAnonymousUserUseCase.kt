package com.martodev.atoute.authentication.domain.usecase

import com.martodev.atoute.authentication.domain.model.AuthResult
import com.martodev.atoute.authentication.domain.repository.AuthRepository

/**
 * Cas d'utilisation pour créer un utilisateur anonyme
 *
 * @property authRepository Repository d'authentification
 */
class CreateAnonymousUserUseCase(private val authRepository: AuthRepository) {
    /**
     * Exécute le cas d'utilisation
     *
     * @param username Pseudo de l'utilisateur
     * @return Résultat de l'opération
     */
    suspend operator fun invoke(username: String): AuthResult {
        if (username.isBlank()) {
            return AuthResult.Error("Le pseudo ne peut pas être vide")
        }
        
        return authRepository.createAnonymousUser(username)
    }
} 