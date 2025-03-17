@file:OptIn(ExperimentalComposeUiApi::class)

package com.martodev.atoute.authentication.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.martodev.atoute.authentication.presentation.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Modes d'authentification disponibles
 */
private enum class AuthMode {
    ANONYMOUS, // Utilisateur anonyme (juste un pseudo)
    SIGN_UP,   // Création de compte
    SIGN_IN    // Connexion
}

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
    var authMode by remember { mutableStateOf(AuthMode.ANONYMOUS) }
    
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
    
    val title = when (authMode) {
        AuthMode.ANONYMOUS -> "Accès rapide"
        AuthMode.SIGN_UP -> "Créer un compte"
        AuthMode.SIGN_IN -> "Connexion"
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = title) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                windowInsets = WindowInsets.statusBars.only(WindowInsetsSides.Top)
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
                // Titre principal
                Text(
                    text = "Bienvenue sur A-Toute",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Carte principale avec le formulaire
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (authMode) {
                            AuthMode.ANONYMOUS -> {
                                // Formulaire pour utilisateur anonyme (juste le pseudo)
                                OutlinedTextField(
                                    value = username,
                                    onValueChange = { username = it },
                                    label = { Text("Pseudo") },
                                    placeholder = { Text("Entrez votre pseudo") },
                                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                Button(
                                    onClick = { viewModel.createAnonymousUser(username) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = username.isNotBlank() && !state.isLoading,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Continuer sans compte")
                                }
                            }
                            AuthMode.SIGN_UP -> {
                                // Formulaire de création de compte
                                OutlinedTextField(
                                    value = username,
                                    onValueChange = { username = it },
                                    label = { Text("Pseudo") },
                                    placeholder = { Text("Entrez votre pseudo") },
                                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Email") },
                                    placeholder = { Text("Entrez votre email") },
                                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .semantics { 
                                            // Ajouter des informations sémantiques pour les gestionnaires de mots de passe
                                            contentDescription = "Champ email"
                                            testTag = "email-field"
                                        }
                                )
                                
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text("Mot de passe") },
                                    placeholder = { Text("Entrez votre mot de passe") },
                                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = { viewModel.createAccount(username, email, password) }
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .semantics { 
                                            // Ajouter des informations sémantiques pour les gestionnaires de mots de passe
                                            contentDescription = "Champ mot de passe"
                                            testTag = "password-field"
                                        }
                                )
                                
                                Button(
                                    onClick = { viewModel.createAccount(username, email, password) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && !state.isLoading,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Créer un compte")
                                }
                            }
                            AuthMode.SIGN_IN -> {
                                // Formulaire de connexion (sans le pseudo)
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Email") },
                                    placeholder = { Text("Entrez votre email") },
                                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .semantics { 
                                            // Ajouter des informations sémantiques pour les gestionnaires de mots de passe
                                            contentDescription = "Champ email"
                                            testTag = "email-field"
                                        }
                                )
                                
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text("Mot de passe") },
                                    placeholder = { Text("Entrez votre mot de passe") },
                                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = { viewModel.signIn(email, password) }
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .semantics { 
                                            // Ajouter des informations sémantiques pour les gestionnaires de mots de passe
                                            contentDescription = "Champ mot de passe"
                                            testTag = "password-field"
                                        }
                                )
                                
                                Button(
                                    onClick = { viewModel.signIn(email, password) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = email.isNotBlank() && password.isNotBlank() && !state.isLoading,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Se connecter")
                                }
                            }
                        }
                    }
                }
                
                // Boutons de navigation entre les modes
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (authMode) {
                            AuthMode.ANONYMOUS -> {
                                Button(
                                    onClick = { authMode = AuthMode.SIGN_UP },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Text("Créer un compte")
                                }
                                
                                TextButton(
                                    onClick = { authMode = AuthMode.SIGN_IN },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Se connecter")
                                }
                            }
                            AuthMode.SIGN_UP -> {
                                TextButton(
                                    onClick = { authMode = AuthMode.SIGN_IN },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Déjà un compte ? Se connecter")
                                }
                                
                                TextButton(
                                    onClick = { authMode = AuthMode.ANONYMOUS },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Continuer sans compte")
                                }
                            }
                            AuthMode.SIGN_IN -> {
                                TextButton(
                                    onClick = { authMode = AuthMode.SIGN_UP },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Pas de compte ? Créer un compte")
                                }
                                
                                TextButton(
                                    onClick = { authMode = AuthMode.ANONYMOUS },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Continuer sans compte")
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
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
} 