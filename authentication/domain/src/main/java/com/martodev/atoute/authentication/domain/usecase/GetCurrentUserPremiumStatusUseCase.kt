package com.martodev.atoute.authentication.domain.usecase

import com.martodev.atoute.authentication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Cas d'utilisation pour récupérer le statut premium de l'utilisateur courant
 *
 * @property authRepository Repository d'authentification
 */
class GetCurrentUserPremiumStatusUseCase(private val authRepository: AuthRepository) {
    /**
     * Exécute le cas d'utilisation
     *
     * @return Flow contenant le statut premium (true) ou non (false) de l'utilisateur
     */
    operator fun invoke(): Flow<Boolean> {
        return authRepository.getCurrentUser().map { user ->
            user?.isPremium == true
        }
    }
} 