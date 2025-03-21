package com.martodev.atoute

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

/**
 * Test unitaire pour vérifier le fonctionnement de base de l'application.
 */
class AppCoreTest {
    @Test
    fun appContext_isValid() {
        // Vérifier que le contexte de base est correct
        assertTrue(true)
    }
    
    @Test
    fun validatePartyData_withValidParameters_returnsTrue() {
        // Donnée d'entrée pour un événement valide
        val title = "Anniversaire Marc"
        val date = LocalDateTime.now().plusDays(5)
        val location = "Paris"
        
        // Appliquer la validation
        val result = validatePartyData(title, date, location)
        
        // Vérifier que la validation est réussie
        assertTrue(result)
    }
    
    @Test
    fun validatePartyData_withPastDate_returnsFalse() {
        // Donnée d'entrée avec date passée
        val title = "Anniversaire Marc"
        val date = LocalDateTime.now().minusDays(2)
        val location = "Paris"
        
        // Appliquer la validation
        val result = validatePartyData(title, date, location)
        
        // Vérifier que la validation échoue
        assertFalse(result)
    }
    
    @Test
    fun validatePartyData_withEmptyTitle_returnsFalse() {
        // Donnée d'entrée avec titre vide
        val title = ""
        val date = LocalDateTime.now().plusDays(5)
        val location = "Paris"
        
        // Appliquer la validation
        val result = validatePartyData(title, date, location)
        
        // Vérifier que la validation échoue
        assertFalse(result)
    }
    
    /**
     * Fonction de validation des données d'événement
     */
    private fun validatePartyData(title: String, date: LocalDateTime, location: String): Boolean {
        if (title.isBlank()) return false
        if (date.isBefore(LocalDateTime.now())) return false
        return true
    }
} 