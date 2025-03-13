package com.martodev.atoute.authentication.domain.usecase

import com.martodev.atoute.authentication.domain.model.User
import com.martodev.atoute.authentication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * Cas d'utilisation pour récupérer l'utilisateur actuel
 *
 * @property authRepository Repository d'authentification
 */
class GetCurrentUserUseCase(private val authRepository: AuthRepository) {
    /**
     * Exécute le cas d'utilisation
     *
     * @return Flow contenant l'utilisateur actuel ou null
     */
    operator fun invoke(): Flow<User?> {
        return authRepository.getCurrentUser()
    }
} 