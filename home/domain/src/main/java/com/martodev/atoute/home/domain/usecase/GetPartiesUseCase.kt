package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.model.Party
import com.martodev.atoute.home.domain.repository.PartyRepository
import kotlinx.coroutines.flow.Flow

/**
 * Cas d'utilisation pour récupérer toutes les parties
 */
class GetPartiesUseCase(private val partyRepository: PartyRepository) {
    
    /**
     * Récupère toutes les parties
     */
    operator fun invoke(): Flow<List<Party>> {
        return partyRepository.getAllParties()
    }
} 