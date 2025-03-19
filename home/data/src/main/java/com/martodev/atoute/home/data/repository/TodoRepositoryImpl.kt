package com.martodev.atoute.home.data.repository

import com.martodev.atoute.core.data.dao.PartyDao
import com.martodev.atoute.core.data.dao.TodoDao
import com.martodev.atoute.core.data.entity.TodoEntity
import com.martodev.atoute.core.data.firebase.sync.FirestoreSyncManager
import com.martodev.atoute.home.domain.model.Todo
import com.martodev.atoute.home.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TodoRepositoryImpl(
    private val todoDao: TodoDao,
    private val partyDao: PartyDao,
    private val syncManager: FirestoreSyncManager
) : TodoRepository {

    override fun getAllTodos(): Flow<List<Todo>> {
        return todoDao.getAllTodos().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getPriorityTodos(): Flow<List<Todo>> {
        return todoDao.getPriorityTodos().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getTodosByParty(partyId: String): Flow<List<Todo>> {
        return todoDao.getTodosByPartyId(partyId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getTodoById(todoId: String): Todo? {
        return todoDao.getTodoById(todoId)?.toDomainModel()
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
    
    // Mappers
    private fun TodoEntity.toDomainModel(): Todo {
        return Todo(
            id = id,
            title = title,
            isCompleted = isCompleted,
            assignedTo = assignedTo,
            partyId = partyId,
            partyColor = null, // On ne récupère pas la couleur pour simplifier
            isPriority = isPriority
        )
    }
    
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