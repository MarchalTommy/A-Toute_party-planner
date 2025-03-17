package com.martodev.atoute.core.data.firebase.repository

import com.martodev.atoute.core.data.firebase.model.FirestoreToBuy
import kotlinx.coroutines.flow.Flow

/**
 * Interface pour les opérations Firestore liées aux articles à acheter
 */
interface FirestoreToBuyRepository : FirestoreRepository<FirestoreToBuy> {
    /**
     * Récupère tous les articles à acheter d'un événement
     * @param eventId ID de l'événement
     * @return Flow contenant la liste des articles à acheter de l'événement
     */
    fun getToBuysByEventId(eventId: String): Flow<List<FirestoreToBuy>>
    
    /**
     * Récupère tous les articles à acheter assignés à un utilisateur
     * @param userId ID de l'utilisateur
     * @return Flow contenant la liste des articles à acheter assignés à l'utilisateur
     */
    fun getToBuysByUserId(userId: String): Flow<List<FirestoreToBuy>>
    
    /**
     * Assigne un article à acheter à un utilisateur
     * @param toBuyId ID de l'article
     * @param userId ID de l'utilisateur
     */
    suspend fun assignToBuyToUser(toBuyId: String, userId: String)
    
    /**
     * Désassigne un article à acheter d'un utilisateur
     * @param toBuyId ID de l'article
     */
    suspend fun unassignToBuy(toBuyId: String)
    
    /**
     * Marque un article à acheter comme acheté
     * @param toBuyId ID de l'article
     * @param isPurchased État d'achat
     */
    suspend fun setToBuyPurchased(toBuyId: String, isPurchased: Boolean)
    
    /**
     * Met à jour la quantité d'un article à acheter
     * @param toBuyId ID de l'article
     * @param quantity Nouvelle quantité
     */
    suspend fun updateToBuyQuantity(toBuyId: String, quantity: Int)
} 