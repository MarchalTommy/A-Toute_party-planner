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
class UpdateUserPreferencesUseCaseTest {
    
    private lateinit var authRepository: AuthRepository
    private lateinit var updateUserPreferencesUseCase: UpdateUserPreferencesUseCase
    
    @Before
    fun setUp() {
        authRepository = mock()
        updateUserPreferencesUseCase = UpdateUserPreferencesUseCase(authRepository)
    }
    
    @Test
    fun `invoke with empty userId returns error`() = runTest {
        // Given
        val preferences = UserPreferences(
            drinksAlcohol = true,
            isHalal = false,
            isVegetarian = false,
            isVegan = false,
            hasAllergies = listOf()
        )
        
        // When
        val result = updateUserPreferencesUseCase("", preferences)
        
        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("L'ID utilisateur ne peut pas être vide", (result as AuthResult.Error).message)
    }
    
    @Test
    fun `invoke with valid userId and preferences returns success from repository`() = runTest {
        // Given
        val userId = "user1"
        val preferences = UserPreferences(
            drinksAlcohol = true,
            isHalal = false,
            isVegetarian = true,
            isVegan = false,
            hasAllergies = listOf("Arachides")
        )
        val user = User(
            id = userId,
            username = "testuser",
            email = "test@example.com",
            isPremium = false,
            preferences = preferences
        )
        val expectedResult = AuthResult.Success(user)
        
        whenever(authRepository.updateUserPreferences(userId, preferences)).thenReturn(expectedResult)
        
        // When
        val result = updateUserPreferencesUseCase(userId, preferences)
        
        // Then
        assertTrue(result is AuthResult.Success)
        assertEquals(expectedResult, result)
    }
    
    @Test
    fun `invoke with valid userId but repository error returns error`() = runTest {
        // Given
        val userId = "user1"
        val preferences = UserPreferences(
            drinksAlcohol = true,
            isHalal = false,
            isVegetarian = true,
            isVegan = false,
            hasAllergies = listOf("Arachides")
        )
        val errorMessage = "Erreur lors de la mise à jour des préférences"
        val expectedResult = AuthResult.Error(errorMessage)
        
        whenever(authRepository.updateUserPreferences(userId, preferences)).thenReturn(expectedResult)
        
        // When
        val result = updateUserPreferencesUseCase(userId, preferences)
        
        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals(errorMessage, (result as AuthResult.Error).message)
    }
} 