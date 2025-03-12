package com.martodev.atoute.party.domain.usecase

import com.martodev.atoute.party.domain.repository.ToBuyRepository

/**
 * Cas d'utilisation pour mettre à jour la priorité d'un article à acheter
 */
class UpdateToBuyPriorityUseCase(
    private val toBuyRepository: ToBuyRepository
) {
    /**
     * Met à jour la priorité d'un article à acheter
     * @param toBuyId L'identifiant de l'article
     * @param isPriority Indique si l'article est prioritaire
     */
    suspend operator fun invoke(toBuyId: String, isPriority: Boolean) {
        toBuyRepository.updateToBuyPriority(toBuyId, isPriority)
    }
} 