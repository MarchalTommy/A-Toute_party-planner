package com.martodev.atoute.party.domain.usecase

import com.martodev.atoute.party.domain.repository.TodoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class UpdateTodoStatusUseCaseTest {
    
    private lateinit var todoRepository: TodoRepository
    private lateinit var updateTodoStatusUseCase: UpdateTodoStatusUseCase
    
    @Before
    fun setUp() {
        todoRepository = mock()
        updateTodoStatusUseCase = UpdateTodoStatusUseCase(todoRepository)
    }
    
    @Test
    fun invokeCallsUpdateTodoStatusOnRepositoryWithCorrectParameters() = runTest {
        // Given
        val todoId = "todo1"
        val isCompleted = true
        
        // When
        updateTodoStatusUseCase(todoId, isCompleted)
        
        // Then
        verify(todoRepository).updateTodoStatus(todoId, isCompleted)
    }
    
    @Test
    fun invokeCallsUpdateTodoStatusOnRepositoryWithFalseParameter() = runTest {
        // Given
        val todoId = "todo1"
        val isCompleted = false
        
        // When
        updateTodoStatusUseCase(todoId, isCompleted)
        
        // Then
        verify(todoRepository).updateTodoStatus(todoId, isCompleted)
    }
} 