package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.repository.TodoRepository

/**
 * Cas d'utilisation pour mettre à jour la priorité d'un todo
 */
class UpdateTodoPriorityUseCase(private val todoRepository: TodoRepository) {
    
    /**
     * Met à jour la priorité d'un todo
     * @param todoId L'identifiant du todo
     * @param isPriority La nouvelle valeur de priorité
     */
    suspend operator fun invoke(todoId: String, isPriority: Boolean) {
        todoRepository.updateTodoPriority(todoId, isPriority)
    }
} 