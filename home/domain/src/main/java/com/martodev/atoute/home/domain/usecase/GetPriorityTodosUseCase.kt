package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.model.Todo
import com.martodev.atoute.home.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

/**
 * Cas d'utilisation pour récupérer les todos prioritaires
 */
class GetPriorityTodosUseCase(private val todoRepository: TodoRepository) {
    
    /**
     * Récupère les todos prioritaires
     */
    operator fun invoke(): Flow<List<Todo>> {
        return todoRepository.getPriorityTodos()
    }
} 