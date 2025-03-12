package com.martodev.atoute.party.domain.usecase

import com.martodev.atoute.party.domain.model.Todo
import com.martodev.atoute.party.domain.repository.TodoRepository

/**
 * Cas d'utilisation pour sauvegarder une tâche
 */
class SaveTodoUseCase(
    private val todoRepository: TodoRepository
) {
    /**
     * Sauvegarde une tâche
     * @param todo La tâche à sauvegarder
     */
    suspend operator fun invoke(todo: Todo) {
        todoRepository.saveTodo(todo)
    }
} 