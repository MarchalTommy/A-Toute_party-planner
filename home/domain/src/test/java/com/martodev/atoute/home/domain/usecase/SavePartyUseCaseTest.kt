package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.model.Party
import com.martodev.atoute.home.domain.repository.PartyRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class SavePartyUseCaseTest {
    
    private lateinit var partyRepository: PartyRepository
    private lateinit var checkPartyLimitUseCase: CheckPartyLimitUseCase
    private lateinit var savePartyUseCase: SavePartyUseCase
    
    @Before
    fun setUp() {
        partyRepository = mock()
        checkPartyLimitUseCase = mock()
        savePartyUseCase = SavePartyUseCase(partyRepository, checkPartyLimitUseCase)
    }
    
    @Test
    fun invokeWithExistingPartyIdSavesParty() = runTest {
        // Given
        val party = Party(
            id = "party1", // ID existant
            title = "Fête d'anniversaire",
            date = LocalDateTime.now(),
            location = "Maison"
        )
        
        // When
        val result = savePartyUseCase(party)
        
        // Then
        assertEquals("L'ID de la party doit être retourné", party.id, result)
        verify(partyRepository).saveParty(party)
    }
    
    @Test
    fun invokeWithNewPartyAndUnderLimitSavesParty() = runTest {
        // Given
        val party = Party(
            id = "", // ID vide = nouvelle party
            title = "Nouvelle fête",
            date = LocalDateTime.now().plusDays(7),
            location = "Parc",
            description = "Barbecue"
        )
        
        whenever(checkPartyLimitUseCase.checkSync()).thenReturn(true)
        
        // When
        val result = savePartyUseCase(party)
        
        // Then
        assertEquals("L'ID de la party doit être retourné", party.id, result)
        verify(checkPartyLimitUseCase).checkSync()
        verify(partyRepository).saveParty(party)
    }
    
    @Test
    fun invokeWithNewPartyAndOverLimitThrowsException() = runTest {
        // Given
        val party = Party(
            id = "", // ID vide = nouvelle party
            title = "Nouvelle fête",
            date = LocalDateTime.now().plusDays(7)
        )
        
        whenever(checkPartyLimitUseCase.checkSync()).thenReturn(false)
        
        try {
            // When
            savePartyUseCase(party)
            fail("Une PartyLimitReachedException aurait dû être lancée")
        } catch (e: SavePartyUseCase.PartyLimitReachedException) {
            // Then
            val expectedMessage = "Vous avez atteint la limite de ${CheckPartyLimitUseCase.NON_PREMIUM_PARTY_LIMIT} événements pour un compte non-premium. " +
                    "Passez à la version premium pour créer des événements illimités."
            assertEquals("Le message d'erreur doit mentionner la limite", expectedMessage, e.message)
        }
    }
} 