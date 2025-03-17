package com.martodev.atoute.core.data.firebase.sync

import kotlinx.coroutines.flow.Flow

/**
 * Interface pour la gestion de la synchronisation entre Firestore et Room
 */
interface FirestoreSyncManager {
    /**
     * Synchronise les données locales avec Firestore
     * @return Flow indiquant l'état de la synchronisation
     */
    fun syncData(): Flow<SyncState>
    
    /**
     * Synchronise un événement spécifique
     * @param eventId ID de l'événement à synchroniser
     * @return État de la synchronisation
     */
    suspend fun syncEvent(eventId: String): SyncState
    
    /**
     * Synchronise les tâches d'un événement
     * @param eventId ID de l'événement
     * @return État de la synchronisation
     */
    suspend fun syncEventTodos(eventId: String): SyncState
    
    /**
     * Synchronise les articles à acheter d'un événement
     * @param eventId ID de l'événement
     * @return État de la synchronisation
     */
    suspend fun syncEventToBuys(eventId: String): SyncState
    
    /**
     * Envoie les modifications locales vers Firestore
     * @return État de la synchronisation
     */
    suspend fun pushLocalChanges(): SyncState
}

/**
 * État de la synchronisation
 */
sealed class SyncState {
    /**
     * Synchronisation en cours
     */
    object Loading : SyncState()
    
    /**
     * Synchronisation réussie
     * @property updatedItems Nombre d'éléments mis à jour
     */
    data class Success(val updatedItems: Int) : SyncState()
    
    /**
     * Erreur lors de la synchronisation
     * @property message Message d'erreur
     * @property exception Exception
     */
    data class Error(val message: String, val exception: Exception? = null) : SyncState()
} 