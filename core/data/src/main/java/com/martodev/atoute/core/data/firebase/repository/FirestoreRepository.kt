package com.martodev.atoute.core.data.firebase.repository

import kotlinx.coroutines.flow.Flow

/**
 * Interface générique pour les opérations Firestore
 */
interface FirestoreRepository<T> {
    /**
     * Récupère un document par son ID
     * @param id ID du document
     * @return Flow contenant le document ou null s'il n'existe pas
     */
    fun getDocumentById(id: String): Flow<T?>
    
    /**
     * Récupère un document par son ID de manière synchrone
     * @param id ID du document
     * @return Le document ou null s'il n'existe pas
     */
    suspend fun getDocumentByIdSync(id: String): T?
    
    /**
     * Ajoute ou met à jour un document
     * @param document Document à ajouter ou mettre à jour
     * @return ID du document
     */
    suspend fun saveDocument(document: T): String
    
    /**
     * Supprime un document
     * @param id ID du document à supprimer
     */
    suspend fun deleteDocument(id: String)
} 