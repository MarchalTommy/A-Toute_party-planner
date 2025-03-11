package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.model.Party
import com.martodev.atoute.home.domain.repository.PartyRepository

/**
 * Cas d'utilisation pour sauvegarder une party
 */
class SavePartyUseCase(private val partyRepository: PartyRepository) {
    
    /**
     * Sauvegarde une party
     * @param party La party à sauvegarder
     */
    suspend operator fun invoke(party: Party) {
        partyRepository.saveParty(party)
    }
} 