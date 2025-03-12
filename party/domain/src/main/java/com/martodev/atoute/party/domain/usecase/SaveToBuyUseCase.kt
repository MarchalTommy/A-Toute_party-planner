package com.martodev.atoute.party.domain.usecase

import com.martodev.atoute.party.domain.model.ToBuy
import com.martodev.atoute.party.domain.repository.ToBuyRepository

/**
 * Cas d'utilisation pour sauvegarder un article à acheter
 */
class SaveToBuyUseCase(
    private val toBuyRepository: ToBuyRepository
) {
    /**
     * Sauvegarde un article à acheter
     * @param toBuy L'article à sauvegarder
     */
    suspend operator fun invoke(toBuy: ToBuy) {
        toBuyRepository.saveToBuy(toBuy)
    }
} 