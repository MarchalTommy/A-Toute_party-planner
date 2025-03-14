package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.repository.TodoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class UpdateTodoPriorityUseCaseTest {
    
    private lateinit var todoRepository: TodoRepository
    private lateinit var updateTodoPriorityUseCase: UpdateTodoPriorityUseCase
    
    @Before
    fun setUp() {
        todoRepository = mock()
        updateTodoPriorityUseCase = UpdateTodoPriorityUseCase(todoRepository)
    }
    
    @Test
    fun invokeCallsUpdateTodoPriorityOnRepositoryWithTrueParameter() = runTest {
        // Given
        val todoId = "todo-123"
        val isPriority = true
        
        // When
        updateTodoPriorityUseCase(todoId, isPriority)
        
        // Then
        verify(todoRepository).updateTodoPriority(todoId, isPriority)
    }
    
    @Test
    fun invokeCallsUpdateTodoPriorityOnRepositoryWithFalseParameter() = runTest {
        // Given
        val todoId = "todo-123"
        val isPriority = false
        
        // When
        updateTodoPriorityUseCase(todoId, isPriority)
        
        // Then
        verify(todoRepository).updateTodoPriority(todoId, isPriority)
    }
} 