package com.martodev.atoute.authentication.domain.usecase

import com.martodev.atoute.authentication.domain.model.User
import com.martodev.atoute.authentication.domain.model.UserPreferences
import com.martodev.atoute.authentication.domain.repository.AuthRepository
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
class GetCurrentUserPremiumStatusUseCaseTest {
    
    private lateinit var authRepository: AuthRepository
    private lateinit var getCurrentUserPremiumStatusUseCase: GetCurrentUserPremiumStatusUseCase
    
    @Before
    fun setUp() {
        authRepository = mock()
        getCurrentUserPremiumStatusUseCase = GetCurrentUserPremiumStatusUseCase(authRepository)
    }
    
    @Test
    fun `invoke returns true when user is premium`() = runTest {
        // Given
        val user = User(
            id = "user1",
            username = "testuser",
            email = "test@example.com",
            isPremium = true,
            preferences = UserPreferences(
                drinksAlcohol = true,
                isHalal = false,
                isVegetarian = false,
                isVegan = false,
                hasAllergies = listOf()
            )
        )
        whenever(authRepository.getCurrentUser()).thenReturn(flowOf(user))
        
        // When
        val result = getCurrentUserPremiumStatusUseCase().first()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `invoke returns false when user is not premium`() = runTest {
        // Given
        val user = User(
            id = "user1",
            username = "testuser",
            email = "test@example.com",
            isPremium = false,
            preferences = UserPreferences()
        )
        whenever(authRepository.getCurrentUser()).thenReturn(flowOf(user))
        
        // When
        val result = getCurrentUserPremiumStatusUseCase().first()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `invoke returns false when user is null`() = runTest {
        // Given
        whenever(authRepository.getCurrentUser()).thenReturn(flowOf(null))
        
        // When
        val result = getCurrentUserPremiumStatusUseCase().first()
        
        // Then
        assertFalse(result)
    }
} 