package com.martodev.atoute.authentication.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.martodev.atoute.authentication.presentation.components.AuthTextField
import com.martodev.atoute.authentication.presentation.viewmodel.UserPreferencesViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Écran des préférences utilisateur
 *
 * @param onNavigateBack Callback appelé pour revenir en arrière
 * @param modifier Modificateur Compose
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPreferencesScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserPreferencesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var newAllergy by remember { mutableStateOf("") }
    
    // Effet pour afficher les erreurs
    LaunchedEffect(state.error) {
        if (state.error != null) {
            // Afficher un message d'erreur (à implémenter)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes préférences") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Préférences alimentaires",
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Préférence pour l'alcool
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Je consomme de l'alcool")
                        Switch(
                            checked = state.preferences.drinksAlcohol,
                            onCheckedChange = { viewModel.updateDrinksAlcohol(it) }
                        )
                    }
                    
                    // Préférence pour le régime halal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Je suis un régime halal")
                        Switch(
                            checked = state.preferences.isHalal,
                            onCheckedChange = { viewModel.updateIsHalal(it) }
                        )
                    }
                    
                    // Préférence pour le régime végétarien
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Je suis végétarien")
                        Switch(
                            checked = state.preferences.isVegetarian,
                            onCheckedChange = { viewModel.updateIsVegetarian(it) }
                        )
                    }
                    
                    // Préférence pour le régime végétalien
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Je suis végétalien")
                        Switch(
                            checked = state.preferences.isVegan,
                            onCheckedChange = { viewModel.updateIsVegan(it) }
                        )
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Allergies",
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Champ pour ajouter une allergie
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AuthTextField(
                            value = newAllergy,
                            onValueChange = { newAllergy = it },
                            label = "Nouvelle allergie",
                            placeholder = "Ex: arachides, lactose...",
                            imeAction = ImeAction.Done,
                            onImeAction = {
                                if (newAllergy.isNotBlank()) {
                                    viewModel.addAllergy(newAllergy)
                                    newAllergy = ""
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        
                        IconButton(
                            onClick = {
                                if (newAllergy.isNotBlank()) {
                                    viewModel.addAllergy(newAllergy)
                                    newAllergy = ""
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Ajouter une allergie"
                            )
                        }
                    }
                }
                
                // Liste des allergies
                items(state.preferences.hasAllergies) { allergy ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(allergy)
                        IconButton(
                            onClick = { viewModel.removeAllergy(allergy) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Supprimer l'allergie"
                            )
                        }
                    }
                }
                
                // Statut premium
                if (state.user?.id?.isNotBlank() == true) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "Statut premium",
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Activer le statut premium")
                            Switch(
                                checked = state.user?.isPremium ?: false,
                                onCheckedChange = { viewModel.updatePremiumStatus(it) }
                            )
                        }
                        
                        if (state.user?.isPremium == true) {
                            Text(
                                text = "Vous bénéficiez des fonctionnalités premium !",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = "Passez à la version premium pour débloquer toutes les fonctionnalités !",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            
            // Indicateur de chargement
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
} 