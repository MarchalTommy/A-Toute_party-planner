package com.martodev.atoute

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.martodev.atoute.authentication.presentation.screen.UserPreferencesScreen
import com.martodev.atoute.home.presentation.HomeScreen
import org.koin.androidx.compose.koinViewModel

/**
 * Écran principal de l'application avec une barre de navigation inférieure
 * et un NavHost pour la navigation entre les écrans.
 *
 * @param onNavigateToPartyDetail Callback pour naviguer vers le détail d'une soirée
 * @param onNavigateToAuth Callback pour naviguer vers l'écran d'authentification (déconnexion)
 */
@Composable
fun MainScreen(
    onNavigateToPartyDetail: (String) -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNav(navController = navController)
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Écran d'accueil
            composable(Screen.Home.route) {
                HomeScreen(
                    onPartyClick = onNavigateToPartyDetail
                )
            }

            // Écran de discussions (à implémenter)
            composable(Screen.Chats.route) {
                ChatPlaceholderScreen()
            }

            // Écran de profil / préférences
            composable(Screen.Profile.route) {
                UserPreferencesScreen(
                    onNavigateBack = { navController.navigateUp() },
                    showBackButton = false
                )
            }
        }
    }
}

/**
 * Écrans disponibles dans la barre de navigation
 */
sealed class Screen(
    val route: String,
    val titleResId: Int,
    val icon: @Composable () -> Unit
) {
    // Accueil
    object Home : Screen(
        route = "home",
        titleResId = R.string.home,
        icon = { Icon(Icons.Filled.Home, contentDescription = null) }
    )

    // Discussions
    object Chats : Screen(
        route = "chats",
        titleResId = R.string.chat,
        icon = { Icon(painterResource(R.drawable.chat), contentDescription = null) }
    )

    // Profil / Préférences
    object Profile : Screen(
        route = "profile",
        titleResId = R.string.profile,
        icon = { Icon(Icons.Filled.Person, contentDescription = null) }
    )
}

/**
 * Composant de barre de navigation inférieure
 *
 * @param navController Contrôleur de navigation
 */
@Composable
fun BottomNav(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Chats,
        Screen.Profile
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            NavigationBarItem(
                icon = { screen.icon() },
                label = { Text(stringResource(id = screen.titleResId)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

/**
 * Écran temporaire pour la section Chats/Discussions
 */
@Composable
fun ChatPlaceholderScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Écran de discussions à implémenter")
    }
} 