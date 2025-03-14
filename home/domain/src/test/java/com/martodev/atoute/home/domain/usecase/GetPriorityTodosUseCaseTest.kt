package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.model.Todo
import com.martodev.atoute.home.domain.repository.TodoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class GetPriorityTodosUseCaseTest {
    
    private lateinit var todoRepository: TodoRepository
    private lateinit var getPriorityTodosUseCase: GetPriorityTodosUseCase
    
    @Before
    fun setUp() {
        todoRepository = mock()
        getPriorityTodosUseCase = GetPriorityTodosUseCase(todoRepository)
    }
    
    @Test
    fun invokeReturnsPriorityTodosFromRepository() = runTest {
        // Given
        val priorityTodos = listOf(
            Todo(
                id = "todo-1",
                title = "Todo 1",
                isCompleted = false,
                partyId = "party-1",
                isPriority = true
            ),
            Todo(
                id = "todo-2",
                title = "Todo 2",
                isCompleted = true,
                partyId = "party-2",
                isPriority = true
            )
        )
        
        whenever(todoRepository.getPriorityTodos()).thenReturn(flowOf(priorityTodos))
        
        // When
        val result = getPriorityTodosUseCase().first()
        
        // Then
        assertEquals(priorityTodos, result)
    }
    
    @Test
    fun invokeReturnsEmptyListWhenNoPriorityTodosExist() = runTest {
        // Given
        val emptyList = emptyList<Todo>()
        whenever(todoRepository.getPriorityTodos()).thenReturn(flowOf(emptyList))
        
        // When
        val result = getPriorityTodosUseCase().first()
        
        // Then
        assertEquals(emptyList, result)
    }
} 