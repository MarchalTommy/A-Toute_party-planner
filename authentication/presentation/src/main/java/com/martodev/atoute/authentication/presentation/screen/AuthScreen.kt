package com.martodev.atoute.authentication.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.martodev.atoute.authentication.presentation.components.AuthTextField
import com.martodev.atoute.authentication.presentation.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Écran d'authentification
 *
 * @param onAuthSuccess Callback appelé lorsque l'authentification réussit
 * @param onNavigateToPreferences Callback appelé pour naviguer vers l'écran des préférences
 * @param modifier Modificateur Compose
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onNavigateToPreferences: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // État local pour les champs de formulaire
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isCreatingAccount by remember { mutableStateOf(false) }
    
    // Effet pour gérer la navigation après authentification
    LaunchedEffect(state.user) {
        if (state.user != null) {
            onAuthSuccess()
        }
    }
    
    // Effet pour afficher les erreurs
    LaunchedEffect(state.error) {
        if (state.error != null) {
            // Afficher un message d'erreur (à implémenter)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (isCreatingAccount) "Créer un compte" else "Connexion") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Titre
                Text(
                    text = "Bienvenue sur A-Toute",
                    style = MaterialTheme.typography.headlineMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Formulaire
                AuthTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "Pseudo",
                    placeholder = "Entrez votre pseudo",
                    imeAction = if (isCreatingAccount) ImeAction.Next else ImeAction.Done,
                    onImeAction = {
                        if (!isCreatingAccount) {
                            viewModel.createAnonymousUser(username)
                        }
                    }
                )
                
                if (isCreatingAccount) {
                    AuthTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        placeholder = "Entrez votre email",
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                    
                    AuthTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Mot de passe",
                        placeholder = "Entrez votre mot de passe",
                        isPassword = true,
                        imeAction = ImeAction.Done,
                        onImeAction = {
                            viewModel.createAccount(username, email, password)
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Boutons d'action
                if (isCreatingAccount) {
                    Button(
                        onClick = { viewModel.createAccount(username, email, password) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && !state.isLoading
                    ) {
                        Text("Créer un compte")
                    }
                    
                    TextButton(
                        onClick = { isCreatingAccount = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Déjà un compte ? Se connecter")
                    }
                    
                    TextButton(
                        onClick = { 
                            viewModel.createAnonymousUser(username)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continuer sans compte")
                    }
                } else {
                    Button(
                        onClick = { viewModel.createAnonymousUser(username) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = username.isNotBlank() && !state.isLoading
                    ) {
                        Text("Continuer sans compte")
                    }
                    
                    Button(
                        onClick = { isCreatingAccount = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Créer un compte")
                    }
                    
                    TextButton(
                        onClick = { 
                            // Afficher le formulaire de connexion
                            isCreatingAccount = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Se connecter")
                    }
                }
                
                // Bouton pour accéder aux préférences
                OutlinedButton(
                    onClick = onNavigateToPreferences,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Définir mes préférences")
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