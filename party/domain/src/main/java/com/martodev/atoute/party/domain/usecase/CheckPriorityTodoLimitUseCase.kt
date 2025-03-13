package com.martodev.atoute.party.domain.usecase

import com.martodev.atoute.authentication.domain.usecase.GetCurrentUserPremiumStatusUseCase
import com.martodev.atoute.party.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Cas d'utilisation pour vérifier si l'utilisateur a atteint la limite de tâches prioritaires
 * pour un utilisateur non-premium
 * 
 * Les utilisateurs non-premium sont limités à 4 tâches prioritaires au total.
 */
class CheckPriorityTodoLimitUseCase(
    private val todoRepository: TodoRepository,
    private val isPremiumUseCase: GetCurrentUserPremiumStatusUseCase
) {
    companion object {
        const val NON_PREMIUM_PRIORITY_TODO_LIMIT = 4
    }
    
    /**
     * Vérifie si l'utilisateur peut ajouter une nouvelle tâche prioritaire
     * 
     * @return Flow contenant un Boolean indiquant si l'utilisateur peut ajouter une nouvelle tâche prioritaire
     *         true: l'utilisateur peut ajouter une nouvelle tâche prioritaire
     *         false: l'utilisateur a atteint sa limite
     */
    operator fun invoke(): Flow<Boolean> {
        val priorityTodos = todoRepository.getPriorityTodos().map { todos -> 
            todos.count { it.isPriority }
        }
        
        return priorityTodos.combine(isPremiumUseCase()) { count, isPremium ->
            // Les utilisateurs premium n'ont pas de limite
            if (isPremium) {
                return@combine true
            }
            
            // Pour les utilisateurs non-premium, vérifier la limite
            count < NON_PREMIUM_PRIORITY_TODO_LIMIT
        }
    }
    
    /**
     * Vérifie de manière synchrone si l'utilisateur peut ajouter une nouvelle tâche prioritaire
     * 
     * @return Boolean indiquant si l'utilisateur peut ajouter une nouvelle tâche prioritaire
     *         true: l'utilisateur peut ajouter une nouvelle tâche prioritaire
     *         false: l'utilisateur a atteint sa limite
     */
    suspend fun checkSync(): Boolean {
        val isPremium = isPremiumUseCase().first()
        
        // Les utilisateurs premium n'ont pas de limite
        if (isPremium) {
            return true
        }
        
        // Pour les utilisateurs non-premium, vérifier la limite
        val todos = todoRepository.getPriorityTodos().first()
        val priorityCount = todos.count { it.isPriority }
        return priorityCount < NON_PREMIUM_PRIORITY_TODO_LIMIT
    }
} 