package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.model.Party
import com.martodev.atoute.home.domain.repository.PartyRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class GetPartiesUseCaseTest {
    
    private lateinit var partyRepository: PartyRepository
    private lateinit var getPartiesUseCase: GetPartiesUseCase
    
    @Before
    fun setUp() {
        partyRepository = mock()
        getPartiesUseCase = GetPartiesUseCase(partyRepository)
    }
    
    @Test
    fun invokeReturnsPartiesFromRepository() = runTest {
        // Given
        val parties = listOf(
            Party(id = "1", title = "Party 1", date = LocalDateTime.now()),
            Party(id = "2", title = "Party 2", date = LocalDateTime.now().plusDays(1)),
            Party(id = "3", title = "Party 3", date = LocalDateTime.now().plusDays(2))
        )
        whenever(partyRepository.getAllParties()).thenReturn(flowOf(parties))
        
        // When
        val result = getPartiesUseCase().first()
        
        // Then
        assertEquals("La liste des parties doit être retournée", parties, result)
        assertEquals("Le nombre de parties doit correspondre", 3, result.size)
        assertEquals("L'ordre des parties doit être préservé", "Party 1", result[0].title)
        assertEquals("L'ordre des parties doit être préservé", "Party 2", result[1].title)
        assertEquals("L'ordre des parties doit être préservé", "Party 3", result[2].title)
    }
    
    @Test
    fun invokeReturnsEmptyListWhenNoParties() = runTest {
        // Given
        whenever(partyRepository.getAllParties()).thenReturn(flowOf(emptyList()))
        
        // When
        val result = getPartiesUseCase().first()
        
        // Then
        assertEquals("Une liste vide doit être retournée", emptyList<Party>(), result)
    }
} 