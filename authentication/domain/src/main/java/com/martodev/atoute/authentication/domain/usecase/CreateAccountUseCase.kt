package com.martodev.atoute.authentication.domain.usecase

import com.martodev.atoute.authentication.domain.model.AuthResult
import com.martodev.atoute.authentication.domain.repository.AuthRepository

/**
 * Cas d'utilisation pour créer un compte utilisateur
 *
 * @property authRepository Repository d'authentification
 */
class CreateAccountUseCase(private val authRepository: AuthRepository) {
    /**
     * Exécute le cas d'utilisation
     *
     * @param username Pseudo de l'utilisateur
     * @param email Email de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     * @return Résultat de l'opération
     */
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String
    ): AuthResult {
        // Validation des entrées
        if (username.isBlank()) {
            return AuthResult.Error("Le pseudo ne peut pas être vide")
        }
        
        if (email.isBlank()) {
            return AuthResult.Error("L'email ne peut pas être vide")
        }
        
        if (!email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"))) {
            return AuthResult.Error("Format d'email invalide")
        }
        
        if (password.length < 6) {
            return AuthResult.Error("Le mot de passe doit contenir au moins 6 caractères")
        }
        
        return authRepository.createAccount(username, email, password)
    }
} 