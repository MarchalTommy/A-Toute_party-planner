package com.martodev.atoute.home.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
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
     * Ajoute l'écran d'accueil au graphe de navigation
     *
     * @param onNavigateToParty Callback appelé lorsqu'un utilisateur clique sur une party
     */
    fun NavGraphBuilder.homeScreen(
        onNavigateToParty: (String) -> Unit
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = koinViewModel(),
                onPartyClick = onNavigateToParty
            )
        }
    }
} 