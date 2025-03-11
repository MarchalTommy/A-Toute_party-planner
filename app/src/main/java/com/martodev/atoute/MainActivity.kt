package com.martodev.atoute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.martodev.atoute.home.presentation.HomeEntry
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
                    // Utilisation de l'écran d'accueil avec la navigation interne gérée par HomeEntry
                    HomeEntry.HomeScreen(
                        onNavigateToAddParty = {
                            // Dans le futur, on pourrait naviguer vers un écran d'ajout de Party
                            // Pour l'instant, c'est un placeholder
                        }
                    )
                }
            }
        }
    }
}