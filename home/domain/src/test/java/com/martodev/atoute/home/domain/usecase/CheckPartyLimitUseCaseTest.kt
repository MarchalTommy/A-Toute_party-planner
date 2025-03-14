package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.authentication.domain.usecase.GetCurrentUserPremiumStatusUseCase
import com.martodev.atoute.home.domain.model.Party
import com.martodev.atoute.home.domain.repository.PartyRepository
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
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class CheckPartyLimitUseCaseTest {
    
    private lateinit var partyRepository: PartyRepository
    private lateinit var isPremiumUseCase: GetCurrentUserPremiumStatusUseCase
    private lateinit var checkPartyLimitUseCase: CheckPartyLimitUseCase
    
    @Before
    fun setUp() {
        partyRepository = mock()
        isPremiumUseCase = mock()
        checkPartyLimitUseCase = CheckPartyLimitUseCase(partyRepository, isPremiumUseCase)
    }
    
    @Test
    fun invokeReturnsTrueWhenUserIsPremium() = runTest {
        // Given
        whenever(isPremiumUseCase()).thenReturn(flowOf(true))
        
        // Même avec plusieurs parties (au-dessus de la limite)
        val parties = listOf(
            Party(id = "1", title = "Party 1", date = LocalDateTime.now()),
            Party(id = "2", title = "Party 2", date = LocalDateTime.now()),
            Party(id = "3", title = "Party 3", date = LocalDateTime.now()),
            Party(id = "4", title = "Party 4", date = LocalDateTime.now())
        )
        whenever(partyRepository.getAllParties()).thenReturn(flowOf(parties))
        
        // When
        val result = checkPartyLimitUseCase().first()
        
        // Then
        assertTrue("L'utilisateur premium devrait pouvoir créer une nouvelle party", result)
    }
    
    @Test
    fun invokeReturnsTrueWhenUserIsNotPremiumButUnderLimit() = runTest {
        // Given
        val parties = listOf(
            Party(id = "1", title = "Party 1", date = LocalDateTime.now()),
            Party(id = "2", title = "Party 2", date = LocalDateTime.now())
        )
        whenever(isPremiumUseCase()).thenReturn(flowOf(false))
        whenever(partyRepository.getAllParties()).thenReturn(flowOf(parties))
        
        // When
        val result = checkPartyLimitUseCase().first()
        
        // Then
        assertTrue("L'utilisateur non-premium avec 2 parties (sous la limite de 3) devrait pouvoir créer une nouvelle party", result)
    }
    
    @Test
    fun invokeReturnsFalseWhenUserIsNotPremiumAndAtLimit() = runTest {
        // Given
        val parties = listOf(
            Party(id = "1", title = "Party 1", date = LocalDateTime.now()),
            Party(id = "2", title = "Party 2", date = LocalDateTime.now()),
            Party(id = "3", title = "Party 3", date = LocalDateTime.now())
        )
        whenever(isPremiumUseCase()).thenReturn(flowOf(false))
        whenever(partyRepository.getAllParties()).thenReturn(flowOf(parties))
        
        // When
        val result = checkPartyLimitUseCase().first()
        
        // Then
        assertFalse("L'utilisateur non-premium avec 3 parties (à la limite) ne devrait pas pouvoir créer une nouvelle party", result)
    }
    
    @Test
    fun checkSyncReturnsTrueWhenUserIsPremium() = runTest {
        // Given
        whenever(isPremiumUseCase()).thenReturn(flowOf(true))
        
        // Même avec plusieurs parties (au-dessus de la limite)
        val parties = listOf(
            Party(id = "1", title = "Party 1", date = LocalDateTime.now()),
            Party(id = "2", title = "Party 2", date = LocalDateTime.now()),
            Party(id = "3", title = "Party 3", date = LocalDateTime.now()),
            Party(id = "4", title = "Party 4", date = LocalDateTime.now())
        )
        whenever(partyRepository.getAllParties()).thenReturn(flowOf(parties))
        
        // When
        val result = checkPartyLimitUseCase.checkSync()
        
        // Then
        assertTrue("L'utilisateur premium devrait pouvoir créer une nouvelle party (méthode synchrone)", result)
    }
    
    @Test
    fun checkSyncReturnsTrueWhenUserIsNotPremiumButUnderLimit() = runTest {
        // Given
        val parties = listOf(
            Party(id = "1", title = "Party 1", date = LocalDateTime.now()),
            Party(id = "2", title = "Party 2", date = LocalDateTime.now())
        )
        whenever(isPremiumUseCase()).thenReturn(flowOf(false))
        whenever(partyRepository.getAllParties()).thenReturn(flowOf(parties))
        
        // When
        val result = checkPartyLimitUseCase.checkSync()
        
        // Then
        assertTrue("L'utilisateur non-premium avec 2 parties devrait pouvoir créer une nouvelle party (méthode synchrone)", result)
    }
    
    @Test
    fun checkSyncReturnsFalseWhenUserIsNotPremiumAndAtLimit() = runTest {
        // Given
        val parties = listOf(
            Party(id = "1", title = "Party 1", date = LocalDateTime.now()),
            Party(id = "2", title = "Party 2", date = LocalDateTime.now()),
            Party(id = "3", title = "Party 3", date = LocalDateTime.now())
        )
        whenever(isPremiumUseCase()).thenReturn(flowOf(false))
        whenever(partyRepository.getAllParties()).thenReturn(flowOf(parties))
        
        // When
        val result = checkPartyLimitUseCase.checkSync()
        
        // Then
        assertFalse("L'utilisateur non-premium avec 3 parties ne devrait pas pouvoir créer une nouvelle party (méthode synchrone)", result)
    }
} 