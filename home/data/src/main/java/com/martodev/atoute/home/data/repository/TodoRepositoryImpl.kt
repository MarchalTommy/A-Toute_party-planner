package com.martodev.atoute.home.data.repository

import com.martodev.atoute.home.data.dao.PartyDao
import com.martodev.atoute.home.data.dao.TodoDao
import com.martodev.atoute.home.data.datasource.MockDataSource
import com.martodev.atoute.home.data.mapper.toEntity
import com.martodev.atoute.home.data.mapper.toDomain
import com.martodev.atoute.home.domain.model.Todo
import com.martodev.atoute.home.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class TodoRepositoryImpl(
    private val todoDao: TodoDao,
    private val partyDao: PartyDao,
    private val mockDataSource: MockDataSource
) : TodoRepository {

    // Mode mock pour le développement
    private val useMockData = false
    
    override fun getAllTodos(): Flow<List<Todo>> {
        return if (useMockData) {
            flow { emit(mockDataSource.getTodos()) }
        } else {
            todoDao.getAllTodos().map { todoEntities ->
                todoEntities.map { it.toDomain() }
            }
        }
    }
    
    override fun getPriorityTodos(): Flow<List<Todo>> {
        return if (useMockData) {
            flow { emit(mockDataSource.getTodos().filter { it.isPriority }) }
        } else {
            todoDao.getPriorityTodos().map { todoEntities ->
                todoEntities.map { it.toDomain() }
            }
        }
    }
    
    override fun getTodosByParty(partyId: String): Flow<List<Todo>> {
        return if (useMockData) {
            flow { emit(mockDataSource.getTodos().filter { it.partyId == partyId }) }
        } else {
            todoDao.getTodosByPartyId(partyId).map { todoEntities ->
                todoEntities.map { it.toDomain() }
            }
        }
    }
    
    override suspend fun getTodoById(todoId: String): Todo? {
        return if (useMockData) {
            mockDataSource.getTodos().find { it.id == todoId }
        } else {
            todoDao.getTodoById(todoId)?.toDomain()
        }
    }
    
    override suspend fun saveTodo(todo: Todo) {
        if (!useMockData) {
            todoDao.insertTodo(todo.toEntity())
            
            // Mettre à jour les compteurs de la Party
            updatePartyTodoCounters(todo.partyId)
        }
    }
    
    override suspend fun saveTodos(todos: List<Todo>) {
        if (!useMockData) {
            todos.forEach { saveTodo(it) }
        }
    }
    
    override suspend fun updateTodoStatus(todoId: String, isCompleted: Boolean) {
        if (!useMockData) {
            val todo = todoDao.getTodoById(todoId) ?: return
            todoDao.updateTodoStatus(todoId, isCompleted)
            
            // Mettre à jour les compteurs de la Party
            updatePartyTodoCounters(todo.partyId)
        }
    }
    
    override suspend fun updateTodoPriority(todoId: String, isPriority: Boolean) {
        if (!useMockData) {
            todoDao.updateTodoPriority(todoId, isPriority)
        }
    }
    
    override suspend fun deleteTodo(todoId: String) {
        if (!useMockData) {
            val todo = todoDao.getTodoById(todoId) ?: return
            todoDao.deleteTodoById(todoId)
            
            // Mettre à jour les compteurs de la Party
            updatePartyTodoCounters(todo.partyId)
        }
    }
    
    override suspend fun deleteTodosByParty(partyId: String) {
        if (!useMockData) {
            todoDao.deleteTodosByPartyId(partyId)
        }
    }
    
    private suspend fun updatePartyTodoCounters(partyId: String) {
        if (!useMockData) {
            val todos = todoDao.getTodosByPartyIdSync(partyId)
            val todoCount = todos.size
            val completedTodoCount = todos.count { it.isCompleted }
            partyDao.updateTodoCounts(partyId, todoCount, completedTodoCount)
        }
    }
} 