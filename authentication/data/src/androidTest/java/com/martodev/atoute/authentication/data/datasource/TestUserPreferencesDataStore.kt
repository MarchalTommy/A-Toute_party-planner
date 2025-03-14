package com.martodev.atoute.authentication.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Version de UserPreferencesDataStore pour les tests
 * Implémente l'interface et utilise un DataStore personnalisé pour les tests
 */
class TestUserPreferencesDataStore(
    private val testDataStore: DataStore<Preferences>
) : IUserPreferencesDataStore {
    
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
    override fun getCurrentUserId(): Flow<String?> {
        return testDataStore.data.map { preferences ->
            preferences[CURRENT_USER_ID]
        }
    }
    
    /**
     * Enregistre l'ID de l'utilisateur actuel
     *
     * @param userId ID de l'utilisateur
     */
    override suspend fun saveCurrentUserId(userId: String) {
        testDataStore.edit { preferences ->
            preferences[CURRENT_USER_ID] = userId
        }
    }
    
    /**
     * Récupère le nom de l'utilisateur actuel
     *
     * @return Flow contenant le nom de l'utilisateur ou null
     */
    override fun getCurrentUserName(): Flow<String?> {
        return testDataStore.data.map { preferences ->
            preferences[USER_NAME]
        }
    }
    
    /**
     * Enregistre le nom de l'utilisateur actuel
     *
     * @param userName Nom de l'utilisateur
     */
    override suspend fun saveCurrentUserName(userName: String) {
        testDataStore.edit { preferences ->
            preferences[USER_NAME] = userName
        }
    }
    
    /**
     * Efface les données de l'utilisateur actuel
     */
    override suspend fun clearCurrentUser() {
        testDataStore.edit { preferences ->
            preferences.remove(CURRENT_USER_ID)
            preferences.remove(USER_NAME)
        }
    }
} 