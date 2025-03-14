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
class SignInUseCaseTest {
    
    private lateinit var authRepository: AuthRepository
    private lateinit var signInUseCase: SignInUseCase
    
    @Before
    fun setUp() {
        authRepository = mock()
        signInUseCase = SignInUseCase(authRepository)
    }
    
    @Test
    fun invokeWithEmptyEmailReturnsError() = runTest {
        // When
        val result = signInUseCase("", "password123")
        
        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("L'email ne peut pas être vide", (result as AuthResult.Error).message)
    }
    
    @Test
    fun invokeWithEmptyPasswordReturnsError() = runTest {
        // When
        val result = signInUseCase("test@example.com", "")
        
        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("Le mot de passe ne peut pas être vide", (result as AuthResult.Error).message)
    }
    
    @Test
    fun invokeWithValidCredentialsReturnsSuccessFromRepository() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val user = User(
            id = "user1",
            username = "testuser",
            email = email,
            isPremium = false,
            preferences = UserPreferences()
        )
        val expectedResult = AuthResult.Success(user)
        
        whenever(authRepository.signIn(email, password)).thenReturn(expectedResult)
        
        // When
        val result = signInUseCase(email, password)
        
        // Then
        assertTrue(result is AuthResult.Success)
        assertEquals(expectedResult, result)
    }
    
    @Test
    fun invokeWithValidCredentialsButRepositoryErrorReturnsError() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val errorMessage = "Erreur d'authentification"
        val expectedResult = AuthResult.Error(errorMessage)
        
        whenever(authRepository.signIn(email, password)).thenReturn(expectedResult)
        
        // When
        val result = signInUseCase(email, password)
        
        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals(errorMessage, (result as AuthResult.Error).message)
    }
} 