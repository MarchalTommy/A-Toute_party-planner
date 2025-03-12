package com.martodev.atoute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.martodev.atoute.home.presentation.HomeEntry
import com.martodev.atoute.party.presentation.PartyEntry
import com.martodev.atoute.party.presentation.PartyEntry.partyDetailScreen
import com.martodev.atoute.ui.theme.ATouteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ATouteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Navigation principale de l'application
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = "home_graph"
                    ) {
                        // Intégration du graphe de navigation Home
                        navigation(
                            startDestination = HomeEntry.Routes.HOME,
                            route = "home_graph"
                        ) {
                            // Écran d'accueil principale de Home
                            composable(HomeEntry.Routes.HOME) {
                                HomeEntry.HomeScreen(
                                    onNavigateToParty = { partyId ->
                                        // Naviguer vers le détail de la Party via le module Party
                                        navController.navigate(PartyEntry.Routes.partyDetail(partyId))
                                    }
                                )
                            }
                        }
                        
                        // Intégration de l'écran de détail Party
                        partyDetailScreen(
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}