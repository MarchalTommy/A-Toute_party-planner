package com.martodev.atoute.authentication.domain.usecase

import com.martodev.atoute.authentication.domain.model.AuthResult
import com.martodev.atoute.authentication.domain.repository.AuthRepository

/**
 * Cas d'utilisation pour connecter un utilisateur
 *
 * @property authRepository Repository d'authentification
 */
class SignInUseCase(private val authRepository: AuthRepository) {
    /**
     * Exécute le cas d'utilisation
     *
     * @param email Email de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     * @return Résultat de l'opération
     */
    suspend operator fun invoke(email: String, password: String): AuthResult {
        // Validation des entrées
        if (email.isBlank()) {
            return AuthResult.Error("L'email ne peut pas être vide")
        }
        
        if (password.isBlank()) {
            return AuthResult.Error("Le mot de passe ne peut pas être vide")
        }
        
        return authRepository.signIn(email, password)
    }
} 