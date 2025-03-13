package com.martodev.atoute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.martodev.atoute.authentication.domain.usecase.GetCurrentUserUseCase
import com.martodev.atoute.authentication.presentation.AuthEntry
import com.martodev.atoute.authentication.presentation.AuthEntry.authScreens
import com.martodev.atoute.party.presentation.PartyEntry
import com.martodev.atoute.party.presentation.PartyEntry.partyDetailScreen
import com.martodev.atoute.ui.theme.ATouteTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val getCurrentUserUseCase: GetCurrentUserUseCase by inject()

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

                    // Récupérer l'utilisateur actuel
                    val currentUser by getCurrentUserUseCase().collectAsState(initial = null)

                    // Définir la destination de départ en fonction de l'utilisateur
                    val startDestination = if (currentUser != null) {
                        "main_screen"
                    } else {
                        "auth_graph"
                    }

                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        // Intégration du graphe de navigation Auth
                        navigation(
                            startDestination = AuthEntry.Routes.AUTH,
                            route = "auth_graph"
                        ) {
                            // Écran d'authentification
                            authScreens(
                                onAuthSuccess = {
                                    navController.navigate("main_screen") {
                                        popUpTo("auth_graph") { inclusive = true }
                                    }
                                },
                                // Nous avons supprimé le bouton des préférences, mais gardons ce paramètre pour la compatibilité
                                onNavigateToPreferences = { }
                            )
                        }

                        // Écran principal avec bottom navigation bar
                        composable(route = "main_screen") {
                            MainScreen(
                                onNavigateToPartyDetail = { partyId ->
                                    navController.navigate(PartyEntry.Routes.partyDetail(partyId))
                                },
                                onNavigateToAuth = {
                                    // Pour la déconnexion éventuelle
                                    navController.navigate("auth_graph") {
                                        popUpTo("main_screen") { inclusive = true }
                                    }
                                }
                            )
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