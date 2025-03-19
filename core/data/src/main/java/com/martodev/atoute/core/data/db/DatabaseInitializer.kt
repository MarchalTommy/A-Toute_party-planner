package com.martodev.atoute.core.data.db

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.martodev.atoute.core.data.dao.ParticipantDao
import com.martodev.atoute.core.data.dao.PartyDao
import com.martodev.atoute.core.data.dao.ToBuyDao
import com.martodev.atoute.core.data.dao.TodoDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Service responsable de l'initialisation de la base de données
 */
class DatabaseInitializer(
    private val context: Context,
    private val partyDao: PartyDao,
    private val todoDao: TodoDao,
    private val toBuyDao: ToBuyDao,
    private val participantDao: ParticipantDao
) {
    
    companion object {
        private const val PREFS_NAME = "database_prefs"
        private const val KEY_DB_INITIALIZED = "is_db_initialized"
    }
    
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Initialise la base de données si ce n'est pas déjà fait
     */
    fun initializeDatabaseIfNeeded(scope: CoroutineScope) {
        val isInitialized = prefs.getBoolean(KEY_DB_INITIALIZED, false)
        
        if (!isInitialized) {
            Log.d("DatabaseInitializer", "Initialisation de la base de données")
            
            scope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        // Marquer la base de données comme initialisée
                        prefs.edit { putBoolean(KEY_DB_INITIALIZED, true) }
                        Log.d("DatabaseInitializer", "Base de données initialisée avec succès")
                    } catch (e: Exception) {
                        Log.e("DatabaseInitializer", "Erreur lors de l'initialisation de la base de données", e)
                    }
                }
            }
        } else {
            Log.d("DatabaseInitializer", "Base de données déjà initialisée")
        }
    }
} 