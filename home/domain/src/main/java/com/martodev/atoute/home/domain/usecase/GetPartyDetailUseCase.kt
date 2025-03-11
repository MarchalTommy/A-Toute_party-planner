package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.model.Party
import com.martodev.atoute.home.domain.repository.PartyRepository
import kotlinx.coroutines.flow.Flow

/**
 * Cas d'utilisation pour récupérer les détails d'une party spécifique
 */
class GetPartyDetailUseCase(private val partyRepository: PartyRepository) {
    
    /**
     * Récupère les détails d'une party spécifique
     * @param partyId L'identifiant de la party
     */
    operator fun invoke(partyId: String): Flow<Party?> {
        return partyRepository.getPartyById(partyId)
    }
} 