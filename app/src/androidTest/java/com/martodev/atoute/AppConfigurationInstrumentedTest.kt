package com.martodev.atoute

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test instrumenté pour vérifier la configuration de l'application.
 */
@RunWith(AndroidJUnit4::class)
class AppConfigurationInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.martodev.atoute", appContext.packageName)
    }
    
    @Test
    fun verifyApplicationClass() {
        // Vérifier que la classe d'application est correctement configurée
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val application = appContext.applicationContext
        
        // Vérifier que l'application est une instance de ATouteApplication
        assertTrue(application is ATouteApplication)
    }
    
    @Test
    fun verifyAppResources() {
        // Vérifier que les ressources essentielles sont disponibles
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Vérifier quelques ressources importantes
        val appName = appContext.getString(R.string.app_name)
        assertNotNull("Le nom de l'application ne doit pas être null", appName)
        assertFalse("Le nom de l'application ne doit pas être vide", appName.isEmpty())
    }
} 