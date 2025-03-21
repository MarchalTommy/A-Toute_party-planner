package com.martodev.atoute.core.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class ValidationUtilsTest {
    
    @Test
    fun validatePartyData_withValidParameters_returnsTrue() {
        // Given
        val title = "Anniversaire"
        val date = LocalDateTime.now().plusDays(2)
        val location = "Paris"
        
        // When
        val result = ValidationUtils.validatePartyData(title, date, location)
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun validatePartyData_withEmptyTitle_returnsFalse() {
        // Given
        val title = ""
        val date = LocalDateTime.now().plusDays(2)
        val location = "Paris"
        
        // When
        val result = ValidationUtils.validatePartyData(title, date, location)
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun validatePartyData_withPastDate_returnsFalse() {
        // Given
        val title = "Anniversaire"
        val date = LocalDateTime.now().minusDays(1)
        val location = "Paris"
        
        // When
        val result = ValidationUtils.validatePartyData(title, date, location)
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun validatePartyData_withEmptyLocation_returnsFalse() {
        // Given
        val title = "Anniversaire"
        val date = LocalDateTime.now().plusDays(2)
        val location = ""
        
        // When
        val result = ValidationUtils.validatePartyData(title, date, location)
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun validateTodoData_withValidParameters_returnsTrue() {
        // Given
        val title = "Acheter gâteau"
        val partyId = "party123"
        
        // When
        val result = ValidationUtils.validateTodoData(title, partyId)
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun validateTodoData_withEmptyTitle_returnsFalse() {
        // Given
        val title = ""
        val partyId = "party123"
        
        // When
        val result = ValidationUtils.validateTodoData(title, partyId)
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun validateTodoData_withEmptyPartyId_returnsFalse() {
        // Given
        val title = "Acheter gâteau"
        val partyId = ""
        
        // When
        val result = ValidationUtils.validateTodoData(title, partyId)
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun validateToBuyData_withValidParameters_returnsTrue() {
        // Given
        val title = "Bouteilles d'eau"
        val quantity = 5
        val partyId = "party123"
        
        // When
        val result = ValidationUtils.validateToBuyData(title, quantity, partyId)
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun validateToBuyData_withEmptyTitle_returnsFalse() {
        // Given
        val title = ""
        val quantity = 5
        val partyId = "party123"
        
        // When
        val result = ValidationUtils.validateToBuyData(title, quantity, partyId)
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun validateToBuyData_withZeroQuantity_returnsFalse() {
        // Given
        val title = "Bouteilles d'eau"
        val quantity = 0
        val partyId = "party123"
        
        // When
        val result = ValidationUtils.validateToBuyData(title, quantity, partyId)
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun validateToBuyData_withEmptyPartyId_returnsFalse() {
        // Given
        val title = "Bouteilles d'eau"
        val quantity = 5
        val partyId = ""
        
        // When
        val result = ValidationUtils.validateToBuyData(title, quantity, partyId)
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun validateUserPreferences_withValidUsername_returnsTrue() {
        // Given
        val username = "JohnDoe"
        
        // When
        val result = ValidationUtils.validateUserPreferences(username)
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun validateUserPreferences_withEmptyUsername_returnsFalse() {
        // Given
        val username = ""
        
        // When
        val result = ValidationUtils.validateUserPreferences(username)
        
        // Then
        assertFalse(result)
    }
} 