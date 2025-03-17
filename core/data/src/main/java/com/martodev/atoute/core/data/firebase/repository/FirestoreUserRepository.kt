package com.martodev.atoute.core.data.firebase.repository

import com.martodev.atoute.core.data.firebase.model.FirestoreUser
import kotlinx.coroutines.flow.Flow

/**
 * Interface pour les opérations Firestore liées aux utilisateurs
 */
interface FirestoreUserRepository : FirestoreRepository<FirestoreUser> {
    /**
     * Récupère l'utilisateur actuellement connecté
     * @return Flow contenant l'utilisateur connecté ou null s'il n'est pas connecté
     */
    fun getCurrentUser(): Flow<FirestoreUser?>
    
    /**
     * Récupère l'utilisateur actuellement connecté de manière synchrone
     * @return L'utilisateur connecté ou null s'il n'est pas connecté
     */
    suspend fun getCurrentUserSync(): FirestoreUser?
    
    /**
     * Ajoute un événement à un utilisateur
     * @param userId ID de l'utilisateur
     * @param eventId ID de l'événement
     * @param isCreator Indique si l'utilisateur est le créateur de l'événement
     */
    suspend fun addEventToUser(userId: String, eventId: String, isCreator: Boolean)
    
    /**
     * Supprime un événement d'un utilisateur
     * @param userId ID de l'utilisateur
     * @param eventId ID de l'événement
     */
    suspend fun removeEventFromUser(userId: String, eventId: String)
} 