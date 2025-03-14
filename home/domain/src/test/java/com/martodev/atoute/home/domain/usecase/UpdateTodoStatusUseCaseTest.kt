package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.repository.TodoRepository
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
    fun invokeCallsUpdateTodoStatusOnRepositoryWithTrueParameter() = runTest {
        // Given
        val todoId = "todo-123"
        val isCompleted = true
        
        // When
        updateTodoStatusUseCase(todoId, isCompleted)
        
        // Then
        verify(todoRepository).updateTodoStatus(todoId, isCompleted)
    }
    
    @Test
    fun invokeCallsUpdateTodoStatusOnRepositoryWithFalseParameter() = runTest {
        // Given
        val todoId = "todo-123"
        val isCompleted = false
        
        // When
        updateTodoStatusUseCase(todoId, isCompleted)
        
        // Then
        verify(todoRepository).updateTodoStatus(todoId, isCompleted)
    }
} 