package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.model.Todo
import com.martodev.atoute.home.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

/**
 * Cas d'utilisation pour récupérer les todos d'une party spécifique
 */
class GetTodosByPartyUseCase(private val todoRepository: TodoRepository) {
    
    /**
     * Récupère les todos d'une party spécifique
     * @param partyId L'identifiant de la party
     */
    operator fun invoke(partyId: String): Flow<List<Todo>> {
        return todoRepository.getTodosByParty(partyId)
    }
} 