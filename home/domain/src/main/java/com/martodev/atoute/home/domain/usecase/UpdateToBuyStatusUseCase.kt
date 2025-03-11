package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.repository.ToBuyRepository

/**
 * Cas d'utilisation pour mettre à jour le statut d'un article à acheter
 */
class UpdateToBuyStatusUseCase(private val toBuyRepository: ToBuyRepository) {
    
    /**
     * Met à jour le statut d'un article à acheter
     * @param toBuyId L'identifiant de l'article
     * @param isPurchased La nouvelle valeur d'achat
     */
    suspend operator fun invoke(toBuyId: String, isPurchased: Boolean) {
        toBuyRepository.updateToBuyStatus(toBuyId, isPurchased)
    }
} 