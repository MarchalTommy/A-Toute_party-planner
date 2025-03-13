package com.martodev.atoute.authentication.domain.usecase

import com.martodev.atoute.authentication.domain.repository.AuthRepository

/**
 * Cas d'utilisation pour déconnecter un utilisateur
 *
 * @property authRepository Repository d'authentification
 */
class SignOutUseCase(private val authRepository: AuthRepository) {
    /**
     * Exécute le cas d'utilisation
     */
    suspend operator fun invoke() {
        authRepository.signOut()
    }
} 