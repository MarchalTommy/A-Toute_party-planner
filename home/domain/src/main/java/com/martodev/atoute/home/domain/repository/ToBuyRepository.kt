package com.martodev.atoute.home.domain.repository

import com.martodev.atoute.home.domain.model.ToBuy
import kotlinx.coroutines.flow.Flow

/**
 * Interface du repository pour les ToBuy dans la couche domaine
 */
interface ToBuyRepository {
    
    /**
     * Récupère tous les articles à acheter
     */
    fun getAllToBuys(): Flow<List<ToBuy>>
    
    /**
     * Récupère tous les articles à acheter prioritaires
     */
    fun getPriorityToBuys(): Flow<List<ToBuy>>
    
    /**
     * Récupère les articles à acheter pour une party donnée
     */
    fun getToBuysByParty(partyId: String): Flow<List<ToBuy>>
    
    /**
     * Récupère un article à acheter par son ID
     */
    suspend fun getToBuyById(toBuyId: String): ToBuy?
    
    /**
     * Ajoute ou met à jour un article à acheter
     */
    suspend fun saveToBuy(toBuy: ToBuy)
    
    /**
     * Ajoute ou met à jour plusieurs articles à acheter
     */
    suspend fun saveToBuys(toBuys: List<ToBuy>)
    
    /**
     * Met à jour le statut d'achat d'un article
     */
    suspend fun updateToBuyStatus(toBuyId: String, isPurchased: Boolean)
    
    /**
     * Met à jour le statut de priorité d'un article
     */
    suspend fun updateToBuyPriority(toBuyId: String, isPriority: Boolean)
    
    /**
     * Supprime un article à acheter
     */
    suspend fun deleteToBuy(toBuyId: String)
    
    /**
     * Supprime tous les articles à acheter pour une party donnée
     */
    suspend fun deleteToBuysByParty(partyId: String)
} 