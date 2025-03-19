package com.martodev.atoute.home.data.repository

import com.martodev.atoute.core.data.dao.PartyDao
import com.martodev.atoute.core.data.dao.TodoDao
import com.martodev.atoute.core.data.entity.PartyEntity
import com.martodev.atoute.core.data.entity.PartyWithDetails
import com.martodev.atoute.core.data.entity.TodoEntity
import com.martodev.atoute.core.data.firebase.sync.FirestoreSyncManager
import com.martodev.atoute.home.domain.model.Todo
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
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class TodoRepositoryImplTest {
    
    private lateinit var todoDao: TodoDao
    private lateinit var partyDao: PartyDao
    private lateinit var syncManager: FirestoreSyncManager
    private lateinit var todoRepository: TodoRepositoryImpl
    
    @Before
    fun setUp() {
        todoDao = mock()
        partyDao = mock()
        syncManager = mock()
        todoRepository = TodoRepositoryImpl(todoDao, partyDao, syncManager)
    }
    
    @Test
    fun getAllTodosConvertsEntitiesToDomainModels() = runTest {
        // Given
        val todoEntities = listOf(
            TodoEntity(
                id = "1",
                title = "Todo 1",
                isCompleted = false,
                partyId = "party1",
                isPriority = true
            ),
            TodoEntity(
                id = "2",
                title = "Todo 2",
                isCompleted = true,
                partyId = "party2",
                isPriority = false
            )
        )
        
        val partyEntity1 = PartyEntity(
            id = "party1",
            title = "Party 1",
            date = LocalDateTime.now(),
            location = "Paris",
            description = "Christmas party",
            color = 0xFFFF0000,
            todoCount = 1,
            completedTodoCount = 0
        )
        
        val partyEntity2 = PartyEntity(
            id = "party2",
            title = "Party 2",
            date = LocalDateTime.now(),
            location = "Lyon",
            description = "New Year party",
            color = 0xFF00FF00,
            todoCount = 1,
            completedTodoCount = 1
        )
        
        whenever(todoDao.getAllTodos()).thenReturn(flowOf(todoEntities))
        whenever(partyDao.getPartyById("party1")).thenReturn(partyEntity1)
        whenever(partyDao.getPartyById("party2")).thenReturn(partyEntity2)
        
        // When
        val result = todoRepository.getAllTodos().first()
        
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
        assertEquals("party2", result[1].partyId)
        assertEquals(false, result[1].isPriority)
    }
    
    @Test
    fun getPriorityTodosConvertsEntitiesToDomainModels() = runTest {
        // Given
        val todoEntities = listOf(
            TodoEntity(
                id = "1",
                title = "Todo 1",
                isCompleted = false,
                partyId = "party1",
                isPriority = true
            )
        )
        
        val partyEntity = PartyEntity(
            id = "party1",
            title = "Party 1",
            date = LocalDateTime.now(),
            location = "Paris",
            description = "Christmas party",
            color = 0xFFFF0000,
            todoCount = 1,
            completedTodoCount = 0
        )
        
        whenever(todoDao.getPriorityTodos()).thenReturn(flowOf(todoEntities))
        whenever(partyDao.getPartyById("party1")).thenReturn(partyEntity)
        
        // When
        val result = todoRepository.getPriorityTodos().first()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
        assertEquals("Todo 1", result[0].title)
        assertEquals(false, result[0].isCompleted)
        assertEquals("party1", result[0].partyId)
        assertEquals(true, result[0].isPriority)
    }
    
    @Test
    fun getTodosByPartyConvertsEntitiesToDomainModels() = runTest {
        // Given
        val partyId = "party1"
        val todoEntities = listOf(
            TodoEntity(
                id = "1",
                title = "Todo 1",
                isCompleted = false,
                partyId = partyId,
                isPriority = true
            ),
            TodoEntity(
                id = "2",
                title = "Todo 2",
                isCompleted = true,
                partyId = partyId,
                isPriority = false
            )
        )
        
        val partyEntity = PartyEntity(
            id = partyId,
            title = "Party 1",
            date = LocalDateTime.now(),
            location = "Paris",
            description = "Christmas party",
            color = 0xFFFF0000,
            todoCount = 2,
            completedTodoCount = 1
        )
        
        val partyWithDetails = PartyWithDetails(
            party = partyEntity,
            participants = emptyList(),
            todos = emptyList(),
            toBuys = emptyList()
        )
        
        whenever(todoDao.getTodosByPartyId(partyId)).thenReturn(flowOf(todoEntities))
        whenever(partyDao.getPartyWithDetailsById(partyId)).thenReturn(flowOf(partyWithDetails))
        
        // When
        val result = todoRepository.getTodosByParty(partyId).first()
        
        // Then
        assertEquals(2, result.size)
        
        assertEquals("1", result[0].id)
        assertEquals("Todo 1", result[0].title)
        assertEquals(false, result[0].isCompleted)
        assertEquals(partyId, result[0].partyId)
        assertEquals(true, result[0].isPriority)
        
        assertEquals("2", result[1].id)
        assertEquals("Todo 2", result[1].title)
        assertEquals(true, result[1].isCompleted)
        assertEquals(partyId, result[1].partyId)
        assertEquals(false, result[1].isPriority)
    }
    
    @Test
    fun getTodoByIdConvertsEntityToDomainModel() = runTest {
        // Given
        val todoId = "1"
        val todoEntity = TodoEntity(
            id = todoId,
            title = "Todo 1",
            isCompleted = false,
            partyId = "party1",
            isPriority = true
        )
        
        whenever(todoDao.getTodoById(todoId)).thenReturn(todoEntity)
        
        // When
        val result = todoRepository.getTodoById(todoId)
        
        // Then
        assertEquals(todoId, result?.id)
        assertEquals("Todo 1", result?.title)
        assertEquals(false, result?.isCompleted)
        assertEquals("party1", result?.partyId)
        assertEquals(true, result?.isPriority)
    }
    
    @Test
    fun getTodoByIdReturnsNullWhenEntityIsNull() = runTest {
        // Given
        val todoId = "1"
        whenever(todoDao.getTodoById(todoId)).thenReturn(null)
        
        // When
        val result = todoRepository.getTodoById(todoId)
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun saveTodoConvertsAndSavesDomainModelToEntity() = runTest {
        // Given
        val todo = Todo(
            id = "1",
            title = "Todo 1",
            isCompleted = false,
            partyId = "party1",
            isPriority = true
        )
        
        // When
        todoRepository.saveTodo(todo)
        
        // Then
        verify(todoDao).insertTodo(
            TodoEntity(
                id = "1",
                title = "Todo 1",
                isCompleted = false,
                partyId = "party1",
                isPriority = true
            )
        )
    }
    
    @Test
    fun updateTodoStatusUpdatesEntityStatus() = runTest {
        // Given
        val todoId = "1"
        val isCompleted = true
        
        // When
        todoRepository.updateTodoStatus(todoId, isCompleted)
        
        // Then
        verify(todoDao).updateTodoStatus(todoId, isCompleted)
    }
    
    @Test
    fun updateTodoPriorityUpdatesEntityPriority() = runTest {
        // Given
        val todoId = "1"
        val isPriority = true
        
        // When
        todoRepository.updateTodoPriority(todoId, isPriority)
        
        // Then
        verify(todoDao).updateTodoPriority(todoId, isPriority)
    }
    
    @Test
    fun deleteTodoDeletesEntityById() = runTest {
        // Given
        val todoId = "1"
        
        // When
        todoRepository.deleteTodo(todoId)
        
        // Then
        verify(todoDao).deleteTodoById(todoId)
    }
} 