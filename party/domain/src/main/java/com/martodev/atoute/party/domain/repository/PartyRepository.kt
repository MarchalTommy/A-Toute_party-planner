package com.martodev.atoute.party.domain.repository

import com.martodev.atoute.party.domain.model.Party
import kotlinx.coroutines.flow.Flow

/**
 * Interface du repository pour les Party dans la couche domaine
 */
interface PartyRepository {
    
    /**
     * Récupère toutes les parties
     */
    fun getAllParties(): Flow<List<Party>>
    
    /**
     * Récupère les parties auxquelles l'utilisateur a accès
     * (celles qu'il a créées et celles où il est participant)
     */
    fun getAccessibleParties(userId: String): Flow<List<Party>>
    
    /**
     * Récupère une party par son ID
     */
    fun getPartyById(partyId: String): Flow<Party?>
    
    /**
     * Récupère une party par son ID de manière synchrone
     */
    suspend fun getPartyByIdSync(partyId: String): Party?
    
    /**
     * Ajoute ou met à jour une party
     */
    suspend fun saveParty(party: Party)
    
    /**
     * Ajoute ou met à jour plusieurs parties
     */
    suspend fun saveParties(parties: List<Party>)
    
    /**
     * Supprime une party
     */
    suspend fun deleteParty(partyId: String)

}