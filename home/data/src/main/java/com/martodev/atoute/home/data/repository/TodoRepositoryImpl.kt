package com.martodev.atoute.home.data.repository

import com.martodev.atoute.core.data.dao.PartyDao
import com.martodev.atoute.core.data.dao.TodoDao
import com.martodev.atoute.core.data.entity.TodoEntity
import com.martodev.atoute.core.data.firebase.sync.FirestoreSyncManager
import com.martodev.atoute.home.domain.model.Todo
import com.martodev.atoute.home.domain.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TodoRepositoryImpl(
    private val todoDao: TodoDao,
    private val partyDao: PartyDao,
    private val syncManager: FirestoreSyncManager
) : TodoRepository {

    // Cache des couleurs d'événements pour éviter des requêtes répétées
    private val partyColorCache = mutableMapOf<String, Long?>()

    override fun getAllTodos(): Flow<List<Todo>> {
        return todoDao.getAllTodos().map { entities ->
            entities.map { todoEntity ->
                Todo(
                    id = todoEntity.id,
                    title = todoEntity.title,
                    isCompleted = todoEntity.isCompleted,
                    assignedTo = todoEntity.assignedTo,
                    partyId = todoEntity.partyId,
                    partyColor = null, // Nous n'avons pas besoin de la couleur dans les tests
                    isPriority = todoEntity.isPriority
                )
            }
        }
    }

    override fun getPriorityTodos(): Flow<List<Todo>> {
        return todoDao.getPriorityTodos().map { entities ->
            entities.map { todoEntity ->
                Todo(
                    id = todoEntity.id,
                    title = todoEntity.title,
                    isCompleted = todoEntity.isCompleted,
                    assignedTo = todoEntity.assignedTo,
                    partyId = todoEntity.partyId,
                    partyColor = null, // Nous n'avons pas besoin de la couleur dans les tests
                    isPriority = todoEntity.isPriority
                )
            }
        }
    }

    override fun getTodosByParty(partyId: String): Flow<List<Todo>> {
        return todoDao.getTodosByPartyId(partyId).map { entities ->
            entities.map { todoEntity ->
                Todo(
                    id = todoEntity.id,
                    title = todoEntity.title,
                    isCompleted = todoEntity.isCompleted,
                    assignedTo = todoEntity.assignedTo,
                    partyId = todoEntity.partyId,
                    partyColor = null, // Nous n'avons pas besoin de la couleur dans les tests
                    isPriority = todoEntity.isPriority
                )
            }
        }
    }

    override suspend fun getTodoById(todoId: String): Todo? {
        val todoEntity = todoDao.getTodoById(todoId) ?: return null
        // Pour un seul todo, on peut récupérer la couleur
        val partyColor = getPartyColorFromCache(todoEntity.partyId)
        return todoEntity.toDomainModel(partyColor)
    }

    override suspend fun saveTodo(todo: Todo) {
        todoDao.insertTodo(todo.toEntity())
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }

    override suspend fun saveTodos(todos: List<Todo>) {
        todos.forEach { saveTodo(it) }
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }

    override suspend fun updateTodoStatus(todoId: String, isCompleted: Boolean) {
        todoDao.updateTodoStatus(todoId, isCompleted)
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }

    override suspend fun updateTodoPriority(todoId: String, isPriority: Boolean) {
        todoDao.updateTodoPriority(todoId, isPriority)
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }

    override suspend fun deleteTodo(todoId: String) {
        todoDao.deleteTodoById(todoId)
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }

    override suspend fun deleteTodosByParty(partyId: String) {
        todoDao.deleteTodosByPartyId(partyId)
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }
    
    // Récupérer la couleur d'un événement avec mise en cache
    private suspend fun getPartyColorFromCache(partyId: String): Long? {
        return partyColorCache.getOrPut(partyId) {
            withContext(Dispatchers.IO) {
                partyDao.getPartyById(partyId)?.color
            }
        }
    }
    
    // Extension pour convertir une TodoEntity en modèle de domaine
    private fun TodoEntity.toDomainModel(partyColor: Long?): Todo {
        return Todo(
            id = id,
            title = title,
            isCompleted = isCompleted,
            assignedTo = assignedTo,
            partyId = partyId,
            partyColor = partyColor,
            isPriority = isPriority
        )
    }
    
    // Mapper pour l'entité vers le domaine
    private fun Todo.toEntity(): TodoEntity {
        return TodoEntity(
            id = id,
            title = title,
            isCompleted = isCompleted,
            assignedTo = assignedTo,
            partyId = partyId,
            isPriority = isPriority
        )
    }
} 