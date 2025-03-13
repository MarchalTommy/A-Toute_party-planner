package com.martodev.atoute.authentication.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Clé pour le DataStore des préférences utilisateur
 */
private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

/**
 * Gère les préférences utilisateur avec DataStore
 *
 * @property context Contexte Android
 */
class UserPreferencesDataStore(private val context: Context) {
    
    companion object {
        // Clés pour les préférences
        private val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
    }
    
    /**
     * Récupère l'ID de l'utilisateur actuel
     *
     * @return Flow contenant l'ID de l'utilisateur ou null
     */
    fun getCurrentUserId(): Flow<String?> {
        return context.userPreferencesDataStore.data.map { preferences ->
            preferences[CURRENT_USER_ID]
        }
    }
    
    /**
     * Enregistre l'ID de l'utilisateur actuel
     *
     * @param userId ID de l'utilisateur
     */
    suspend fun saveCurrentUserId(userId: String) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[CURRENT_USER_ID] = userId
        }
    }
    
    /**
     * Récupère le nom de l'utilisateur actuel
     *
     * @return Flow contenant le nom de l'utilisateur ou null
     */
    fun getCurrentUserName(): Flow<String?> {
        return context.userPreferencesDataStore.data.map { preferences ->
            preferences[USER_NAME]
        }
    }
    
    /**
     * Enregistre le nom de l'utilisateur actuel
     *
     * @param userName Nom de l'utilisateur
     */
    suspend fun saveCurrentUserName(userName: String) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[USER_NAME] = userName
        }
    }
    
    /**
     * Efface les données de l'utilisateur actuel
     */
    suspend fun clearCurrentUser() {
        context.userPreferencesDataStore.edit { preferences ->
            preferences.remove(CURRENT_USER_ID)
            preferences.remove(USER_NAME)
        }
    }
} 