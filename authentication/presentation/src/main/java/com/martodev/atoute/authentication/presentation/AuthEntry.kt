package com.martodev.atoute.authentication.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.martodev.atoute.authentication.presentation.screen.AuthScreen
import com.martodev.atoute.authentication.presentation.screen.UserPreferencesScreen
import org.koin.androidx.compose.koinViewModel

/**
 * Point d'entrée du module d'authentification
 */
object AuthEntry {

    /**
     * Routes pour la navigation
     */
    object Routes {
        const val AUTH = "auth"
        const val USER_PREFERENCES = "user_preferences"
    }

    /**
     * Ajoute les écrans d'authentification au graphe de navigation
     *
     * @param navController Contrôleur de navigation
     * @param onAuthSuccess Callback appelé lorsque l'authentification réussit
     */
    fun NavGraphBuilder.authScreens(
        onAuthSuccess: () -> Unit,
        onNavigateToPreferences: () -> Unit
    ) {
        composable(Routes.AUTH) {
            AuthScreen(
                onAuthSuccess = onAuthSuccess,
                onNavigateToPreferences = onNavigateToPreferences,
                viewModel = koinViewModel()
            )
        }
    }

    /**
     * Ajoute l'écran des préférences utilisateur au graphe de navigation
     *
     * @param onNavigateBack Callback appelé pour revenir en arrière
     */
    fun NavGraphBuilder.userPreferencesScreen(
        onNavigateBack: () -> Unit
    ) {
        composable(Routes.USER_PREFERENCES) {
            UserPreferencesScreen(
                onNavigateBack = onNavigateBack,
                viewModel = koinViewModel()
            )
        }
    }
} 