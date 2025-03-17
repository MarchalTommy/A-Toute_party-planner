package com.martodev.atoute.core.data.firebase.repository

import com.martodev.atoute.core.data.firebase.model.FirestoreEvent
import kotlinx.coroutines.flow.Flow

/**
 * Interface pour les opérations Firestore liées aux événements
 */
interface FirestoreEventRepository : FirestoreRepository<FirestoreEvent> {
    /**
     * Récupère tous les événements d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Flow contenant la liste des événements de l'utilisateur
     */
    fun getEventsByUserId(userId: String): Flow<List<FirestoreEvent>>
    
    /**
     * Récupère tous les événements de l'utilisateur actuellement connecté
     * @return Flow contenant la liste des événements de l'utilisateur connecté
     */
    fun getCurrentUserEvents(): Flow<List<FirestoreEvent>>
    
    /**
     * Ajoute un participant à un événement
     * @param eventId ID de l'événement
     * @param userId ID de l'utilisateur à ajouter
     * @param isCreator Indique si l'utilisateur est le créateur de l'événement
     */
    suspend fun addParticipantToEvent(eventId: String, userId: String, isCreator: Boolean)
    
    /**
     * Supprime un participant d'un événement
     * @param eventId ID de l'événement
     * @param userId ID de l'utilisateur à supprimer
     */
    suspend fun removeParticipantFromEvent(eventId: String, userId: String)
    
    /**
     * Ajoute une tâche à un événement
     * @param eventId ID de l'événement
     * @param todoId ID de la tâche à ajouter
     */
    suspend fun addTodoToEvent(eventId: String, todoId: String)
    
    /**
     * Supprime une tâche d'un événement
     * @param eventId ID de l'événement
     * @param todoId ID de la tâche à supprimer
     */
    suspend fun removeTodoFromEvent(eventId: String, todoId: String)
    
    /**
     * Ajoute un article à acheter à un événement
     * @param eventId ID de l'événement
     * @param toBuyId ID de l'article à ajouter
     */
    suspend fun addToBuyToEvent(eventId: String, toBuyId: String)
    
    /**
     * Supprime un article à acheter d'un événement
     * @param eventId ID de l'événement
     * @param toBuyId ID de l'article à supprimer
     */
    suspend fun removeToBuyFromEvent(eventId: String, toBuyId: String)
} 