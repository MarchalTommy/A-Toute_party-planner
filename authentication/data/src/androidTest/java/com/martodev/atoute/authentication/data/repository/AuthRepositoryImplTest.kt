package com.martodev.atoute.authentication.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.martodev.atoute.authentication.data.datasource.IUserPreferencesDataStore
import com.martodev.atoute.authentication.domain.model.AuthResult
import com.martodev.atoute.authentication.domain.model.UserPreferences
import com.martodev.atoute.core.data.dao.UserDao
import com.martodev.atoute.core.data.db.ATouteDatabase
import com.martodev.atoute.core.data.entity.UserEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AuthRepositoryImplTest {

    private lateinit var userDao: UserDao
    private lateinit var userPreferencesDataStore: IUserPreferencesDataStore
    private lateinit var authRepository: AuthRepositoryImpl
    private lateinit var db: ATouteDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ATouteDatabase::class.java
        ).build()
        userDao = db.userDao()

        // Utiliser un mock pour le DataStore pour éviter les problèmes de persistance
        userPreferencesDataStore = mock()

        // TODO: fix this
        authRepository = AuthRepositoryImpl(
            userDao,
            userPreferencesDataStore,

        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun createAnonymousUser_success() = runTest {
        // Given
        val username = "testuser"
        whenever(userPreferencesDataStore.getCurrentUserId()).thenReturn(flowOf(null))
        whenever(userPreferencesDataStore.getCurrentUserName()).thenReturn(flowOf(null))

        // When
        val result = authRepository.createAnonymousUser(username)

        // Then
        assertTrue(result is AuthResult.Success)
        val user = (result as AuthResult.Success).user
        assertEquals(username, user.username)
        assertNull(user.email)
        assertEquals(false, user.isPremium)
    }

    @Test
    fun createAccount_success() = runTest {
        // Given
        val username = "testuser"
        val email = "test@example.com"
        val password = "password123"
        whenever(userPreferencesDataStore.getCurrentUserId()).thenReturn(flowOf(null))
        whenever(userPreferencesDataStore.getCurrentUserName()).thenReturn(flowOf(null))

        // When
        val result = authRepository.createAccount(username, email, password)

        // Then
        assertTrue(result is AuthResult.Success)
        val user = (result as AuthResult.Success).user
        assertEquals(username, user.username)
        assertEquals(email, user.email)
        assertEquals(false, user.isPremium)
    }

    @Test
    fun createAccount_emailAlreadyExists_returnsError() = runTest {
        // Given
        val username = "testuser"
        val email = "test@example.com"
        val password = "password123"
        val existingUser = UserEntity(
            id = "existing_user",
            username = "existinguser",
            email = email,
            isPremium = false,
            drinksAlcohol = true,
            isHalal = false,
            isVegetarian = false,
            isVegan = false,
            allergies = ""
        )
        userDao.insertUser(existingUser)

        // When
        val result = authRepository.createAccount(username, email, password)

        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("Un compte avec cet email existe déjà", (result as AuthResult.Error).message)
    }

    @Test
    fun signIn_success() = runTest {
        // Given
        val username = "testuser"
        val email = "test@example.com"
        val password = "password123"
        val userId = "user1"
        val user = UserEntity(
            id = userId,
            username = username,
            email = email,
            isPremium = false,
            drinksAlcohol = true,
            isHalal = false,
            isVegetarian = false,
            isVegan = false,
            allergies = ""
        )
        userDao.insertUser(user)

        // When
        val result = authRepository.signIn(email, password)

        // Then
        assertTrue(result is AuthResult.Success)
        val signedInUser = (result as AuthResult.Success).user
        assertEquals(userId, signedInUser.id)
        assertEquals(username, signedInUser.username)
        assertEquals(email, signedInUser.email)
    }

    @Test
    fun signIn_userNotFound_returnsError() = runTest {
        // Given
        val email = "nonexistent@example.com"
        val password = "password123"

        // When
        val result = authRepository.signIn(email, password)

        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("Email ou mot de passe incorrect", (result as AuthResult.Error).message)
    }

    @Test
    fun updateUserPreferences_success() = runTest {
        // Given
        val userId = "user1"
        val username = "testuser"
        val email = "test@example.com"
        val user = UserEntity(
            id = userId,
            username = username,
            email = email,
            isPremium = false,
            drinksAlcohol = true,
            isHalal = false,
            isVegetarian = false,
            isVegan = false,
            allergies = ""
        )
        userDao.insertUser(user)
        whenever(userPreferencesDataStore.getCurrentUserId()).thenReturn(flowOf(userId))
        whenever(userPreferencesDataStore.getCurrentUserName()).thenReturn(flowOf(username))

        val preferences = UserPreferences(
            drinksAlcohol = false,
            isHalal = true,
            isVegetarian = true,
            isVegan = false,
            hasAllergies = listOf("Arachides", "Lactose")
        )

        // When
        val result = authRepository.updateUserPreferences(userId, preferences)

        // Then
        assertTrue(result is AuthResult.Success)
        val updatedUser = (result as AuthResult.Success).user
        assertEquals(userId, updatedUser.id)
        assertEquals(username, updatedUser.username)
        assertEquals(email, updatedUser.email)
        assertNotNull(updatedUser.preferences)
        assertEquals(false, updatedUser.preferences.drinksAlcohol)
        assertEquals(true, updatedUser.preferences.isHalal)
        assertEquals(true, updatedUser.preferences.isVegetarian)
        assertEquals(false, updatedUser.preferences.isVegan)
        assertEquals(listOf("Arachides", "Lactose"), updatedUser.preferences.hasAllergies)
    }

    @Test
    fun updatePremiumStatus_success() = runTest {
        // Given
        val userId = "user1"
        val username = "testuser"
        val email = "test@example.com"
        val user = UserEntity(
            id = userId,
            username = username,
            email = email,
            isPremium = false,
            drinksAlcohol = true,
            isHalal = false,
            isVegetarian = false,
            isVegan = false,
            allergies = ""
        )
        userDao.insertUser(user)
        whenever(userPreferencesDataStore.getCurrentUserId()).thenReturn(flowOf(userId))
        whenever(userPreferencesDataStore.getCurrentUserName()).thenReturn(flowOf(username))

        // When
        val result = authRepository.updatePremiumStatus(userId, true)

        // Then
        assertTrue(result is AuthResult.Success)
        val updatedUser = (result as AuthResult.Success).user
        assertEquals(userId, updatedUser.id)
        assertEquals(username, updatedUser.username)
        assertEquals(email, updatedUser.email)
        assertEquals(true, updatedUser.isPremium)
    }
} 