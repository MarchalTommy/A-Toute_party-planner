package com.martodev.atoute.home.data.repository

import com.martodev.atoute.core.data.dao.PartyDao
import com.martodev.atoute.core.data.dao.ToBuyDao
import com.martodev.atoute.core.data.entity.ToBuyEntity
import com.martodev.atoute.core.data.firebase.sync.FirestoreSyncManager
import com.martodev.atoute.home.domain.model.ToBuy
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

@ExperimentalCoroutinesApi
class ToBuyRepositoryImplTest {
    
    private lateinit var toBuyDao: ToBuyDao
    private lateinit var partyDao: PartyDao
    private lateinit var syncManager: FirestoreSyncManager
    private lateinit var toBuyRepository: ToBuyRepositoryImpl
    
    @Before
    fun setUp() {
        toBuyDao = mock()
        partyDao = mock()
        syncManager = mock()
        toBuyRepository = ToBuyRepositoryImpl(toBuyDao, partyDao, syncManager)
    }
    
    @Test
    fun getAllToBuysConvertsEntitiesToDomainModels() = runTest {
        // Given
        val toBuyEntities = listOf(
            ToBuyEntity(
                id = "1",
                title = "ToBuy 1",
                quantity = 2,
                estimatedPrice = 10.0f,
                isPurchased = false,
                assignedTo = null,
                partyId = "party1",
                isPriority = true
            ),
            ToBuyEntity(
                id = "2",
                title = "ToBuy 2",
                quantity = 1,
                estimatedPrice = 5.0f,
                isPurchased = true,
                assignedTo = "user1",
                partyId = "party2",
                isPriority = false
            )
        )
        
        whenever(toBuyDao.getAllToBuys()).thenReturn(flowOf(toBuyEntities))
        
        // When
        val result = toBuyRepository.getAllToBuys().first()
        
        // Then
        assertEquals(2, result.size)
        
        assertEquals("1", result[0].id)
        assertEquals("ToBuy 1", result[0].title)
        assertEquals(2, result[0].quantity)
        assertEquals(10.0f, result[0].estimatedPrice)
        assertEquals(false, result[0].isPurchased)
        assertEquals(null, result[0].assignedTo)
        assertEquals("party1", result[0].partyId)
        assertEquals(true, result[0].isPriority)
        
        assertEquals("2", result[1].id)
        assertEquals("ToBuy 2", result[1].title)
        assertEquals(1, result[1].quantity)
        assertEquals(5.0f, result[1].estimatedPrice)
        assertEquals(true, result[1].isPurchased)
        assertEquals("user1", result[1].assignedTo)
        assertEquals("party2", result[1].partyId)
        assertEquals(false, result[1].isPriority)
    }
    
    @Test
    fun getPriorityToBuysConvertsEntitiesToDomainModels() = runTest {
        // Given
        val toBuyEntities = listOf(
            ToBuyEntity(
                id = "1",
                title = "ToBuy 1",
                quantity = 2,
                estimatedPrice = 10.0f,
                isPurchased = false,
                assignedTo = null,
                partyId = "party1",
                isPriority = true
            )
        )
        
        whenever(toBuyDao.getPriorityToBuys()).thenReturn(flowOf(toBuyEntities))
        
        // When
        val result = toBuyRepository.getPriorityToBuys().first()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
        assertEquals("ToBuy 1", result[0].title)
        assertEquals(2, result[0].quantity)
        assertEquals(10.0f, result[0].estimatedPrice)
        assertEquals(false, result[0].isPurchased)
        assertEquals(null, result[0].assignedTo)
        assertEquals("party1", result[0].partyId)
        assertEquals(true, result[0].isPriority)
    }
    
    @Test
    fun getToBuysByPartyConvertsEntitiesToDomainModels() = runTest {
        // Given
        val partyId = "party1"
        val toBuyEntities = listOf(
            ToBuyEntity(
                id = "1",
                title = "ToBuy 1",
                quantity = 2,
                estimatedPrice = 10.0f,
                isPurchased = false,
                assignedTo = null,
                partyId = partyId,
                isPriority = true
            ),
            ToBuyEntity(
                id = "2",
                title = "ToBuy 2",
                quantity = 1,
                estimatedPrice = 5.0f,
                isPurchased = true,
                assignedTo = "user1",
                partyId = partyId,
                isPriority = false
            )
        )
        
        whenever(toBuyDao.getToBuysByPartyId(partyId)).thenReturn(flowOf(toBuyEntities))
        
        // When
        val result = toBuyRepository.getToBuysByParty(partyId).first()
        
        // Then
        assertEquals(2, result.size)
        
        assertEquals("1", result[0].id)
        assertEquals("ToBuy 1", result[0].title)
        assertEquals(2, result[0].quantity)
        assertEquals(10.0f, result[0].estimatedPrice)
        assertEquals(false, result[0].isPurchased)
        assertEquals(null, result[0].assignedTo)
        assertEquals(partyId, result[0].partyId)
        assertEquals(true, result[0].isPriority)
        
        assertEquals("2", result[1].id)
        assertEquals("ToBuy 2", result[1].title)
        assertEquals(1, result[1].quantity)
        assertEquals(5.0f, result[1].estimatedPrice)
        assertEquals(true, result[1].isPurchased)
        assertEquals("user1", result[1].assignedTo)
        assertEquals(partyId, result[1].partyId)
        assertEquals(false, result[1].isPriority)
    }
    
