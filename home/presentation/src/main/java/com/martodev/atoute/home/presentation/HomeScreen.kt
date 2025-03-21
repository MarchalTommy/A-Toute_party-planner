package com.martodev.atoute.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.martodev.atoute.home.presentation.model.Party
import com.martodev.atoute.home.presentation.model.Todo
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onPartyClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateEventDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Gérer les messages d'erreur
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            // Ne pas effacer l'erreur automatiquement pour le moment
        }
    }
    
    // Gérer les messages de succès
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Bouton pour scanner le QR code
                FloatingActionButton(
                    onClick = { viewModel.showQrScanDialog() },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.qr_code_scanner),
                        contentDescription = "Scanner un QR code",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
                
                // Bouton pour ajouter un événement
                FloatingActionButton(
                    onClick = { showCreateEventDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Ajouter un événement",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.statusBars.only(WindowInsetsSides.Top)
    ) { paddingValues ->
        HomeContent(
            uiState = uiState,
            onPartyClick = onPartyClick,
            onTodoStatusChange = viewModel::updateTodoStatus,
            calculateDaysUntil = viewModel::calculateDaysUntil,
            modifier = Modifier.padding(paddingValues)
        )

        // Dialogue de création d'événement
        if (showCreateEventDialog) {
            CreateEventDialog(
                onDismiss = { showCreateEventDialog = false },
                onCreateEvent = { title, date, location, color ->
                    viewModel.createEvent(title, date, location, color)
                    showCreateEventDialog = false
                }
            )
        }

        // Dialogue de scan QR code
        if (uiState.isQrScanDialogVisible) {
            QrCodeScanDialog(
                onDismiss = viewModel::hideQrScanDialog,
                onScanCompleted = viewModel::processScannedQrCode
            )
        }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onPartyClick: (String) -> Unit,
    onTodoStatusChange: (String, Boolean) -> Unit,
    calculateDaysUntil: (LocalDateTime) -> Long,
    modifier: Modifier = Modifier
) {
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (uiState.error != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Erreur: ${uiState.error}",
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "Évenements à venir",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (uiState.parties.isEmpty()) {
            item {
                EmptyPartiesCard()
            }
        } else {
            items(uiState.parties) { party ->
                PartyCard(
                    party = party,
                    todos = uiState.todos,
                    daysUntil = calculateDaysUntil(party.date),
                    onClick = { onPartyClick(party.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tâches prioritaires",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (uiState.todos.isEmpty()) {
            item {
                EmptyTodosCard()
            }
        } else {
            items(uiState.todos) { todo ->
                TodoCard(
                    todo = todo,
                    onStatusChange = { isCompleted -> onTodoStatusChange(todo.id, isCompleted) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PartyCard(
    party: Party,
    todos: List<Todo>,
    daysUntil: Long,
    onClick: () -> Unit
) {
    val accentColor = remember(party.id) {
        if (party.color != null) {
            Color(party.color)
        } else {
            val hash = party.id.hashCode()
            Color(
                red = (hash and 0xFF0000 shr 16) / 255f,
                green = (hash and 0x00FF00 shr 8) / 255f,
                blue = (hash and 0x0000FF) / 255f,
                alpha = 1f
            )
        }
    }

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = party.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = party.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when {
                            daysUntil <= 0 -> "Aujourd'hui !"
                            daysUntil == 1L -> "Demain"
                            daysUntil < 30 -> "$daysUntil jours restants"
                            else -> "Plus d'un mois"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = party.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (party.todoCount > 0) {
                Spacer(modifier = Modifier.height(16.dp))

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoCard(
    todo: Todo,
    onStatusChange: (Boolean) -> Unit
) {
    val accentColor = remember(todo.partyId) {
        when {
            todo.partyColor != null -> Color(todo.partyColor)
            else -> {
                val hash = todo.partyId.hashCode()
                Color(
                    red = (hash and 0xFF0000 shr 16) / 255f,
                    green = (hash and 0x00FF00 shr 8) / 255f,
                    blue = (hash and 0x0000FF) / 255f,
                    alpha = 1f
                )
            }
        }
    }

    Card(
        onClick = { onStatusChange(!todo.isCompleted) },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(accentColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (todo.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.AddCircle,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (todo.assignedTo != null) {
                    Text(
                        text = "Assigné à: ${todo.assignedTo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    HorizontalDivider(thickness = 0.5.dp)
}

@Composable
private fun EmptyPartiesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Aucune Party prévue",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Appuyez sur le bouton + pour créer une nouvelle Party",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyTodosCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Aucune tâche prioritaire",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Composant pour afficher un message de succès temporaire
 */
@Composable
private fun SuccessMessageBox(
    message: String,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
        }

        // Fermeture automatique après quelques secondes
        LaunchedEffect(message) {
            delay(3000) // 3 secondes
            onDismiss()
        }
    }
}
