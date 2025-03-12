package com.martodev.atoute.party.domain.repository

import com.martodev.atoute.party.domain.model.ToBuy
import kotlinx.coroutines.flow.Flow

/**
 * Interface du repository pour les articles à acheter dans la couche domaine
 */
interface ToBuyRepository {
    
    /**
     * Récupère tous les articles à acheter
     */
    fun getAllToBuys(): Flow<List<ToBuy>>
    
    /**
     * Récupère tous les articles prioritaires
     */
    fun getPriorityToBuys(): Flow<List<ToBuy>>
    
    /**
     * Récupère les articles pour une party donnée
     */
    fun getToBuysByParty(partyId: String): Flow<List<ToBuy>>
    
    /**
     * Récupère un article par son ID
     */
    fun getToBuyById(toBuyId: String): Flow<ToBuy?>
    
    /**
     * Sauvegarde un article
     */
    suspend fun saveToBuy(toBuy: ToBuy)
    
    /**
     * Sauvegarde plusieurs articles
     */
    suspend fun saveToBuys(toBuys: List<ToBuy>)
    
    /**
     * Met à jour le statut d'un article (acheté ou non)
     */
    suspend fun updateToBuyStatus(toBuyId: String, isPurchased: Boolean)
    
    /**
     * Met à jour la priorité d'un article
     */
    suspend fun updateToBuyPriority(toBuyId: String, isPriority: Boolean)
    
    /**
     * Supprime un article par son ID
     */
    suspend fun deleteToBuy(toBuyId: String)
    
    /**
     * Supprime tous les articles associés à une party
     */
    suspend fun deleteToBuysByParty(partyId: String)
} 