package com.martodev.atoute.authentication.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.IOException

/**
 * Gère les préférences utilisateur avec DataStore
 *
 * @property context Contexte Android
 */
class UserPreferencesDataStore(private val context: Context) : IUserPreferencesDataStore {

    companion object {
        // Nom du fichier DataStore
        private const val PREFERENCES_NAME = "user_preferences"

        // Clés pour les préférences
        private val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
        private val LAST_LOGGED_IN_USER_ID_KEY = stringPreferencesKey("last_logged_in_user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
    }

    // Création sécurisée du DataStore qui respecte le cycle de vie de l'application 
    // et sera supprimé lors de la désinstallation
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = { context.preferencesDataStoreFile(PREFERENCES_NAME) },
        migrations = listOf(
            SharedPreferencesMigration(context, PREFERENCES_NAME)
        )
    )

    /**
     * Récupère l'ID de l'utilisateur actuel
     *
     * @return Flow contenant l'ID de l'utilisateur ou null
     */
    override fun getCurrentUserId(): Flow<String?> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[CURRENT_USER_ID]
            }
    }

    /**
     * Enregistre l'ID de l'utilisateur actuel
     *
     * @param userId ID de l'utilisateur
     */
    override suspend fun saveCurrentUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[CURRENT_USER_ID] = userId
        }
    }

    /**
     * Récupère l'ID de l'utilisateur précédent
     *
     * @return Flow contenant l'ID de l'utilisateur ou null
     */
    override fun getPreviousUserId(): Flow<String?> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[LAST_LOGGED_IN_USER_ID_KEY]
            }
    }

    /**
     * Enregistre l'ID de l'utilisateur précédent
     *
     * @param userId ID de l'utilisateur
     */
    override suspend fun savePreviousUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[LAST_LOGGED_IN_USER_ID_KEY] = userId
        }
    }

    /**
     * Récupère le nom de l'utilisateur actuel
     *
     * @return Flow contenant le nom de l'utilisateur ou null
     */
    override fun getCurrentUserName(): Flow<String?> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[USER_NAME]
            }
    }

    /**
     * Enregistre le nom de l'utilisateur actuel
     *
     * @param userName Nom de l'utilisateur
     */
    override suspend fun saveCurrentUserName(userName: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = userName
        }
    }

    /**
     * Efface les données de l'utilisateur actuel
     */
    override suspend fun clearCurrentUser() {
        dataStore.edit { preferences ->
            val id = preferences[CURRENT_USER_ID]
            preferences[LAST_LOGGED_IN_USER_ID_KEY] = id ?: ""
            preferences.remove(CURRENT_USER_ID)
            preferences.remove(USER_NAME)
        }
    }

    /**
     * Efface les données
     */
    override suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Récupère le nom d'utilisateur actuel de manière synchrone
     *
     * @return Le nom d'utilisateur ou une chaîne vide si non trouvé
     */
    override fun getCurrentUserNameSync(): String {
        try {
            val preferences = runBlocking { dataStore.data.first() }
            return preferences[USER_NAME] ?: ""
        } catch (e: Exception) {
            println("Erreur lors de la récupération synchrone du nom d'utilisateur: ${e.message}")
            return ""
        }
    }
} 