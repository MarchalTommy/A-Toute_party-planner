package com.martodev.atoute.party.domain.usecase

import com.martodev.atoute.party.domain.repository.TodoRepository

/**
 * Cas d'utilisation pour mettre à jour la priorité d'une tâche
 */
class UpdateTodoPriorityUseCase(
    private val todoRepository: TodoRepository
) {
    /**
     * Met à jour la priorité d'une tâche
     * @param todoId L'identifiant de la tâche
     * @param isPriority Indique si la tâche est prioritaire
     */
    suspend operator fun invoke(todoId: String, isPriority: Boolean) {
        todoRepository.updateTodoPriority(todoId, isPriority)
    }
} 