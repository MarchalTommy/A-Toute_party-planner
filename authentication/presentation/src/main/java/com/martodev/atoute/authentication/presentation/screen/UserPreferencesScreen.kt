package com.martodev.atoute.authentication.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
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
    viewModel: UserPreferencesViewModel = koinViewModel(),
    showBackButton: Boolean = true
) {
    val state by viewModel.state.collectAsState()
    var newAllergy by remember { mutableStateOf("") }
    
    // Effet pour afficher les erreurs
    LaunchedEffect(state.error) {
        if (state.error != null) {
            // Afficher un message d'erreur (à implémenter)
        }
    }
    
    // Effet pour le debug des informations utilisateur
    LaunchedEffect(state.user) {
        println("UserPreferencesScreen - User state: ${state.user}")
        println("UserPreferencesScreen - User ID: ${state.user?.id}")
    }
    
    // Vérification plus précise de l'état de connexion
    val isUserLoggedIn = state.user != null && !state.user?.id.isNullOrBlank()
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mon profil") },
                navigationIcon = if (showBackButton) {
                    {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour"
                            )
                        }
                    }
                } else { {} },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                windowInsets = WindowInsets.statusBars.only(WindowInsetsSides.Top)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(layoutDirection = LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(layoutDirection = LayoutDirection.Ltr)
                    // Pas de padding en bas pour éviter la barre blanche
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 80.dp), // Padding en bas pour éviter que le contenu ne soit caché par la bottom bar
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Message d'avertissement pour utilisateur non connecté
                if (!isUserLoggedIn) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Mode prévisualisation",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "Vous n'êtes pas connecté. Vos préférences ne seront pas sauvegardées. Connectez-vous ou créez un compte pour enregistrer vos préférences.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
                
                // Préférences alimentaires
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Préférences alimentaires",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
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
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            
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
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            
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
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            
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
                    }
                }
                
                // Allergies
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Allergies",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Champ pour ajouter une allergie
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newAllergy,
                                    onValueChange = { newAllergy = it },
                                    label = { Text("Nouvelle allergie") },
                                    placeholder = { Text("Ex: arachides, lactose...") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                FilledIconButton(
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
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Liste des allergies
                            if (state.preferences.hasAllergies.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(vertical = 24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Aucune allergie enregistrée",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                Column(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    state.preferences.hasAllergies.forEach { allergy ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.primaryContainer
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = allergy,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                                IconButton(
                                                    onClick = { viewModel.removeAllergy(allergy) }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Supprimer l'allergie",
                                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Statut premium
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Statut premium",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            if (!isUserLoggedIn) {
                                // Message spécifique pour les utilisateurs non connectés
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Connectez-vous pour accéder aux fonctionnalités premium",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                // Switch pour les utilisateurs connectés
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
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                if (state.user?.isPremium == true) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                            .padding(horizontal = 16.dp, vertical = 12.dp)
                                    ) {
                                        Text(
                                            text = "Vous bénéficiez des fonctionnalités premium !",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Passez à la version premium pour débloquer toutes les fonctionnalités !",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Indicateur de chargement
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // Boutons d'action en bas de l'écran
            if (!isUserLoggedIn) {
                // Bouton flottant pour se connecter si l'utilisateur n'est pas connecté
                ExtendedFloatingActionButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    text = { Text("Se connecter") }
                )
            } else {
                // Bouton de déconnexion pour les utilisateurs connectés
                ExtendedFloatingActionButton(
                    onClick = { viewModel.signOut() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    text = { Text("Se déconnecter") }
                )
            }
            
            // Afficher les erreurs dans un Snackbar
            state.error?.let { errorMessage ->
                val snackbarHostState = remember { SnackbarHostState() }
                
                LaunchedEffect(errorMessage) {
                    snackbarHostState.showSnackbar(
                        message = errorMessage,
                        actionLabel = "OK",
                        duration = SnackbarDuration.Short
                    )
                    // Effacer l'erreur après l'affichage
                    viewModel.clearError()
                }
                
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
} 