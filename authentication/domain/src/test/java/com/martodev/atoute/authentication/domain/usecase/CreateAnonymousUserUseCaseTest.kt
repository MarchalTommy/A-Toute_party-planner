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
class CreateAnonymousUserUseCaseTest {
    
    private lateinit var authRepository: AuthRepository
    private lateinit var createAnonymousUserUseCase: CreateAnonymousUserUseCase
    
    @Before
    fun setUp() {
        authRepository = mock()
        createAnonymousUserUseCase = CreateAnonymousUserUseCase(authRepository)
    }
    
    @Test
    fun `invoke with empty username returns error`() = runTest {
        // When
        val result = createAnonymousUserUseCase("")
        
        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("Le pseudo ne peut pas être vide", (result as AuthResult.Error).message)
    }
    
    @Test
    fun `invoke with valid username returns success from repository`() = runTest {
        // Given
        val username = "testuser"
        val user = User(
            id = "user1",
            username = username,
            email = null,
            isPremium = false,
            preferences = UserPreferences()
        )
        val expectedResult = AuthResult.Success(user)
        
        whenever(authRepository.createAnonymousUser(username)).thenReturn(expectedResult)
        
        // When
        val result = createAnonymousUserUseCase(username)
        
        // Then
        assertTrue(result is AuthResult.Success)
        assertEquals(expectedResult, result)
    }
    
    @Test
    fun `invoke with valid username but repository error returns error`() = runTest {
        // Given
        val username = "testuser"
        val errorMessage = "Erreur lors de la création de l'utilisateur anonyme"
        val expectedResult = AuthResult.Error(errorMessage)
        
        whenever(authRepository.createAnonymousUser(username)).thenReturn(expectedResult)
        
        // When
        val result = createAnonymousUserUseCase(username)
        
        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals(errorMessage, (result as AuthResult.Error).message)
    }
} 