package com.martodev.atoute.party.domain.usecase

import com.martodev.atoute.party.domain.repository.ToBuyRepository

/**
 * Cas d'utilisation pour mettre à jour le statut d'un article à acheter (acheté ou non)
 */
class UpdateToBuyStatusUseCase(
    private val toBuyRepository: ToBuyRepository
) {
    /**
     * Met à jour le statut d'un article à acheter
     * @param toBuyId L'identifiant de l'article
     * @param isPurchased Le nouveau statut de l'article
     */
    suspend operator fun invoke(toBuyId: String, isPurchased: Boolean) {
        toBuyRepository.updateToBuyStatus(toBuyId, isPurchased)
    }
} 