package com.martodev.atoute.home.presentation

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel

/**
 * Point d'entrée pour le module Home, exposant l'écran d'accueil Home
 */
object HomeEntry {

    /**
     * Noms des routes pour la navigation
     */
    object Routes {
        const val HOME = "home"
    }

    /**
     * Écran d'accueil principal, exposé pour être utilisé par l'application
     */
    @Composable
    fun HomeScreen(
        onNavigateToParty: (String) -> Unit = {}
    ) {
        // Utilisation directe du composant HomeScreen
        HomeScreen(
            viewModel = koinViewModel(),
            onPartyClick = onNavigateToParty
        )
    }
} 