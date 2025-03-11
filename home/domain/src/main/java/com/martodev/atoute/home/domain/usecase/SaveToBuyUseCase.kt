package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.model.ToBuy
import com.martodev.atoute.home.domain.repository.ToBuyRepository

/**
 * Use case pour sauvegarder un article à acheter
 */
class SaveToBuyUseCase(private val toBuyRepository: ToBuyRepository) {

    /**
     * Sauvegarde un article à acheter
     * @param toBuy L'article à sauvegarder
     */
    suspend operator fun invoke(toBuy: ToBuy) {
        toBuyRepository.saveToBuy(toBuy)
    }
} 