package com.martodev.atoute.authentication.data.datasource

import kotlinx.coroutines.flow.Flow

/**
 * Interface pour la gestion des préférences utilisateur avec DataStore
 */
interface IUserPreferencesDataStore {
    /**
     * Récupère l'ID de l'utilisateur actuel
     *
     * @return Flow contenant l'ID de l'utilisateur ou null
     */
    fun getCurrentUserId(): Flow<String?>
    
    /**
     * Enregistre l'ID de l'utilisateur actuel
     *
     * @param userId ID de l'utilisateur
     */
    suspend fun saveCurrentUserId(userId: String)
    
    /**
     * Récupère le nom de l'utilisateur actuel
     *
     * @return Flow contenant le nom de l'utilisateur ou null
     */
    fun getCurrentUserName(): Flow<String?>
    
    /**
     * Enregistre le nom de l'utilisateur actuel
     *
     * @param userName Nom de l'utilisateur
     */
    suspend fun saveCurrentUserName(userName: String)
    
    /**
     * Efface les données de l'utilisateur actuel
     */
    suspend fun clearCurrentUser()
} 