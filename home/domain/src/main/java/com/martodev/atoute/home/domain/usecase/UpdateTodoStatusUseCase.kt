package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.repository.TodoRepository

/**
 * Cas d'utilisation pour mettre à jour le statut d'un todo
 */
class UpdateTodoStatusUseCase(private val todoRepository: TodoRepository) {
    
    /**
     * Met à jour le statut d'un todo
     * @param todoId L'identifiant du todo
     * @param isCompleted La nouvelle valeur de complétion
     */
    suspend operator fun invoke(todoId: String, isCompleted: Boolean) {
        todoRepository.updateTodoStatus(todoId, isCompleted)
    }
} 