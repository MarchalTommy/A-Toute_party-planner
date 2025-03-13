package com.martodev.atoute.party.domain.usecase

import com.martodev.atoute.party.domain.repository.TodoRepository

/**
 * Cas d'utilisation pour mettre à jour le statut de priorité d'une tâche
 */
class UpdateTodoPriorityUseCase(
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
     * Met à jour le statut de priorité d'une tâche
     * @param todoId ID de la tâche
     * @param isPriority Nouveau statut de priorité
     * @throws PriorityTodoLimitReachedException si l'utilisateur a atteint sa limite de tâches prioritaires
     */
    suspend operator fun invoke(todoId: String, isPriority: Boolean) {
        // Ne vérifier la limite que si on essaie de mettre une tâche en prioritaire
        if (isPriority) {
            val canCreatePriorityTodo = checkPriorityTodoLimitUseCase.checkSync()
            if (!canCreatePriorityTodo) {
                throw PriorityTodoLimitReachedException(CheckPriorityTodoLimitUseCase.NON_PREMIUM_PRIORITY_TODO_LIMIT)
            }
        }
        
        // Mettre à jour le statut de priorité
        todoRepository.updateTodoPriority(todoId, isPriority)
    }
} 