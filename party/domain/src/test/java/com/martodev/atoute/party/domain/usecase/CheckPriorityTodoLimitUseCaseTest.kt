package com.martodev.atoute.party.domain.usecase

import com.martodev.atoute.authentication.domain.usecase.GetCurrentUserPremiumStatusUseCase
import com.martodev.atoute.party.domain.model.Todo
import com.martodev.atoute.party.domain.repository.TodoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class CheckPriorityTodoLimitUseCaseTest {
    
    private lateinit var todoRepository: TodoRepository
    private lateinit var isPremiumUseCase: GetCurrentUserPremiumStatusUseCase
    private lateinit var checkPriorityTodoLimitUseCase: CheckPriorityTodoLimitUseCase
    
    @Before
    fun setUp() {
        todoRepository = mock()
        isPremiumUseCase = mock()
        checkPriorityTodoLimitUseCase = CheckPriorityTodoLimitUseCase(todoRepository, isPremiumUseCase)
    }
    
    @Test
    fun invokeReturnsTrueWhenUserIsPremium() = runTest {
        // Given
        whenever(isPremiumUseCase()).thenReturn(flowOf(true))
        whenever(todoRepository.getPriorityTodos()).thenReturn(flowOf(emptyList()))
        
        // When
        val result = checkPriorityTodoLimitUseCase().first()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun invokeReturnsTrueWhenUserIsNotPremiumButUnderLimit() = runTest {
        // Given
        val priorityTodos = listOf(
            Todo(id = "1", title = "Todo 1", partyId = "party1", isPriority = true),
            Todo(id = "2", title = "Todo 2", partyId = "party1", isPriority = true),
            Todo(id = "3", title = "Todo 3", partyId = "party1", isPriority = true)
        )
        whenever(isPremiumUseCase()).thenReturn(flowOf(false))
        whenever(todoRepository.getPriorityTodos()).thenReturn(flowOf(priorityTodos))
        
        // When
        val result = checkPriorityTodoLimitUseCase().first()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun invokeReturnsFalseWhenUserIsNotPremiumAndAtLimit() = runTest {
        // Given
        val priorityTodos = listOf(
            Todo(id = "1", title = "Todo 1", partyId = "party1", isPriority = true),
            Todo(id = "2", title = "Todo 2", partyId = "party1", isPriority = true),
            Todo(id = "3", title = "Todo 3", partyId = "party1", isPriority = true),
            Todo(id = "4", title = "Todo 4", partyId = "party1", isPriority = true)
        )
        whenever(isPremiumUseCase()).thenReturn(flowOf(false))
        whenever(todoRepository.getPriorityTodos()).thenReturn(flowOf(priorityTodos))
        
        // When
        val result = checkPriorityTodoLimitUseCase().first()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun checkSyncReturnsTrueWhenUserIsPremium() = runTest {
        // Given
        whenever(isPremiumUseCase()).thenReturn(flowOf(true))
        
        // When
        val result = checkPriorityTodoLimitUseCase.checkSync()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun checkSyncReturnsTrueWhenUserIsNotPremiumButUnderLimit() = runTest {
        // Given
        val priorityTodos = listOf(
            Todo(id = "1", title = "Todo 1", partyId = "party1", isPriority = true),
            Todo(id = "2", title = "Todo 2", partyId = "party1", isPriority = true),
            Todo(id = "3", title = "Todo 3", partyId = "party1", isPriority = true)
        )
        whenever(isPremiumUseCase()).thenReturn(flowOf(false))
        whenever(todoRepository.getPriorityTodos()).thenReturn(flowOf(priorityTodos))
        
        // When
        val result = checkPriorityTodoLimitUseCase.checkSync()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun checkSyncReturnsFalseWhenUserIsNotPremiumAndAtLimit() = runTest {
        // Given
        val priorityTodos = listOf(
            Todo(id = "1", title = "Todo 1", partyId = "party1", isPriority = true),
            Todo(id = "2", title = "Todo 2", partyId = "party1", isPriority = true),
            Todo(id = "3", title = "Todo 3", partyId = "party1", isPriority = true),
            Todo(id = "4", title = "Todo 4", partyId = "party1", isPriority = true)
        )
        whenever(isPremiumUseCase()).thenReturn(flowOf(false))
        whenever(todoRepository.getPriorityTodos()).thenReturn(flowOf(priorityTodos))
        
        // When
        val result = checkPriorityTodoLimitUseCase.checkSync()
        
        // Then
        assertFalse(result)
    }
} 