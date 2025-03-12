package com.martodev.atoute.party.domain.usecase

import com.martodev.atoute.party.domain.repository.TodoRepository

/**
 * Cas d'utilisation pour mettre à jour le statut d'une tâche (complétée ou non)
 */
class UpdateTodoStatusUseCase(
    private val todoRepository: TodoRepository
) {
    /**
     * Met à jour le statut d'une tâche
     * @param todoId L'identifiant de la tâche
     * @param isCompleted Le nouveau statut de la tâche
     */
    suspend operator fun invoke(todoId: String, isCompleted: Boolean) {
        todoRepository.updateTodoStatus(todoId, isCompleted)
    }
} 