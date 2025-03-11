package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.model.Party
import com.martodev.atoute.home.domain.repository.PartyRepository

/**
 * Cas d'utilisation pour sauvegarder une Party
 */
class SavePartyUseCase(private val partyRepository: PartyRepository) {
    /**
     * Sauvegarde une Party et retourne son ID
     * @param party la Party à sauvegarder
     * @return l'ID de la Party sauvegardée
     */
    suspend operator fun invoke(party: Party): String {
        partyRepository.saveParty(party)
        return party.id
    }
} 