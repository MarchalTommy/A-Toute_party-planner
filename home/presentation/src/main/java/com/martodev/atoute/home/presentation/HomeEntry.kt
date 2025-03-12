package com.martodev.atoute.home.presentation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
        const val PARTY_DETAIL = "party_detail" // Conservé pour compatibilité
        const val PARTY_ID = "partyId" // Conservé pour compatibilité
        
        // Route complète pour l'écran de détail (conservé pour compatibilité)
        const val PARTY_DETAIL_ROUTE = "$PARTY_DETAIL/{$PARTY_ID}"
        
        // Fonction pour créer la route avec l'ID (conservé pour compatibilité)
        fun partyDetail(partyId: String) = "$PARTY_DETAIL/$partyId"
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
            onPartyClick = onNavigateToParty,
            onTodoClick = { /* À implémenter */ },
            onAddPartyClick = { /* À implémenter */ }
        )
    }
} 