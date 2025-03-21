package com.martodev.atoute.core.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.martodev.atoute.core.data.entity.TodoEntity
import com.martodev.atoute.core.data.entity.TodoWithPartyColor
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour accéder aux entités Todo dans la base de données
 */
@Dao
interface TodoDao {
    
    @Query("SELECT * FROM todos")
    fun getAllTodos(): Flow<List<TodoEntity>>
    
    /**
     * Récupère tous les todos avec leur couleur d'événement associé
     */
    @Query("SELECT t.*, p.color as partyColor FROM todos t LEFT JOIN parties p ON t.partyId = p.id")
    fun getAllTodosWithPartyColor(): Flow<List<TodoWithPartyColor>>
    
    /**
     * Récupère les todos prioritaires avec leur couleur d'événement associé
     */
    @Query("SELECT t.*, p.color as partyColor FROM todos t LEFT JOIN parties p ON t.partyId = p.id WHERE t.isPriority = 1")
    fun getPriorityTodosWithPartyColor(): Flow<List<TodoWithPartyColor>>
    
    @Query("SELECT * FROM todos WHERE isPriority = 1")
    fun getPriorityTodos(): Flow<List<TodoEntity>>
    
    @Query("SELECT * FROM todos WHERE partyId = :partyId")
    fun getTodosByPartyId(partyId: String): Flow<List<TodoEntity>>
    
    /**
     * Récupère les todos d'un événement spécifique avec leur couleur
     */
    @Query("SELECT t.*, p.color as partyColor FROM todos t LEFT JOIN parties p ON t.partyId = p.id WHERE t.partyId = :partyId")
    fun getTodosByPartyIdWithColor(partyId: String): Flow<List<TodoWithPartyColor>>
    
    @Query("SELECT * FROM todos WHERE partyId = :partyId")
    suspend fun getTodosByPartyIdSync(partyId: String): List<TodoEntity>
    
    @Query("SELECT * FROM todos WHERE id = :todoId")
    suspend fun getTodoById(todoId: String): TodoEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodos(todos: List<TodoEntity>)
    
    @Update
    suspend fun updateTodo(todo: TodoEntity)
    
    @Query("UPDATE todos SET isPriority = :isPriority WHERE id = :todoId")
    suspend fun updateTodoPriority(todoId: String, isPriority: Boolean)
    
    @Query("UPDATE todos SET isCompleted = :isCompleted WHERE id = :todoId")
    suspend fun updateTodoStatus(todoId: String, isCompleted: Boolean)
    
    @Delete
    suspend fun deleteTodo(todo: TodoEntity)
    
    /**
     * Supprime un Todo par son ID
     */
    @Query("DELETE FROM todos WHERE id = :todoId")
    suspend fun deleteTodoById(todoId: String)
    
    @Query("DELETE FROM todos WHERE partyId = :partyId")
    suspend fun deleteTodosByPartyId(partyId: String)
} 