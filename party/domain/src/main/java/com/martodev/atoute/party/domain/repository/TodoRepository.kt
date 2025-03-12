package com.martodev.atoute.party.domain.repository

import com.martodev.atoute.party.domain.model.Todo
import kotlinx.coroutines.flow.Flow

/**
 * Interface du repository pour les Todo dans la couche domaine
 */
interface TodoRepository {
    
    /**
     * Récupère toutes les tâches
     */
    fun getAllTodos(): Flow<List<Todo>>
    
    /**
     * Récupère toutes les tâches prioritaires
     */
    fun getPriorityTodos(): Flow<List<Todo>>
    
    /**
     * Récupère les tâches pour une party donnée
     */
    fun getTodosByParty(partyId: String): Flow<List<Todo>>
    
    /**
     * Récupère une tâche par son ID
     */
    suspend fun getTodoById(todoId: String): Todo?
    
    /**
     * Ajoute ou met à jour une tâche
     */
    suspend fun saveTodo(todo: Todo)
    
    /**
     * Ajoute ou met à jour plusieurs tâches
     */
    suspend fun saveTodos(todos: List<Todo>)
    
    /**
     * Met à jour le statut de complétion d'une tâche
     */
    suspend fun updateTodoStatus(todoId: String, isCompleted: Boolean)
    
    /**
     * Met à jour le statut de priorité d'une tâche
     */
    suspend fun updateTodoPriority(todoId: String, isPriority: Boolean)
    
    /**
     * Supprime une tâche
     */
    suspend fun deleteTodo(todoId: String)
    
    /**
     * Supprime toutes les tâches pour une party donnée
     */
    suspend fun deleteTodosByParty(partyId: String)
} 