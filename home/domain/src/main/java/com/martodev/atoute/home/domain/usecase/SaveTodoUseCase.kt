package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.model.Todo
import com.martodev.atoute.home.domain.repository.TodoRepository

/**
 * Cas d'utilisation pour sauvegarder une tâche todo
 */
class SaveTodoUseCase(
    private val todoRepository: TodoRepository
) {
    /**
     * Sauvegarde une tâche todo
     * @param todo La tâche à sauvegarder
     */
    suspend operator fun invoke(todo: Todo) {
        todoRepository.saveTodo(todo)
    }
} 