    @Test
    fun getToBuyByIdConvertsEntityToDomainModel() = runTest {
        // Given
        val toBuyId = "1"
        val toBuyEntity = ToBuyEntity(
            id = toBuyId,
            title = "ToBuy 1",
            quantity = 2,
            estimatedPrice = 10.0f,
            isPurchased = false,
            assignedTo = null,
            partyId = "party1",
            isPriority = true
        )
        
        whenever(toBuyDao.getToBuyById(toBuyId)).thenReturn(toBuyEntity)
        
        // When
        val result = toBuyRepository.getToBuyById(toBuyId)
        
        // Then
        assertEquals(toBuyId, result?.id)
        assertEquals("ToBuy 1", result?.title)
        assertEquals(2, result?.quantity)
        assertEquals(10.0f, result?.estimatedPrice)
        assertEquals(false, result?.isPurchased)
        assertEquals(null, result?.assignedTo)
        assertEquals("party1", result?.partyId)
        assertEquals(true, result?.isPriority)
    }
    
    @Test
    fun getToBuyByIdReturnsNullWhenEntityIsNull() = runTest {
        // Given
        val toBuyId = "1"
        whenever(toBuyDao.getToBuyById(toBuyId)).thenReturn(null)
        
        // When
        val result = toBuyRepository.getToBuyById(toBuyId)
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun saveToBuyConvertsAndSavesDomainModelToEntity() = runTest {
        // Given
        val toBuy = ToBuy(
            id = "1",
            title = "ToBuy 1",
            quantity = 2,
            estimatedPrice = 10.0f,
            isPurchased = false,
            assignedTo = null,
            partyId = "party1",
            partyColor = null,
            isPriority = true
        )
        
        // When
        toBuyRepository.saveToBuy(toBuy)
        
        // Then
        verify(toBuyDao).insertToBuy(
            ToBuyEntity(
                id = "1",
                title = "ToBuy 1",
                quantity = 2,
                estimatedPrice = 10.0f,
                isPurchased = false,
                assignedTo = null,
                partyId = "party1",
                isPriority = true
            )
        )
        verify(syncManager).pushLocalChanges()
    }
    
    @Test
    fun updateToBuyStatusUpdatesEntityStatus() = runTest {
        // Given
        val toBuyId = "1"
        val isPurchased = true
        
        // When
        toBuyRepository.updateToBuyStatus(toBuyId, isPurchased)
        
        // Then
        verify(toBuyDao).updateToBuyStatus(toBuyId, isPurchased)
        verify(syncManager).pushLocalChanges()
    }
    
    @Test
    fun updateToBuyPriorityUpdatesEntityPriority() = runTest {
        // Given
        val toBuyId = "1"
        val isPriority = true
        
        // When
        toBuyRepository.updateToBuyPriority(toBuyId, isPriority)
        
        // Then
        verify(toBuyDao).updateToBuyPriority(toBuyId, isPriority)
        verify(syncManager).pushLocalChanges()
    }
    
    @Test
    fun deleteToBuyDeletesEntityById() = runTest {
        // Given
        val toBuyId = "1"
        
        // When
        toBuyRepository.deleteToBuy(toBuyId)
        
        // Then
        verify(toBuyDao).deleteToBuyById(toBuyId)
        verify(syncManager).pushLocalChanges()
    }
    
    @Test
    fun deleteToBuysByPartyDeletesEntitiesByPartyId() = runTest {
        // Given
        val partyId = "party1"
        
        // When
        toBuyRepository.deleteToBuysByParty(partyId)
        
        // Then
        verify(toBuyDao).deleteToBuysByPartyId(partyId)
        verify(syncManager).pushLocalChanges()
    }
} 