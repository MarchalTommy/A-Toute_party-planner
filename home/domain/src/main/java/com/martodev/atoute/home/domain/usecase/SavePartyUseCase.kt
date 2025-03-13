package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.model.Party
import com.martodev.atoute.home.domain.repository.PartyRepository

/**
 * Cas d'utilisation pour sauvegarder une Party
 */
class SavePartyUseCase(
    private val partyRepository: PartyRepository,
    private val checkPartyLimitUseCase: CheckPartyLimitUseCase
) {
    /**
     * Exception lancée lorsque l'utilisateur a atteint sa limite d'événements
     */
    class PartyLimitReachedException(limit: Int) : Exception(
        "Vous avez atteint la limite de $limit événements pour un compte non-premium. " +
        "Passez à la version premium pour créer des événements illimités."
    )
    
    /**
     * Sauvegarde une Party et retourne son ID
     * @param party la Party à sauvegarder
     * @return l'ID de la Party sauvegardée
     * @throws PartyLimitReachedException si l'utilisateur a atteint sa limite d'événements
     */
    suspend operator fun invoke(party: Party): String {
        // Vérifie si c'est un nouvel événement (pas d'ID existant)
        val isNewParty = party.id.isBlank()
        
        // Si c'est un nouvel événement, vérifier la limite
        if (isNewParty) {
            val canCreateParty = checkPartyLimitUseCase.checkSync()
            if (!canCreateParty) {
                throw PartyLimitReachedException(CheckPartyLimitUseCase.NON_PREMIUM_PARTY_LIMIT)
            }
        }
        
        // Sauvegarder l'événement
        partyRepository.saveParty(party)
        return party.id
    }
} 