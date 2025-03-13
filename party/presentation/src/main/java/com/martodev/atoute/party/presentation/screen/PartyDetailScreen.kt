package com.martodev.atoute.party.presentation.screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.martodev.atoute.party.presentation.components.CreateTodoDialog
import com.martodev.atoute.party.presentation.components.CreateToBuyDialog
import com.martodev.atoute.party.domain.model.Party
import com.martodev.atoute.party.domain.model.ToBuy
import com.martodev.atoute.party.domain.model.Todo
import com.martodev.atoute.party.presentation.components.QrCodeShareDialog
import com.martodev.atoute.party.presentation.viewmodel.PartyDetailViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Écran de détail d'une Party
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyDetailScreen(
    partyId: String,
    viewModel: PartyDetailViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Ajout de logs pour le débogage
    Log.d("PartyDetailScreen", "Écran de détail chargé avec ID: $partyId")
    Log.d("PartyDetailScreen", "État UI actuel: isLoading=${uiState.isLoading}, hasParty=${uiState.party != null}, hasError=${uiState.error != null}")
    
    // Gérer les Snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }
    
    // Gérer les messages d'erreur
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            // Ne pas effacer l'erreur automatiquement pour le moment
        }
    }
    
    // On charge les détails de la Party au chargement de l'écran
    LaunchedEffect(partyId) {
        Log.d("PartyDetailScreen", "LaunchedEffect déclenché pour charger les détails de la Party")
//        viewModel.loadPartyDetails(partyId)
    }
    
    // Animation d'entrée et de sortie
    AnimatedVisibility(
        visible = uiState.party != null,
        enter = fadeIn() + slideInVertically { it },
        exit = fadeOut() + slideOutVertically { it }
    ) {
        val party = uiState.party!!
        
        Log.d("PartyDetailScreen", "Affichage de la Party: ${party.title}")
        
        // On détermine la couleur d'accent à utiliser
        val accentColor = remember(party.id) {
            // Utiliser directement la couleur de la party si elle existe
            if (party.color != null) {
                Color(party.color!!)
            } else {
                // Couleur par défaut si aucune couleur n'est définie pour la party
                Color(0xFF6200EE) // Couleur primaire par défaut
            }
        }
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(party.title) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = accentColor,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            PartyDetailContent(
                modifier = Modifier.padding(paddingValues),
                party = party,
                todos = uiState.todos,
                toBuys = uiState.toBuys,
                accentColor = accentColor,
                calculateDaysUntil = viewModel::calculateDaysUntil,
                onTodoStatusChange = viewModel::updateTodoStatus,
                onToBuyStatusChange = viewModel::updateToBuyStatus,
                onTodoPriorityChange = viewModel::updateTodoPriority,
                onToBuyPriorityChange = viewModel::updateToBuyPriority,
                onMapClick = { /* Non implémenté dans ce module */ },
                onDirectionsClick = { /* Non implémenté dans ce module */ },
                onAddTodo = viewModel::addTodo,
                onAddToBuy = viewModel::addToBuy,
                onShareEvent = viewModel::shareEvent
            )
            
            // Affichage du dialogue de partage si nécessaire
            if (uiState.isShareDialogVisible) {
                val shareData = viewModel.generateEventShareData()
                if (shareData != null) {
                    QrCodeShareDialog(
                        eventData = shareData,
                        onDismiss = { viewModel.hideShareDialog() }
                    )
                }
            }
        }
    }
    
    // Affichage de l'état de chargement
    if (uiState.isLoading) {
        Log.d("PartyDetailScreen", "Affichage de l'indicateur de chargement")
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    
    // Affichage de l'erreur si nécessaire
    uiState.error?.let { error ->
        Log.d("PartyDetailScreen", "Affichage de l'erreur: $error")
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Erreur: $error",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun PartyDetailContent(
    modifier: Modifier = Modifier,
    party: Party,
    todos: List<Todo>,
    toBuys: List<ToBuy>,
    accentColor: Color,
    calculateDaysUntil: (LocalDateTime) -> Long,
    onTodoStatusChange: (String, Boolean) -> Unit,
    onToBuyStatusChange: (String, Boolean) -> Unit,
    onTodoPriorityChange: (String, Boolean) -> Unit,
    onToBuyPriorityChange: (String, Boolean) -> Unit,
    onMapClick: () -> Unit,
    onDirectionsClick: () -> Unit,
    onAddTodo: (String, String?) -> Unit,
    onAddToBuy: (String, Int, Float?, String?) -> Unit,
    onShareEvent: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Carte principale avec les informations de base
        PartyInfoCard(
            party = party,
            daysUntil = calculateDaysUntil(party.date),
            accentColor = accentColor
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Onglets pour naviguer entre les différentes sections
        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("Détails", "Participants", "Tâches", "Achats")
        
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = accentColor
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { 
                        Text(
                            text = title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            softWrap = false
                        ) 
                    },
                    selectedContentColor = accentColor,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Contenu basé sur l'onglet sélectionné
        when (selectedTabIndex) {
            0 -> PartyDetailsTab(party = party, onMapClick = onMapClick, onDirectionsClick = onDirectionsClick, onShareEvent = onShareEvent)
            1 -> PartyParticipantsTab(participants = party.participants)
            2 -> PartyTodosTab(
                todos = todos, 
                onTodoStatusChange = onTodoStatusChange,
                onTodoPriorityChange = onTodoPriorityChange,
                onAddTodo = onAddTodo
            )
            3 -> PartyToBuysTab(
                toBuys = toBuys,
                onToBuyStatusChange = onToBuyStatusChange,
                onToBuyPriorityChange = onToBuyPriorityChange,
                onAddToBuy = onAddToBuy
            )
        }
    }
}

@Composable
private fun PartyInfoCard(
    party: Party,
    daysUntil: Long,
    accentColor: Color
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = party.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Date avec countdown
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null,
                    tint = accentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                
                Column {
                    Text(
                        text = party.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    // Countdown stylisé comme dans HomeScreen
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = when {
                                daysUntil <= 0 -> "Aujourd'hui !"
                                daysUntil == 1L -> "Demain"
                                daysUntil < 30 -> "$daysUntil jours restants"
                                else -> "Plus d'un mois"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Localisation
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = null,
                    tint = accentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = party.location,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Barre de progression des tâches
            if (party.todoCount > 0) {
                LinearProgressIndicator(
                    progress = { party.completedTodoCount.toFloat() / party.todoCount },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = accentColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Texte indiquant le nombre de tâches restantes
                val remainingTasks = party.todoCount - party.completedTodoCount
                Text(
                    text = when (remainingTasks) {
                        0 -> "Tout est prêt !"
                        1 -> "1 tâche restante"
                        else -> "$remainingTasks tâches restantes"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PartyDetailsTab(
    party: Party,
    onMapClick: () -> Unit,
    onDirectionsClick: () -> Unit,
    onShareEvent: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Description
        if (party.description.isNotEmpty()) {
            Text(
                text = "À propos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = party.description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Adresse
        Text(
            text = "Lieu",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = party.location,
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            ElevatedCard(
                onClick = onMapClick,
                modifier = Modifier.weight(1f),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Place,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Voir la carte")
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            ElevatedCard(
                onClick = onDirectionsClick,
                modifier = Modifier.weight(1f),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Place,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Itinéraire")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Bouton de partage
        ElevatedCard(
            onClick = onShareEvent,
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Partager cet événement", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PartyParticipantsTab(
    participants: List<String>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${participants.size} participant${if (participants.size > 1) "s" else ""}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        if (participants.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun participant pour le moment",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(participants) { participant ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar (placeholder)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = participant.first().toString(),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = participant,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PartyTodosTab(
    todos: List<Todo>,
    onTodoStatusChange: (String, Boolean) -> Unit,
    onTodoPriorityChange: (String, Boolean) -> Unit,
    onAddTodo: (String, String?) -> Unit
) {
    var showAddTodoDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tâches à accomplir",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Button(
                onClick = { showAddTodoDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Ajouter une tâche"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ajouter")
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(todos) { todo ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = todo.isCompleted,
                            onCheckedChange = { isChecked -> onTodoStatusChange(todo.id, isChecked) }
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = todo.title,
                                style = MaterialTheme.typography.bodyLarge,
                                textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null
                            )
                            
                            if (todo.assignedTo != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Assigné à: ${todo.assignedTo}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        // Bouton pour marquer comme prioritaire
                        IconButton(
                            onClick = { onTodoPriorityChange(todo.id, !todo.isPriority) }
                        ) {
                            Icon(
                                imageVector = if (todo.isPriority) Icons.Filled.AddCircle else Icons.Outlined.AddCircle,
                                contentDescription = if (todo.isPriority) "Retirer des priorités" else "Ajouter aux priorités",
                                tint = if (todo.isPriority) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
    
    if (showAddTodoDialog) {
        CreateTodoDialog(
            onDismiss = { showAddTodoDialog = false },
            onCreateTodo = { title, assignedTo ->
                onAddTodo(title, assignedTo)
                showAddTodoDialog = false
            },
            participants = emptyList()  // Utilisez la liste de participants du party si disponible
        )
    }
}

@Composable
private fun PartyToBuysTab(
    toBuys: List<ToBuy>,
    onToBuyStatusChange: (String, Boolean) -> Unit,
    onToBuyPriorityChange: (String, Boolean) -> Unit,
    onAddToBuy: (String, Int, Float?, String?) -> Unit
) {
    var showAddToBuyDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Articles à acheter",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Button(
                onClick = { showAddToBuyDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Ajouter un article"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ajouter")
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(toBuys) { toBuy ->
                // Utiliser l'opérateur ?.let pour éviter le smart cast
                val accentColor = toBuy.partyColor?.let { Color(it) } ?: MaterialTheme.colorScheme.primary
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Bande verticale colorée
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(36.dp)
                                .background(accentColor)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Icon(
                            imageVector = if (toBuy.isPurchased) Icons.Filled.CheckCircle else Icons.Outlined.ShoppingCart,
                            contentDescription = null,
                            tint = accentColor
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = toBuy.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f),
                                    textDecoration = if (toBuy.isPurchased) TextDecoration.LineThrough else null
                                )
                                
                                // Affichage de la quantité
                                if (toBuy.quantity > 1) {
                                    Text(
                                        text = "×${toBuy.quantity}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            Row {
                                if (toBuy.estimatedPrice != null) {
                                    Text(
                                        text = "Prix estimé: ${toBuy.estimatedPrice}€",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    
                                    if (toBuy.assignedTo != null) {
                                        Text(
                                            text = " • ",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                
                                if (toBuy.assignedTo != null) {
                                    Text(
                                        text = "Assigné à: ${toBuy.assignedTo}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        
                        // Bouton pour marquer comme prioritaire
                        IconButton(
                            onClick = { onToBuyPriorityChange(toBuy.id, !toBuy.isPriority) }
                        ) {
                            Icon(
                                imageVector = if (toBuy.isPriority) Icons.Filled.AddCircle else Icons.Outlined.AddCircle,
                                contentDescription = if (toBuy.isPriority) "Retirer des priorités" else "Ajouter aux priorités",
                                tint = if (toBuy.isPriority) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Checkbox(
                            checked = toBuy.isPurchased,
                            onCheckedChange = { isChecked -> onToBuyStatusChange(toBuy.id, isChecked) }
                        )
                    }
                }
            }
        }
    }
    
    if (showAddToBuyDialog) {
        CreateToBuyDialog(
            onDismiss = { showAddToBuyDialog = false },
            onCreateToBuy = { title, quantity, price, assignedTo ->
                onAddToBuy(title, quantity, price, assignedTo)
                showAddToBuyDialog = false
            },
            participants = emptyList()  // Utilisez la liste de participants du party si disponible
        )
    }
} 