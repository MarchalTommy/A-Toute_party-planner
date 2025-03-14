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
class CreateAccountUseCaseTest {
    
    private lateinit var authRepository: AuthRepository
    private lateinit var createAccountUseCase: CreateAccountUseCase
    
    @Before
    fun setUp() {
        authRepository = mock()
        createAccountUseCase = CreateAccountUseCase(authRepository)
    }
    
    @Test
    fun `invoke with empty username returns error`() = runTest {
        // When
        val result = createAccountUseCase("", "test@example.com", "password123")
        
        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("Le pseudo ne peut pas être vide", (result as AuthResult.Error).message)
    }
    
    @Test
    fun `invoke with empty email returns error`() = runTest {
        // When
        val result = createAccountUseCase("testuser", "", "password123")
        
        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("L'email ne peut pas être vide", (result as AuthResult.Error).message)
    }
    
    @Test
    fun `invoke with empty password returns error`() = runTest {
        // When
        val result = createAccountUseCase("testuser", "test@example.com", "")
        
        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("Le mot de passe doit contenir au moins 6 caractères", (result as AuthResult.Error).message)
    }
    
    @Test
    fun `invoke with invalid email format returns error`() = runTest {
        // When
        val result = createAccountUseCase("testuser", "invalid-email", "password123")
        
        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("Format d'email invalide", (result as AuthResult.Error).message)
    }
    
    @Test
    fun `invoke with password too short returns error`() = runTest {
        // When
        val result = createAccountUseCase("testuser", "test@example.com", "pass")
        
        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("Le mot de passe doit contenir au moins 6 caractères", (result as AuthResult.Error).message)
    }
    
    @Test
    fun `invoke with valid data returns success from repository`() = runTest {
        // Given
        val username = "testuser"
        val email = "test@example.com"
        val password = "password123"
        val user = User(
            id = "user1",
            username = username,
            email = email,
            isPremium = false,
            preferences = UserPreferences()
        )
        val expectedResult = AuthResult.Success(user)
        
        whenever(authRepository.createAccount(username, email, password)).thenReturn(expectedResult)
        
        // When
        val result = createAccountUseCase(username, email, password)
        
        // Then
        assertTrue(result is AuthResult.Success)
        assertEquals(expectedResult, result)
    }
    
    @Test
    fun `invoke with valid data but repository error returns error`() = runTest {
        // Given
        val username = "testuser"
        val email = "test@example.com"
        val password = "password123"
        val errorMessage = "Erreur lors de la création du compte"
        val expectedResult = AuthResult.Error(errorMessage)
        
        whenever(authRepository.createAccount(username, email, password)).thenReturn(expectedResult)
        
        // When
        val result = createAccountUseCase(username, email, password)
        
        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals(errorMessage, (result as AuthResult.Error).message)
    }
} 