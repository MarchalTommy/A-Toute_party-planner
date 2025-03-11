package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.model.ToBuy
import com.martodev.atoute.home.domain.repository.ToBuyRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case pour récupérer les articles à acheter pour une Party spécifique
 */
class GetToBuysByPartyUseCase(
    private val toBuyRepository: ToBuyRepository
) {
    
    /**
     * Exécute le use case pour récupérer tous les articles à acheter pour une Party
     *
     * @param partyId l'identifiant de la Party
     * @return un flux contenant la liste des articles à acheter pour cette Party
     */
    operator fun invoke(partyId: String): Flow<List<ToBuy>> {
        return toBuyRepository.getToBuysByParty(partyId)
    }
} 