package com.martodev.atoute.party.domain.usecase

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
class UpdateTodoPriorityUseCaseTest {
    
    private lateinit var todoRepository: TodoRepository
    private lateinit var checkPriorityTodoLimitUseCase: CheckPriorityTodoLimitUseCase
    private lateinit var updateTodoPriorityUseCase: UpdateTodoPriorityUseCase
    
    @Before
    fun setUp() {
        todoRepository = mock()
        checkPriorityTodoLimitUseCase = mock()
        updateTodoPriorityUseCase = UpdateTodoPriorityUseCase(todoRepository, checkPriorityTodoLimitUseCase)
    }
    
    @Test
    fun invokeWithSettingPriorityToFalseUpdatesTodo() = runTest {
        // Given
        val todoId = "todo1"
        val isPriority = false
        
        // When
        updateTodoPriorityUseCase(todoId, isPriority)
        
        // Then
        verify(todoRepository).updateTodoPriority(todoId, isPriority)
    }
    
    @Test
    fun invokeWithSettingPriorityToTrueAndUnderLimitUpdatesTodo() = runTest {
        // Given
        val todoId = "todo1"
        val isPriority = true
        whenever(checkPriorityTodoLimitUseCase.checkSync()).thenReturn(true)
        
        // When
        updateTodoPriorityUseCase(todoId, isPriority)
        
        // Then
        verify(checkPriorityTodoLimitUseCase).checkSync()
        verify(todoRepository).updateTodoPriority(todoId, isPriority)
    }
    
    @Test
    fun invokeWithSettingPriorityToTrueAndOverLimitThrowsException() = runTest {
        // Given
        val todoId = "todo1"
        val isPriority = true
        whenever(checkPriorityTodoLimitUseCase.checkSync()).thenReturn(false)
        
        try {
            // When
            updateTodoPriorityUseCase(todoId, isPriority)
            fail("Expected PriorityTodoLimitReachedException to be thrown")
        } catch (e: UpdateTodoPriorityUseCase.PriorityTodoLimitReachedException) {
            // Then
            val expectedMessage = "Vous avez atteint la limite de ${CheckPriorityTodoLimitUseCase.NON_PREMIUM_PRIORITY_TODO_LIMIT} tâches prioritaires pour un compte non-premium. " +
                    "Passez à la version premium pour avoir des tâches prioritaires illimitées."
            assertEquals(expectedMessage, e.message)
        }
    }
}