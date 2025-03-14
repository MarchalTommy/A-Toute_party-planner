package com.martodev.atoute.authentication.domain.usecase

import com.martodev.atoute.authentication.domain.model.User
import com.martodev.atoute.authentication.domain.model.UserPreferences
import com.martodev.atoute.authentication.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class GetCurrentUserUseCaseTest {
    
    private lateinit var authRepository: AuthRepository
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
    
    @Before
    fun setUp() {
        authRepository = mock()
        getCurrentUserUseCase = GetCurrentUserUseCase(authRepository)
    }
    
    @Test
    fun `invoke returns user from repository when user exists`() = runTest {
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
        val result = getCurrentUserUseCase().first()
        
        // Then
        assertEquals(user, result)
    }
    
    @Test
    fun `invoke returns null from repository when no user exists`() = runTest {
        // Given
        whenever(authRepository.getCurrentUser()).thenReturn(flowOf(null))
        
        // When
        val result = getCurrentUserUseCase().first()
        
        // Then
        assertNull(result)
    }
} 