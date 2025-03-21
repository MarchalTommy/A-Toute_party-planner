package com.martodev.atoute.authentication.data.datasource

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.martodev.atoute.core.data.dao.UserDao
import com.martodev.atoute.core.data.db.ATouteDatabase
import com.martodev.atoute.core.data.entity.UserEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class UserDaoTest {
    
    private lateinit var userDao: UserDao
    private lateinit var db: ATouteDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ATouteDatabase::class.java
        ).build()
        userDao = db.userDao()
    }
    
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
    
    @Test
    fun insertAndGetUserById() = runTest {
        // Given
        val user = UserEntity(
            id = "user1",
            username = "testuser",
            email = "test@example.com",
            isPremium = false,
            drinksAlcohol = true,
            isHalal = false,
            isVegetarian = false,
            isVegan = false,
            allergies = ""
        )
        
        // When
        userDao.insertUser(user)
        val retrievedUser = userDao.getUserById("user1").first()
        
        // Then
        assertNotNull(retrievedUser)
        assertEquals(user.id, retrievedUser?.id)
        assertEquals(user.username, retrievedUser?.username)
        assertEquals(user.email, retrievedUser?.email)
        assertEquals(user.isPremium, retrievedUser?.isPremium)
    }
    
    @Test
    fun getUserByIdReturnsNullWhenUserDoesNotExist() = runTest {
        // When
        val retrievedUser = userDao.getUserById("nonexistent").first()
        
        // Then
        assertNull(retrievedUser)
    }
    
    @Test
    fun getUserByEmail() = runTest {
        // Given
        val user = UserEntity(
            id = "user1",
            username = "testuser",
            email = "test@example.com",
            isPremium = false,
            drinksAlcohol = true,
            isHalal = false,
            isVegetarian = false,
            isVegan = false,
            allergies = ""
        )
        userDao.insertUser(user)
        
        // When
        val retrievedUser = userDao.getUserByEmail("test@example.com")
        
        // Then
        assertNotNull(retrievedUser)
        assertEquals(user.id, retrievedUser?.id)
        assertEquals(user.username, retrievedUser?.username)
        assertEquals(user.email, retrievedUser?.email)
    }
    
    @Test
    fun getUserByEmailReturnsNullWhenUserDoesNotExist() = runTest {
        // When
        val retrievedUser = userDao.getUserByEmail("nonexistent@example.com")
        
        // Then
        assertNull(retrievedUser)
    }
    
    @Test
    fun updateUser() = runTest {
        // Given
        val user = UserEntity(
            id = "user1",
            username = "testuser",
            email = "test@example.com",
            isPremium = false,
            drinksAlcohol = true,
            isHalal = false,
            isVegetarian = false,
            isVegan = false,
            allergies = ""
        )
        userDao.insertUser(user)
        
        // When
        val updatedUser = user.copy(
            username = "updateduser",
            isPremium = true,
            isVegetarian = true
        )
        userDao.updateUser(updatedUser)
        val retrievedUser = userDao.getUserById("user1").first()
        
        // Then
        assertNotNull(retrievedUser)
        assertEquals(updatedUser.id, retrievedUser?.id)
        assertEquals(updatedUser.username, retrievedUser?.username)
        assertEquals(updatedUser.email, retrievedUser?.email)
        assertEquals(updatedUser.isPremium, retrievedUser?.isPremium)
        assertEquals(updatedUser.isVegetarian, retrievedUser?.isVegetarian)
    }
    
    @Test
    fun deleteUser() = runTest {
        // Given
        val user = UserEntity(
            id = "user1",
            username = "testuser",
            email = "test@example.com",
            isPremium = false,
            drinksAlcohol = true,
            isHalal = false,
            isVegetarian = false,
            isVegan = false,
            allergies = ""
        )
        userDao.insertUser(user)
        
        // When
        userDao.deleteUser(user)
        val retrievedUser = userDao.getUserById("user1").first()
        
        // Then
        assertNull(retrievedUser)
    }
} 