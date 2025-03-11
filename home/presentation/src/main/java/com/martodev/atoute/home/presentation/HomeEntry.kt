package com.martodev.atoute.home.presentation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.androidx.compose.koinViewModel

/**
 * Point d'entrée pour le module Home, exposant les écrans Home et Party Detail
 */
object HomeEntry {

    /**
     * Noms des routes pour la navigation
     */
    object Routes {
        const val HOME = "home"
        const val PARTY_DETAIL = "party_detail"
        const val PARTY_ID = "partyId"
        
        // Route complète pour l'écran de détail
        const val PARTY_DETAIL_ROUTE = "$PARTY_DETAIL/{$PARTY_ID}"
        
        // Fonction pour créer la route avec l'ID
        fun partyDetail(partyId: String) = "$PARTY_DETAIL/$partyId"
    }

    /**
     * Écran d'accueil principal
     */
    @Composable
    fun HomeScreen(
        onNavigateToAddParty: () -> Unit = {}
    ) {
        val navController = rememberNavController()
        
        // Ajout d'un log pour suivre l'état du NavController
        Log.d("HomeEntry", "NavController créé: ${navController.hashCode()}")
        
        NavHost(
            navController = navController,
            startDestination = Routes.HOME
        ) {
            // Définition du graphe de navigation
            homeNavGraph(
                navController = navController,
                onAddPartyClick = onNavigateToAddParty
            )
        }
    }
    
    /**
     * Configuration du graphe de navigation pour le module Home
     */
    private fun NavGraphBuilder.homeNavGraph(
        navController: androidx.navigation.NavController,
        onAddPartyClick: () -> Unit
    ) {
        // Écran d'accueil
        composable(Routes.HOME) {
            Log.d("HomeEntry", "Navigation vers HOME")
            HomeScreenContent(
                onPartyClick = { partyId ->
                    // Log avant de naviguer
                    Log.d("HomeEntry", "Tentative de navigation vers PARTY_DETAIL avec ID: $partyId")
                    val route = Routes.partyDetail(partyId)
                    Log.d("HomeEntry", "Route générée: $route")
                    navController.navigate(route)
                },
                onAddPartyClick = onAddPartyClick
            )
        }
        
        // Écran de détail d'une Party avec paramètre
        composable(
            route = Routes.PARTY_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(Routes.PARTY_ID) {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            // Extraction de l'ID et log
            val partyId = backStackEntry.arguments?.getString(Routes.PARTY_ID) ?: ""
            Log.d("HomeEntry", "Navigation vers PARTY_DETAIL avec ID récupéré: $partyId")
            
            // Utilisation du viewModel via koinViewModel()
            PartyDetailScreen(
                partyId = partyId,
                viewModel = koinViewModel(),
                onBackClick = {
                    Log.d("HomeEntry", "Retour depuis PARTY_DETAIL")
                    navController.popBackStack()
                },
                onMapClick = { location ->
                    Log.d("HomeEntry", "Clic sur la carte avec location: $location")
                    // À implémenter : ouvrir la carte
                },
                onDirectionsClick = { location ->
                    Log.d("HomeEntry", "Clic sur les directions avec location: $location")
                    // À implémenter : ouvrir l'application de navigation
                }
            )
        }
    }
    
    /**
     * Contenu de l'écran d'accueil
     */
    @Composable
    private fun HomeScreenContent(
        onPartyClick: (String) -> Unit,
        onAddPartyClick: () -> Unit
    ) {
        HomeScreen(
            viewModel = koinViewModel(),
            onPartyClick = onPartyClick,
            onTodoClick = { /* À implémenter */ },
            onAddPartyClick = onAddPartyClick
        )
    }
} 