package com.martodev.atoute.party.domain.usecase

import com.martodev.atoute.party.domain.model.Todo
import com.martodev.atoute.party.domain.repository.TodoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class SaveTodoUseCaseTest {
    
    private lateinit var todoRepository: TodoRepository
    private lateinit var checkPriorityTodoLimitUseCase: CheckPriorityTodoLimitUseCase
    private lateinit var saveTodoUseCase: SaveTodoUseCase
    
    @Before
    fun setUp() {
        todoRepository = mock()
        checkPriorityTodoLimitUseCase = mock()
        saveTodoUseCase = SaveTodoUseCase(todoRepository, checkPriorityTodoLimitUseCase)
    }
    
    @Test
    fun invokeWithNonPriorityTodoSavesTodo() = runTest {
        // Given
        val todo = Todo(
            id = "todo1",
            title = "Test Todo",
            isCompleted = false,
            partyId = "party1",
            isPriority = false
        )
        
        // When
        saveTodoUseCase(todo)
        
        // Then
        verify(todoRepository).saveTodo(todo)
    }
    
    @Test
    fun invokeWithExistingPriorityTodoSavesTodo() = runTest {
        // Given
        val todo = Todo(
            id = "todo1", // ID existant
            title = "Test Todo",
            isCompleted = false,
            partyId = "party1",
            isPriority = true
        )
        
        // When
        saveTodoUseCase(todo)
        
        // Then
        verify(todoRepository).saveTodo(todo)
    }
    
    @Test
    fun invokeWithNewPriorityTodoAndUnderLimitSavesTodo() = runTest {
        // Given
        val todo = Todo(
            id = "", // Nouvel ID (vide)
            title = "Test Todo",
            isCompleted = false,
            partyId = "party1",
            isPriority = true
        )
        whenever(checkPriorityTodoLimitUseCase.checkSync()).thenReturn(true)
        
        // When
        saveTodoUseCase(todo)
        
        // Then
        verify(checkPriorityTodoLimitUseCase).checkSync()
        verify(todoRepository).saveTodo(todo)
    }
    
    @Test
    fun invokeWithNewPriorityTodoAndOverLimitThrowsException() = runTest {
        // Given
        val todo = Todo(
            id = "", // Nouvel ID (vide)
            title = "Test Todo",
            isCompleted = false,
            partyId = "party1",
            isPriority = true
        )
        whenever(checkPriorityTodoLimitUseCase.checkSync()).thenReturn(false)
        
        try {
            // When
            saveTodoUseCase(todo)
            fail("Expected PriorityTodoLimitReachedException to be thrown")
        } catch (e: SaveTodoUseCase.PriorityTodoLimitReachedException) {
            // Then
            val expectedMessage = "Vous avez atteint la limite de ${CheckPriorityTodoLimitUseCase.NON_PREMIUM_PRIORITY_TODO_LIMIT} tâches prioritaires pour un compte non-premium. " +
                    "Passez à la version premium pour avoir des tâches prioritaires illimitées."
            assertEquals(expectedMessage, e.message)
        }
    }
}