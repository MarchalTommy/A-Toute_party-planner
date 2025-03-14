package com.martodev.atoute.party.data.repository

import com.martodev.atoute.home.data.dao.PartyDao
import com.martodev.atoute.home.data.dao.TodoDao
import com.martodev.atoute.home.data.entity.PartyEntity
import com.martodev.atoute.home.data.entity.PartyWithDetails
import com.martodev.atoute.party.domain.model.Party
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class PartyRepositoryImplTest {
    
    private lateinit var partyDao: PartyDao
    private lateinit var todoDao: TodoDao
    private lateinit var partyRepository: PartyRepositoryImpl
    
    @Before
    fun setUp() {
        partyDao = mock()
        todoDao = mock()
        partyRepository = PartyRepositoryImpl(partyDao, todoDao)
    }
    
    @Test
    fun getAllPartiesConvertsEntitiesToDomainModels() = runTest {
        // Given
        val partyEntities = listOf(
            PartyEntity(
                id = "1",
                title = "Party 1",
                date = LocalDateTime.now(),
                location = "Paris",
                description = "Christmas party",
                color = 0xFFFF0000,
                todoCount = 5,
                completedTodoCount = 2
            ),
            PartyEntity(
                id = "2",
                title = "Party 2",
                date = LocalDateTime.now(),
                location = "Lyon",
                description = "New Year party",
                color = 0xFF00FF00,
                todoCount = 3,
                completedTodoCount = 0
            )
        )
        
        whenever(partyDao.getAllParties()).thenReturn(flowOf(partyEntities))
        
        // When
        val result = partyRepository.getAllParties().first()
        
        // Then
        assertEquals(2, result.size)
        
        assertEquals("1", result[0].id)
        assertEquals("Party 1", result[0].title)
        assertEquals("Paris", result[0].location)
        assertEquals("Christmas party", result[0].description)
        assertEquals(5, result[0].todoCount)
        assertEquals(2, result[0].completedTodoCount)
        
        assertEquals("2", result[1].id)
        assertEquals("Party 2", result[1].title)
        assertEquals("Lyon", result[1].location)
        assertEquals("New Year party", result[1].description)
        assertEquals(3, result[1].todoCount)
        assertEquals(0, result[1].completedTodoCount)
    }
    
    @Test
    fun getPartyByIdConvertsEntityToDomainModel() = runTest {
        // Given
        val partyId = "1"
        val partyEntity = PartyEntity(
            id = partyId,
            title = "Party 1",
            date = LocalDateTime.now(),
            location = "Paris",
            description = "Christmas party",
            color = 0xFFFF0000,
            todoCount = 5,
            completedTodoCount = 2
        )
        
        val partyWithDetails = PartyWithDetails(
            party = partyEntity,
            participants = emptyList(),
            todos = emptyList(),
            toBuys = emptyList()
        )
        
        whenever(partyDao.getPartyWithDetailsById(partyId)).thenReturn(flowOf(partyWithDetails))
        
        // When
        val result = partyRepository.getPartyById(partyId).first()
        
        // Then
        assertEquals(partyId, result?.id)
        assertEquals("Party 1", result?.title)
        assertEquals("Paris", result?.location)
        assertEquals("Christmas party", result?.description)
        assertEquals(5, result?.todoCount)
        assertEquals(2, result?.completedTodoCount)
    }
    
    @Test
    fun getPartyByIdReturnsNullWhenEntityIsNull() = runTest {
        // Given
        val partyId = "1"
        whenever(partyDao.getPartyWithDetailsById(partyId)).thenReturn(flowOf(null))
        
        // When
        val result = partyRepository.getPartyById(partyId).first()
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun getPartyByIdSyncConvertsEntityToDomainModel() = runTest {
        // Given
        val partyId = "1"
        val partyEntity = PartyEntity(
            id = partyId,
            title = "Party 1",
            date = LocalDateTime.now(),
            location = "Paris",
            description = "Christmas party",
            color = 0xFFFF0000,
            todoCount = 5,
            completedTodoCount = 2
        )
        
        whenever(partyDao.getPartyById(partyId)).thenReturn(partyEntity)
        
        // When
        val result = partyRepository.getPartyByIdSync(partyId)
        
        // Then
        assertEquals(partyId, result?.id)
        assertEquals("Party 1", result?.title)
        assertEquals("Paris", result?.location)
        assertEquals("Christmas party", result?.description)
        assertEquals(5, result?.todoCount)
        assertEquals(2, result?.completedTodoCount)
    }
    
    @Test
    fun getPartyByIdSyncReturnsNullWhenEntityIsNull() = runTest {
        // Given
        val partyId = "1"
        whenever(partyDao.getPartyById(partyId)).thenReturn(null)
        
        // When
        val result = partyRepository.getPartyByIdSync(partyId)
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun savePartyConvertsAndSavesDomainModelToEntity() = runTest {
        // Given
        val date = LocalDateTime.now()
        val party = Party(
            id = "1",
            title = "Party 1",
            date = date,
            location = "Paris",
            description = "Christmas party",
            color = 0xFFFF0000,
            todoCount = 5,
            completedTodoCount = 2
        )
        
        // When
        partyRepository.saveParty(party)
        
        // Then
        verify(partyDao).insertParty(any())
    }
    
    @Test
    fun savePartyGeneratesIdWhenIdIsEmpty() = runTest {
        // Given
        val date = LocalDateTime.now()
        val party = Party(
            id = "",
            title = "Party 1",
            date = date,
            location = "Paris",
            description = "Christmas party",
            color = 0xFFFF0000,
            todoCount = 5,
            completedTodoCount = 2
        )
        
        // When
        partyRepository.saveParty(party)
        
        // Then
        verify(partyDao).insertParty(any())
    }
    
    @Test
    fun savePartiesConvertsAndSavesDomainModelsToEntities() = runTest {
        // Given
        val date1 = LocalDateTime.now()
        val date2 = LocalDateTime.now().plusDays(7)
        val parties = listOf(
            Party(
                id = "1",
                title = "Party 1",
                date = date1,
                location = "Paris",
                description = "Christmas party",
                color = 0xFFFF0000,
                todoCount = 5,
                completedTodoCount = 2
            ),
            Party(
                id = "2",
                title = "Party 2",
                date = date2,
                location = "Lyon",
                description = "New Year party",
                color = 0xFF00FF00,
                todoCount = 3,
                completedTodoCount = 0
            )
        )
        
        // When
        partyRepository.saveParties(parties)
        
        // Then
        verify(partyDao).insertParties(any())
    }
    
    @Test
    fun deletePartyDelegatesCallToDao() = runTest {
        // Given
        val partyId = "1"
        
        // When
        partyRepository.deleteParty(partyId)
        
        // Then
        verify(partyDao).deletePartyById(partyId)
    }
} 