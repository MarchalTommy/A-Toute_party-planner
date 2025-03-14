package com.martodev.atoute.party.data.repository

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
import com.martodev.atoute.home.domain.repository.ToBuyRepository as HomeToBuyRepository

@ExperimentalCoroutinesApi
class ToBuyRepositoryAdapterTest {
    
    private lateinit var homeToBuyRepository: HomeToBuyRepository
    private lateinit var toBuyRepositoryAdapter: ToBuyRepositoryAdapter
    
    @Before
    fun setUp() {
        homeToBuyRepository = mock()
        toBuyRepositoryAdapter = ToBuyRepositoryAdapter(homeToBuyRepository)
    }
    
    @Test
    fun getAllToBuysConvertsHomeModelToDomainModel() = runTest {
        // Given
        val homeToBuys = listOf(
            com.martodev.atoute.home.domain.model.ToBuy(
                id = "1",
                title = "ToBuy 1",
                quantity = 2,
                estimatedPrice = 10.0f,
                isPurchased = false,
                partyId = "party1",
                isPriority = true
            ),
            com.martodev.atoute.home.domain.model.ToBuy(
                id = "2",
                title = "ToBuy 2", 
                quantity = 1,
                estimatedPrice = 5.0f,
                isPurchased = true,
                partyId = "party1",
                isPriority = false
            )
        )
        
        whenever(homeToBuyRepository.getAllToBuys()).thenReturn(flowOf(homeToBuys))
        
        // When
        val result = toBuyRepositoryAdapter.getAllToBuys().first()
        
        // Then
        assertEquals(2, result.size)
        assertEquals("1", result[0].id)
        assertEquals("ToBuy 1", result[0].title)
        assertEquals(2, result[0].quantity)
        assertEquals(10.0f, result[0].estimatedPrice)
        assertEquals(false, result[0].isPurchased)
        assertEquals("party1", result[0].partyId)
        assertEquals(true, result[0].isPriority)
        
        assertEquals("2", result[1].id)
        assertEquals("ToBuy 2", result[1].title)
        assertEquals(1, result[1].quantity)
        assertEquals(5.0f, result[1].estimatedPrice)
        assertEquals(true, result[1].isPurchased)
        assertEquals("party1", result[1].partyId)
        assertEquals(false, result[1].isPriority)
    }
    
    @Test
    fun getToBuysByPartyConvertsHomeModelToDomainModel() = runTest {
        // Given
        val partyId = "party1"
        val homeToBuys = listOf(
            com.martodev.atoute.home.domain.model.ToBuy(
                id = "1",
                title = "ToBuy 1",
                quantity = 2,
                estimatedPrice = 10.0f,
                isPurchased = false,
                partyId = partyId,
                isPriority = true
            )
        )
        
        whenever(homeToBuyRepository.getToBuysByParty(partyId)).thenReturn(flowOf(homeToBuys))
        
        // When
        val result = toBuyRepositoryAdapter.getToBuysByParty(partyId).first()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
        assertEquals("ToBuy 1", result[0].title)
        assertEquals(2, result[0].quantity)
        assertEquals(10.0f, result[0].estimatedPrice)
        assertEquals(false, result[0].isPurchased)
        assertEquals(partyId, result[0].partyId)
        assertEquals(true, result[0].isPriority)
    }
    
    @Test
    fun getPriorityToBuysConvertsHomeModelToDomainModel() = runTest {
        // Given
        val homeToBuys = listOf(
            com.martodev.atoute.home.domain.model.ToBuy(
                id = "1",
                title = "ToBuy 1",
                quantity = 2,
                estimatedPrice = 10.0f,
                isPurchased = false,
                partyId = "party1",
                isPriority = true
            )
        )
        
        whenever(homeToBuyRepository.getPriorityToBuys()).thenReturn(flowOf(homeToBuys))
        
        // When
        val result = toBuyRepositoryAdapter.getPriorityToBuys().first()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
        assertEquals(true, result[0].isPriority)
    }
    
    @Test
    fun getToBuyByIdConvertsHomeModelToDomainModel() = runTest {
        // Given
        val toBuyId = "1"
        val homeToBuy = com.martodev.atoute.home.domain.model.ToBuy(
            id = toBuyId,
            title = "ToBuy 1",
            quantity = 2,
            estimatedPrice = 10.0f,
            isPurchased = false,
            partyId = "party1",
            isPriority = true
        )
        
        whenever(homeToBuyRepository.getToBuyById(toBuyId)).thenReturn(homeToBuy)
        
        // When
        val result = toBuyRepositoryAdapter.getToBuyById(toBuyId).first()
        
        // Then
        assertEquals(toBuyId, result?.id)
        assertEquals("ToBuy 1", result?.title)
        assertEquals(2, result?.quantity)
        assertEquals(10.0f, result?.estimatedPrice)
        assertEquals(false, result?.isPurchased)
        assertEquals("party1", result?.partyId)
        assertEquals(true, result?.isPriority)
    }
    
    @Test
    fun getToBuyByIdReturnsNullWhenHomeRepositoryReturnsNull() = runTest {
        // Given
        val toBuyId = "1"
        whenever(homeToBuyRepository.getToBuyById(toBuyId)).thenReturn(null)
        
        // When
        val result = toBuyRepositoryAdapter.getToBuyById(toBuyId).first()
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun saveToBuyConvertsAndDelegatesCallToHomeRepository() = runTest {
        // Given
        val toBuy = com.martodev.atoute.party.domain.model.ToBuy(
            id = "1",
            title = "ToBuy 1",
            quantity = 2,
            estimatedPrice = 10.0f,
            isPurchased = false,
            partyId = "party1",
            isPriority = true
        )
        
        // When
        toBuyRepositoryAdapter.saveToBuy(toBuy)
        
        // Then
        verify(homeToBuyRepository).saveToBuy(
            com.martodev.atoute.home.domain.model.ToBuy(
                id = "1",
                title = "ToBuy 1",
                quantity = 2,
                estimatedPrice = 10.0f,
                isPurchased = false,
                partyId = "party1",
                isPriority = true
            )
        )
    }
    
    @Test
    fun updateToBuyStatusDelegatesCallToHomeRepository() = runTest {
        // Given
        val toBuyId = "1"
        val isPurchased = true
        
        // When
        toBuyRepositoryAdapter.updateToBuyStatus(toBuyId, isPurchased)
        
        // Then
        verify(homeToBuyRepository).updateToBuyStatus(toBuyId, isPurchased)
    }
    
    @Test
    fun updateToBuyPriorityDelegatesCallToHomeRepository() = runTest {
        // Given
        val toBuyId = "1"
        val isPriority = true
        
        // When
        toBuyRepositoryAdapter.updateToBuyPriority(toBuyId, isPriority)
        
        // Then
        verify(homeToBuyRepository).updateToBuyPriority(toBuyId, isPriority)
    }
    
    @Test
    fun deleteToBuyDelegatesCallToHomeRepository() = runTest {
        // Given
        val toBuyId = "1"
        
        // When
        toBuyRepositoryAdapter.deleteToBuy(toBuyId)
        
        // Then
        verify(homeToBuyRepository).deleteToBuy(toBuyId)
    }
    
    @Test
    fun deleteToBuysByPartyDelegatesCallToHomeRepository() = runTest {
        // Given
        val partyId = "party1"
        
        // When
        toBuyRepositoryAdapter.deleteToBuysByParty(partyId)
        
        // Then
        verify(homeToBuyRepository).deleteToBuysByParty(partyId)
    }
} 