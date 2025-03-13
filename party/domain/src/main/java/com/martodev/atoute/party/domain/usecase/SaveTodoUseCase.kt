package com.martodev.atoute.party.domain.usecase

import com.martodev.atoute.party.domain.model.Todo
import com.martodev.atoute.party.domain.repository.TodoRepository

/**
 * Cas d'utilisation pour sauvegarder une tâche
 */
class SaveTodoUseCase(
    private val todoRepository: TodoRepository,
    private val checkPriorityTodoLimitUseCase: CheckPriorityTodoLimitUseCase
) {
    /**
     * Exception lancée lorsque l'utilisateur a atteint sa limite de tâches prioritaires
     */
    class PriorityTodoLimitReachedException(limit: Int) : Exception(
        "Vous avez atteint la limite de $limit tâches prioritaires pour un compte non-premium. " +
        "Passez à la version premium pour avoir des tâches prioritaires illimitées."
    )
    
    /**
     * Sauvegarde une tâche
     * @param todo La tâche à sauvegarder
     * @throws PriorityTodoLimitReachedException si l'utilisateur a atteint sa limite de tâches prioritaires
     */
    suspend operator fun invoke(todo: Todo) {
        // Vérifie si c'est une nouvelle tâche prioritaire
        val isNewTodo = todo.id.isBlank()
        
        // Si c'est une nouvelle tâche prioritaire, vérifier la limite
        if (isNewTodo && todo.isPriority) {
            val canCreatePriorityTodo = checkPriorityTodoLimitUseCase.checkSync()
            if (!canCreatePriorityTodo) {
                throw PriorityTodoLimitReachedException(CheckPriorityTodoLimitUseCase.NON_PREMIUM_PRIORITY_TODO_LIMIT)
            }
        }
        
        // Sauvegarder la tâche
        todoRepository.saveTodo(todo)
    }
} 