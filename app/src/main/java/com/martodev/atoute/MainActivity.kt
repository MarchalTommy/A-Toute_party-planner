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
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.martodev.atoute.authentication.domain.usecase.GetCurrentUserUseCase
import com.martodev.atoute.authentication.presentation.AuthEntry
import com.martodev.atoute.authentication.presentation.AuthEntry.authScreens
import com.martodev.atoute.authentication.presentation.AuthEntry.userPreferencesScreen
import com.martodev.atoute.home.presentation.HomeEntry
import com.martodev.atoute.home.presentation.HomeEntry.homeScreen
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
                        "home_graph"
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
                                    navController.navigate("home_graph") {
                                        popUpTo("auth_graph") { inclusive = true }
                                    }
                                },
                                onNavigateToPreferences = {
                                    navController.navigate(AuthEntry.Routes.USER_PREFERENCES)
                                }
                            )

                            // Écran des préférences utilisateur
                            userPreferencesScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // Intégration du graphe de navigation Home
                        navigation(
                            startDestination = HomeEntry.Routes.HOME,
                            route = "home_graph"
                        ) {
                            // Écran d'accueil principal
                            homeScreen(
                                onNavigateToParty = { partyId ->
                                    // Naviguer vers le détail de la Party via le module Party
                                    navController.navigate(PartyEntry.Routes.partyDetail(partyId))
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