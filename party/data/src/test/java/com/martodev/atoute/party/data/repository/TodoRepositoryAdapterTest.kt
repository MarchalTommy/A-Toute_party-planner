package com.martodev.atoute.party.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import com.martodev.atoute.home.domain.repository.TodoRepository as HomeTodoRepository

@ExperimentalCoroutinesApi
class TodoRepositoryAdapterTest {
    
    private lateinit var homeTodoRepository: HomeTodoRepository
    private lateinit var todoRepositoryAdapter: TodoRepositoryAdapter
    
    @Before
    fun setUp() {
        homeTodoRepository = mock()
        todoRepositoryAdapter = TodoRepositoryAdapter(homeTodoRepository)
    }
    
    @Test
    fun getAllTodosConvertsHomeModelToDomainModel() = runTest {
        // Given
        val homeTodos = listOf(
            com.martodev.atoute.home.domain.model.Todo(
                id = "1",
                title = "Todo 1",
                isCompleted = false,
                partyId = "party1",
                isPriority = true
            ),
            com.martodev.atoute.home.domain.model.Todo(
                id = "2",
                title = "Todo 2", 
                isCompleted = true,
                partyId = "party1",
                isPriority = false
            )
        )
        
        whenever(homeTodoRepository.getAllTodos()).thenReturn(flowOf(homeTodos))
        
        // When
        val result = todoRepositoryAdapter.getAllTodos().first()
        
        // Then
        assertEquals(2, result.size)
        assertEquals("1", result[0].id)
        assertEquals("Todo 1", result[0].title)
        assertEquals(false, result[0].isCompleted)
        assertEquals("party1", result[0].partyId)
        assertEquals(true, result[0].isPriority)
        
        assertEquals("2", result[1].id)
        assertEquals("Todo 2", result[1].title)
        assertEquals(true, result[1].isCompleted)
        assertEquals("party1", result[1].partyId)
        assertEquals(false, result[1].isPriority)
    }
    
    @Test
    fun getTodosByPartyConvertsHomeModelToDomainModel() = runTest {
        // Given
        val partyId = "party1"
        val homeTodos = listOf(
            com.martodev.atoute.home.domain.model.Todo(
                id = "1",
                title = "Todo 1",
                isCompleted = false,
                partyId = partyId,
                isPriority = true
            )
        )
        
        whenever(homeTodoRepository.getTodosByParty(partyId)).thenReturn(flowOf(homeTodos))
        
        // When
        val result = todoRepositoryAdapter.getTodosByParty(partyId).first()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
        assertEquals("Todo 1", result[0].title)
        assertEquals(false, result[0].isCompleted)
        assertEquals(partyId, result[0].partyId)
        assertEquals(true, result[0].isPriority)
    }
    
    @Test
    fun getPriorityTodosConvertsHomeModelToDomainModel() = runTest {
        // Given
        val homeTodos = listOf(
            com.martodev.atoute.home.domain.model.Todo(
                id = "1",
                title = "Todo 1",
                isCompleted = false,
                partyId = "party1",
                isPriority = true
            )
        )
        
        whenever(homeTodoRepository.getPriorityTodos()).thenReturn(flowOf(homeTodos))
        
        // When
        val result = todoRepositoryAdapter.getPriorityTodos().first()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
        assertEquals(true, result[0].isPriority)
    }
    
    @Test
    fun getTodoByIdConvertsHomeModelToDomainModel() = runTest {
        // Given
        val todoId = "1"
        val homeTodo = com.martodev.atoute.home.domain.model.Todo(
            id = todoId,
            title = "Todo 1",
            isCompleted = false,
            partyId = "party1",
            isPriority = true
        )
        
        whenever(homeTodoRepository.getTodoById(todoId)).thenReturn(homeTodo)
        
        // When
        val result = todoRepositoryAdapter.getTodoById(todoId)
        
        // Then
        assertEquals(todoId, result?.id)
        assertEquals("Todo 1", result?.title)
        assertEquals(false, result?.isCompleted)
        assertEquals("party1", result?.partyId)
        assertEquals(true, result?.isPriority)
    }
    
    @Test
    fun getTodoByIdReturnsNullWhenHomeRepositoryReturnsNull() = runTest {
        // Given
        val todoId = "1"
        whenever(homeTodoRepository.getTodoById(todoId)).thenReturn(null)
        
        // When
        val result = todoRepositoryAdapter.getTodoById(todoId)
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun saveTodoConvertsAndDelegatesCallToHomeRepository() = runTest {
        // Given
        val todo = com.martodev.atoute.party.domain.model.Todo(
            id = "1",
            title = "Todo 1",
            isCompleted = false,
            partyId = "party1",
            isPriority = true
        )
        
        // When
        todoRepositoryAdapter.saveTodo(todo)
        
        // Then
        verify(homeTodoRepository).saveTodo(
            com.martodev.atoute.home.domain.model.Todo(
                id = "1",
                title = "Todo 1",
                isCompleted = false,
                partyId = "party1",
                isPriority = true
            )
        )
    }
    
    @Test
    fun updateTodoStatusDelegatesCallToHomeRepository() = runTest {
        // Given
        val todoId = "1"
        val isCompleted = true
        
        // When
        todoRepositoryAdapter.updateTodoStatus(todoId, isCompleted)
        
        // Then
        verify(homeTodoRepository).updateTodoStatus(todoId, isCompleted)
    }
    
    @Test
    fun updateTodoPriorityDelegatesCallToHomeRepository() = runTest {
        // Given
        val todoId = "1"
        val isPriority = true
        
        // When
        todoRepositoryAdapter.updateTodoPriority(todoId, isPriority)
        
        // Then
        verify(homeTodoRepository).updateTodoPriority(todoId, isPriority)
    }
    
    @Test
    fun deleteTodoDelegatesCallToHomeRepository() = runTest {
        // Given
        val todoId = "1"
        
        // When
        todoRepositoryAdapter.deleteTodo(todoId)
        
        // Then
        verify(homeTodoRepository).deleteTodo(todoId)
    }
    
    @Test
    fun deleteTodosByPartyDelegatesCallToHomeRepository() = runTest {
        // Given
        val partyId = "party1"
        
        // When
        todoRepositoryAdapter.deleteTodosByParty(partyId)
        
        // Then
        verify(homeTodoRepository).deleteTodosByParty(partyId)
    }
} 