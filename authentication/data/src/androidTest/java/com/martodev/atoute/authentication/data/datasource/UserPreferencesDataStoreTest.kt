package com.martodev.atoute.authentication.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class UserPreferencesDataStoreTest {
    
    private val testContext: Context = ApplicationProvider.getApplicationContext()
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())
    private val testDataStore = PreferenceDataStoreFactory.create(
        scope = testScope,
        produceFile = { testContext.preferencesDataStoreFile("test_user_preferences") }
    )
    
    private lateinit var userPreferencesDataStore: IUserPreferencesDataStore
    
    @Before
    fun setup() {
        userPreferencesDataStore = TestUserPreferencesDataStore(testDataStore)
    }
    
    @After
    fun tearDown() {
        // Supprimer le fichier de préférences de test
        File(testContext.filesDir, "datastore/test_user_preferences.preferences_pb").delete()
    }
    
    @Test
    fun saveAndGetCurrentUserId() = runTest {
        // Given
        val userId = "user123"
        
        // When
        userPreferencesDataStore.saveCurrentUserId(userId)
        val retrievedUserId = userPreferencesDataStore.getCurrentUserId().first()
        
        // Then
        assertEquals(userId, retrievedUserId)
    }
    
    @Test
    fun saveAndGetCurrentUserName() = runTest {
        // Given
        val userName = "John Doe"
        
        // When
        userPreferencesDataStore.saveCurrentUserName(userName)
        val retrievedUserName = userPreferencesDataStore.getCurrentUserName().first()
        
        // Then
        assertEquals(userName, retrievedUserName)
    }
    
    @Test
    fun clearCurrentUser() = runTest {
        // Given
        val userId = "user123"
        val userName = "John Doe"
        userPreferencesDataStore.saveCurrentUserId(userId)
        userPreferencesDataStore.saveCurrentUserName(userName)
        
        // When
        userPreferencesDataStore.clearCurrentUser()
        val retrievedUserId = userPreferencesDataStore.getCurrentUserId().first()
        val retrievedUserName = userPreferencesDataStore.getCurrentUserName().first()
        
        // Then
        assertNull(retrievedUserId)
        assertNull(retrievedUserName)
    }
} 