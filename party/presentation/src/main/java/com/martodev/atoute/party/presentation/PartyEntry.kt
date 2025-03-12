package com.martodev.atoute.party.presentation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.martodev.atoute.party.presentation.screen.PartyDetailScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Point d'entrée pour le module Party, exposant l'écran de détail de Party
 */
object PartyEntry {

    /**
     * Noms des routes pour la navigation
     */
    object Routes {
        const val PARTY_DETAIL = "party_detail"
        const val PARTY_ID = "partyId"
        
        // Route complète pour l'écran de détail
        const val PARTY_DETAIL_ROUTE = "$PARTY_DETAIL/{$PARTY_ID}"
        
        // Fonction pour créer la route avec l'ID
        fun partyDetail(partyId: String) = "$PARTY_DETAIL/$partyId"
    }

    /**
     * Ajoute l'écran de détail de Party au graphe de navigation
     */
    fun NavGraphBuilder.partyDetailScreen(
        onBackClick: () -> Unit
    ) {
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
            // Extraction de l'ID
            val partyId = backStackEntry.arguments?.getString(Routes.PARTY_ID) ?: ""
            Log.d("PartyEntry", "Navigation vers PARTY_DETAIL avec ID: $partyId")
            
            // Création du ViewModel avec l'ID de la party
            val viewModel = koinViewModel<com.martodev.atoute.party.presentation.viewmodel.PartyDetailViewModel> { 
                parametersOf(partyId) 
            }
            
            // Affichage de l'écran de détail
            PartyDetailScreen(
                partyId = partyId,
                viewModel = viewModel,
                onBackClick = onBackClick
            )
        }
    }
} 