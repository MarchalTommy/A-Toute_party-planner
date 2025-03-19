package com.martodev.atoute.core.data.firebase.repository

import com.martodev.atoute.core.data.firebase.model.FirestoreUser
import kotlinx.coroutines.flow.Flow

/**
 * Interface du repository Firestore pour les utilisateurs
 */
interface FirestoreUserRepository {
    /**
     * Récupère un document par son ID de manière synchrone
     */
    suspend fun getDocumentByIdSync(id: String): FirestoreUser?

    /**
     * Sauvegarde un document
     */
    suspend fun saveDocument(document: FirestoreUser): String

    /**
     * Supprime un document
     */
    suspend fun deleteDocument(id: String)

    /**
     * Ajoute un événement à un utilisateur
     */
    suspend fun addEventToUser(userId: String, eventId: String, isCreator: Boolean)

    /**
     * Retire un événement d'un utilisateur
     */
    suspend fun removeEventFromUser(userId: String, eventId: String)

    /**
     * Récupère un document par son ID de manière asynchrone
     */
    fun getDocumentById(id: String): Flow<FirestoreUser?>
} 