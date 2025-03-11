package com.martodev.atoute.home.data.db

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.martodev.atoute.home.data.dao.ParticipantDao
import com.martodev.atoute.home.data.dao.PartyDao
import com.martodev.atoute.home.data.dao.ToBuyDao
import com.martodev.atoute.home.data.dao.TodoDao
import com.martodev.atoute.home.data.datasource.MockDataSource
import com.martodev.atoute.home.data.entity.ParticipantEntity
import com.martodev.atoute.home.data.entity.PartyEntity
import com.martodev.atoute.home.data.mapper.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Service responsable de l'initialisation de la base de données avec des données de test
 */
class DatabaseInitializer(
    private val context: Context,
    private val partyDao: PartyDao,
    private val todoDao: TodoDao,
    private val toBuyDao: ToBuyDao,
    private val participantDao: ParticipantDao,
    private val mockDataSource: MockDataSource
) {
    
    companion object {
        private const val PREFS_NAME = "database_prefs"
        private const val KEY_DB_INITIALIZED = "is_db_initialized"
    }
    
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Initialise la base de données avec les données mockées si ce n'est pas déjà fait
     */
    fun initializeDatabaseIfNeeded(scope: CoroutineScope) {
        val isInitialized = prefs.getBoolean(KEY_DB_INITIALIZED, false)
        
        if (!isInitialized) {
            Log.d("DatabaseInitializer", "Initialisation de la base de données avec les données de test")
            
            scope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        // Récupération des données mockées
                        val mockParties = mockDataSource.getParties()
                        val mockTodos = mockDataSource.getTodos()
                        val mockToBuys = mockDataSource.getToBuys()
                        
                        // Insertion des parties
                        mockParties.forEach { party ->
                            // Conversion en entité
                            val partyEntity = PartyEntity(
                                id = party.id,
                                title = party.title,
                                date = party.date,
                                location = party.location,
                                description = party.description,
                                color = party.color,
                                todoCount = party.todoCount,
                                completedTodoCount = party.completedTodoCount
                            )
                            
                            // Insertion de la partie
                            partyDao.insertParty(partyEntity)
                            
                            // Insertion des participants
                            party.participants.forEach { participantName ->
                                val participantEntity = ParticipantEntity(
                                    // Ne pas spécifier l'id car il est autogénéré
                                    name = participantName,
                                    partyId = party.id
                                )
                                participantDao.insertParticipant(participantEntity)
                            }
                        }
                        
                        // Insertion des todos
                        mockTodos.forEach { todo ->
                            todoDao.insertTodo(todo.toEntity())
                        }
                        
                        // Insertion des achats (toBuys)
                        mockToBuys.forEach { toBuy ->
                            toBuyDao.insertToBuy(toBuy.toEntity())
                        }
                        
                        // Marquer la base de données comme initialisée
                        prefs.edit().putBoolean(KEY_DB_INITIALIZED, true).apply()
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
    
    /**
     * Réinitialise la base de données avec les données mockées (utile pour le développement)
     */
    fun resetDatabase(scope: CoroutineScope, onComplete: () -> Unit = {}) {
        Log.d("DatabaseInitializer", "Réinitialisation de la base de données")
        
        scope.launch {
            withContext(Dispatchers.IO) {
                try {
                    // Récupérer les données actuelles puis les supprimer
                    // Utiliser first() pour obtenir la première émission du Flow
                    val todoEntities = todoDao.getAllTodos().first()
                    val toBuyEntities = toBuyDao.getAllToBuys().first()
                    val partyEntities = partyDao.getAllParties().first()
                    
                    // Supprimer tous les éléments un par un
                    todoEntities.forEach { todoDao.deleteTodo(it) }
                    toBuyEntities.forEach { toBuyDao.deleteToBuy(it) }
                    
                    // On supprime les parties en dernier car les contraintes de clés étrangères
                    // supprimeront automatiquement les todos et toBuys associés
                    partyEntities.forEach { partyDao.deleteParty(it) }
                    
                    // Réinitialisation du flag
                    prefs.edit().putBoolean(KEY_DB_INITIALIZED, false).apply()
                    
                    // Réinitialisation avec les nouvelles données
                    initializeDatabaseIfNeeded(scope)
                    
                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                } catch (e: Exception) {
                    Log.e("DatabaseInitializer", "Erreur lors de la réinitialisation de la base de données", e)
                }
            }
        }
    }
} 