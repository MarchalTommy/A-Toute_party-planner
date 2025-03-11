package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.model.Todo
import com.martodev.atoute.home.domain.repository.TodoRepository

/**
 * Use case pour sauvegarder une tâche
 */
class SaveTodoUseCase(private val todoRepository: TodoRepository) {

    /**
     * Sauvegarde une tâche
     * @param todo La tâche à sauvegarder
     */
    suspend operator fun invoke(todo: Todo) {
        todoRepository.saveTodo(todo)
    }
} 