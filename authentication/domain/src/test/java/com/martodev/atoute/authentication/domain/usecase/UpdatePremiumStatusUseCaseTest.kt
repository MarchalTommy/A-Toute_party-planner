package com.martodev.atoute.authentication.domain.usecase

import com.martodev.atoute.authentication.domain.model.AuthResult
import com.martodev.atoute.authentication.domain.model.User
import com.martodev.atoute.authentication.domain.model.UserPreferences
import com.martodev.atoute.authentication.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class UpdatePremiumStatusUseCaseTest {
    
    private lateinit var authRepository: AuthRepository
    private lateinit var updatePremiumStatusUseCase: UpdatePremiumStatusUseCase
    
    @Before
    fun setUp() {
        authRepository = mock()
        updatePremiumStatusUseCase = UpdatePremiumStatusUseCase(authRepository)
    }
    
    @Test
    fun `invoke with empty userId returns error`() = runTest {
        // When
        val result = updatePremiumStatusUseCase("", true)
        
        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("L'ID utilisateur ne peut pas être vide", (result as AuthResult.Error).message)
    }
    
    @Test
    fun `invoke with valid userId and premium status true returns success from repository`() = runTest {
        // Given
        val userId = "user1"
        val isPremium = true
        val user = User(
            id = userId,
            username = "testuser",
            email = "test@example.com",
            isPremium = isPremium,
            preferences = UserPreferences()
        )
        val expectedResult = AuthResult.Success(user)
        
        whenever(authRepository.updatePremiumStatus(userId, isPremium)).thenReturn(expectedResult)
        
        // When
        val result = updatePremiumStatusUseCase(userId, isPremium)
        
        // Then
        assertTrue(result is AuthResult.Success)
        assertEquals(expectedResult, result)
    }
    
    @Test
    fun `invoke with valid userId and premium status false returns success from repository`() = runTest {
        // Given
        val userId = "user1"
        val isPremium = false
        val user = User(
            id = userId,
            username = "testuser",
            email = "test@example.com",
            isPremium = isPremium,
            preferences = UserPreferences()
        )
        val expectedResult = AuthResult.Success(user)
        
        whenever(authRepository.updatePremiumStatus(userId, isPremium)).thenReturn(expectedResult)
        
        // When
        val result = updatePremiumStatusUseCase(userId, isPremium)
        
        // Then
        assertTrue(result is AuthResult.Success)
        assertEquals(expectedResult, result)
    }
    
    @Test
    fun `invoke with valid userId but repository error returns error`() = runTest {
        // Given
        val userId = "user1"
        val isPremium = true
        val errorMessage = "Erreur lors de la mise à jour du statut premium"
        val expectedResult = AuthResult.Error(errorMessage)
        
        whenever(authRepository.updatePremiumStatus(userId, isPremium)).thenReturn(expectedResult)
        
        // When
        val result = updatePremiumStatusUseCase(userId, isPremium)
        
        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals(errorMessage, (result as AuthResult.Error).message)
    }
} 