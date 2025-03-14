package com.martodev.atoute.party.domain.usecase

import com.martodev.atoute.party.domain.model.Todo
import com.martodev.atoute.party.domain.repository.TodoRepository
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
class GetTodosByPartyUseCaseTest {
    
    private lateinit var todoRepository: TodoRepository
    private lateinit var getTodosByPartyUseCase: GetTodosByPartyUseCase
    
    @Before
    fun setUp() {
        todoRepository = mock()
        getTodosByPartyUseCase = GetTodosByPartyUseCase(todoRepository)
    }
    
    @Test
    fun invokeReturnsTodosForSpecificPartyFromRepository() = runTest {
        // Given
        val partyId = "party1"
        val todos = listOf(
            Todo(id = "1", title = "Todo 1", partyId = partyId, isPriority = true),
            Todo(id = "2", title = "Todo 2", partyId = partyId, isPriority = false),
            Todo(id = "3", title = "Todo 3", partyId = partyId, isPriority = true)
        )
        whenever(todoRepository.getTodosByParty(partyId)).thenReturn(flowOf(todos))
        
        // When
        val result = getTodosByPartyUseCase(partyId).first()
        
        // Then
        assertEquals(todos, result)
    }
    
    @Test
    fun invokeReturnsEmptyListWhenNoTodosExistForParty() = runTest {
        // Given
        val partyId = "party1"
        val emptyList = emptyList<Todo>()
        whenever(todoRepository.getTodosByParty(partyId)).thenReturn(flowOf(emptyList))
        
        // When
        val result = getTodosByPartyUseCase(partyId).first()
        
        // Then
        assertEquals(emptyList, result)
    }
} 