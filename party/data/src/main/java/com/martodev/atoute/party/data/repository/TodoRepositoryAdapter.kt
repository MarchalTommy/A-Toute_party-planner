package com.martodev.atoute.party.data.repository

import com.martodev.atoute.home.domain.repository.TodoRepository as HomeTodoRepository
import com.martodev.atoute.party.domain.model.Todo
import com.martodev.atoute.party.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Adaptateur pour le repository TodoRepository du module Home
 * Permet d'utiliser l'implémentation du module Home avec l'interface du module Party
 */
class TodoRepositoryAdapter(
    private val homeRepository: HomeTodoRepository
) : TodoRepository {

    override fun getAllTodos(): Flow<List<Todo>> {
        return homeRepository.getAllTodos().map { homeTodos ->
            homeTodos.map { homeTodo ->
                Todo(
                    id = homeTodo.id,
                    title = homeTodo.title,
                    isCompleted = homeTodo.isCompleted,
                    assignedTo = homeTodo.assignedTo,
                    partyId = homeTodo.partyId,
                    partyColor = homeTodo.partyColor,
                    isPriority = homeTodo.isPriority
                )
            }
        }
    }

    override fun getTodosByParty(partyId: String): Flow<List<Todo>> {
        return homeRepository.getTodosByParty(partyId).map { homeTodos ->
            homeTodos.map { homeTodo ->
                Todo(
                    id = homeTodo.id,
                    title = homeTodo.title,
                    isCompleted = homeTodo.isCompleted,
                    assignedTo = homeTodo.assignedTo,
                    partyId = homeTodo.partyId,
                    partyColor = homeTodo.partyColor,
                    isPriority = homeTodo.isPriority
                )
            }
        }
    }

    override fun getPriorityTodos(): Flow<List<Todo>> {
        return homeRepository.getPriorityTodos().map { homeTodos ->
            homeTodos.map { homeTodo ->
                Todo(
                    id = homeTodo.id,
                    title = homeTodo.title,
                    isCompleted = homeTodo.isCompleted,
                    assignedTo = homeTodo.assignedTo,
                    partyId = homeTodo.partyId,
                    partyColor = homeTodo.partyColor,
                    isPriority = homeTodo.isPriority
                )
            }
        }
    }

    override suspend fun getTodoById(todoId: String): Todo? {
        return homeRepository.getTodoById(todoId)?.let {
            Todo(
                id = it.id,
                title = it.title,
                isCompleted = it.isCompleted,
                assignedTo = it.assignedTo,
                partyId = it.partyId,
                partyColor = it.partyColor,
                isPriority = it.isPriority
            )
        }
    }

    override suspend fun saveTodo(todo: Todo) {
        homeRepository.saveTodo(
            com.martodev.atoute.home.domain.model.Todo(
                id = todo.id,
                title = todo.title,
                isCompleted = todo.isCompleted,
                assignedTo = todo.assignedTo,
                partyId = todo.partyId,
                partyColor = todo.partyColor,
                isPriority = todo.isPriority
            )
        )
    }

    override suspend fun saveTodos(todos: List<Todo>) {
        homeRepository.saveTodos(
            todos.map {
                com.martodev.atoute.home.domain.model.Todo(
                    id = it.id,
                    title = it.title,
                    isCompleted = it.isCompleted,
                    assignedTo = it.assignedTo,
                    partyId = it.partyId,
                    partyColor = it.partyColor,
                    isPriority = it.isPriority
                )
            }
        )
    }

    override suspend fun updateTodoStatus(todoId: String, isCompleted: Boolean) {
        homeRepository.updateTodoStatus(todoId, isCompleted)
    }

    override suspend fun updateTodoPriority(todoId: String, isPriority: Boolean) {
        homeRepository.updateTodoPriority(todoId, isPriority)
    }

    override suspend fun deleteTodo(todoId: String) {
        homeRepository.deleteTodo(todoId)
    }

    override suspend fun deleteTodosByParty(partyId: String) {
        homeRepository.deleteTodosByParty(partyId)
    }
} 