package com.martodev.atoute.core.data.firebase.repository

import com.martodev.atoute.core.data.firebase.model.FirestoreTodo
import kotlinx.coroutines.flow.Flow

/**
 * Interface pour les opérations Firestore liées aux tâches
 */
interface FirestoreTodoRepository : FirestoreRepository<FirestoreTodo> {
    /**
     * Récupère toutes les tâches d'un événement
     * @param eventId ID de l'événement
     * @return Flow contenant la liste des tâches de l'événement
     */
    fun getTodosByEventId(eventId: String): Flow<List<FirestoreTodo>>
    
    /**
     * Récupère toutes les tâches assignées à un utilisateur
     * @param userId ID de l'utilisateur
     * @return Flow contenant la liste des tâches assignées à l'utilisateur
     */
    fun getTodosByUserId(userId: String): Flow<List<FirestoreTodo>>
    
    /**
     * Assigne une tâche à un utilisateur
     * @param todoId ID de la tâche
     * @param userId ID de l'utilisateur
     */
    suspend fun assignTodoToUser(todoId: String, userId: String)
    
    /**
     * Désassigne une tâche d'un utilisateur
     * @param todoId ID de la tâche
     */
    suspend fun unassignTodo(todoId: String)
    
    /**
     * Marque une tâche comme complétée
     * @param todoId ID de la tâche
     * @param isCompleted État de complétion
     */
    suspend fun setTodoCompleted(todoId: String, isCompleted: Boolean)
} 