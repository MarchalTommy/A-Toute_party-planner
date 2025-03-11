package com.martodev.atoute.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martodev.atoute.home.domain.model.Party
import com.martodev.atoute.home.domain.model.ToBuy
import com.martodev.atoute.home.domain.model.Todo
import com.martodev.atoute.home.domain.usecase.GetPartyDetailUseCase
import com.martodev.atoute.home.domain.usecase.GetToBuysByPartyUseCase
import com.martodev.atoute.home.domain.usecase.GetTodosByPartyUseCase
import com.martodev.atoute.home.domain.usecase.SaveToBuyUseCase
import com.martodev.atoute.home.domain.usecase.SaveTodoUseCase
import com.martodev.atoute.home.domain.usecase.UpdateToBuyPriorityUseCase
import com.martodev.atoute.home.domain.usecase.UpdateToBuyStatusUseCase
import com.martodev.atoute.home.domain.usecase.UpdateTodoPriorityUseCase
import com.martodev.atoute.home.domain.usecase.UpdateTodoStatusUseCase
import com.martodev.atoute.home.presentation.mapper.toPresentation
import com.martodev.atoute.home.presentation.model.Party as PresentationParty
import com.martodev.atoute.home.presentation.model.ToBuy as PresentationToBuy
import com.martodev.atoute.home.presentation.model.Todo as PresentationTodo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

/**
 * ViewModel pour l'écran de détail d'une Party
 */
class PartyDetailViewModel(
    private val getPartyDetailUseCase: GetPartyDetailUseCase,
    private val getTodosByPartyUseCase: GetTodosByPartyUseCase,
    private val getToBuysByPartyUseCase: GetToBuysByPartyUseCase,
    private val updateTodoStatusUseCase: UpdateTodoStatusUseCase,
    private val updateTodoPriorityUseCase: UpdateTodoPriorityUseCase,
    private val updateToBuyStatusUseCase: UpdateToBuyStatusUseCase,
    private val updateToBuyPriorityUseCase: UpdateToBuyPriorityUseCase,
    private val saveTodoUseCase: SaveTodoUseCase,
    private val saveToBuyUseCase: SaveToBuyUseCase
) : ViewModel() {

    // État UI actuel
    private val _uiState = MutableStateFlow(PartyDetailUiState())
    val uiState: StateFlow<PartyDetailUiState> = _uiState.asStateFlow()

    // Couleurs prédéfinies pour les différentes parties (comme dans HomeViewModel)
    private val partyColors = mapOf(
        "Anniversaire de Julie" to 0xFFE91E63, // Rose
        "Soirée Jeux" to 0xFF2196F3,           // Bleu
        "Barbecue d'été" to 0xFF4CAF50         // Vert
    )

    init {
        Log.d("PartyDetailViewModel", "Initialisation du ViewModel")
    }

    /**
     * Charge les détails d'une Party spécifique
     */
    fun loadPartyDetails(partyId: String) {
        Log.d("PartyDetailViewModel", "Chargement des détails pour la Party avec ID: $partyId")
        
        viewModelScope.launch {
            // Indiquons d'abord que nous sommes en chargement
            _uiState.update { it.copy(isLoading = true, error = null) }
            Log.d("PartyDetailViewModel", "État de chargement activé")
            
            // Collecter les détails de la party et convertir en modèle de présentation
            launch {
                getPartyDetailUseCase(partyId).collectLatest { domainParty ->
                    _uiState.update { it.copy(
                        party = domainParty,
                        isLoading = false
                    ) }
                }
            }
            
            // Collecter les todos de la party et convertir en modèles de présentation
            launch {
                getTodosByPartyUseCase(partyId).collectLatest { domainTodos ->
                    _uiState.update { it.copy(
                        todos = domainTodos,
                        isLoading = false
                    ) }
                }
            }
            
            // Collecter les toBuys de la party et convertir en modèles de présentation
            launch {
                getToBuysByPartyUseCase(partyId).collectLatest { domainToBuys ->
                    _uiState.update { it.copy(
                        toBuys = domainToBuys,
                        isLoading = false
                    ) }
                    Log.d("PartyDetailViewModel", "ToBuys chargés: ${domainToBuys.size} éléments")
                }
            }
        }
    }

    /**
     * Met à jour le statut d'une tâche
     */
    fun updateTodoStatus(todoId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            updateTodoStatusUseCase(todoId, isCompleted)
            // La mise à jour du UI State est automatique via les flows
        }
    }

    /**
     * Met à jour le statut d'un article à acheter
     */
    fun updateToBuyStatus(toBuyId: String, isPurchased: Boolean) {
        viewModelScope.launch {
            updateToBuyStatusUseCase(toBuyId, isPurchased)
            // La mise à jour du UI State est automatique via les flows
        }
    }

    /**
     * Met à jour le statut de priorité d'une tâche
     */
    fun updateTodoPriority(todoId: String, isPriority: Boolean) {
        viewModelScope.launch {
            updateTodoPriorityUseCase(todoId, isPriority)
            // La mise à jour du UI State est automatique via les flows
        }
    }
    
    /**
     * Met à jour le statut de priorité d'un achat
     */
    fun updateToBuyPriority(toBuyId: String, isPriority: Boolean) {
        viewModelScope.launch {
            updateToBuyPriorityUseCase(toBuyId, isPriority)
            // La mise à jour du UI State est automatique via les flows
        }
    }

    /**
     * Calcule le nombre de jours restants jusqu'à une date
     */
    fun calculateDaysUntil(date: LocalDateTime): Long {
        val now = LocalDateTime.now()
        return ChronoUnit.DAYS.between(now.toLocalDate(), date.toLocalDate())
    }

    /**
     * Données fictives pour les tests
     */
    private fun getDummyParties(): List<Party> {
        return listOf(
            Party(
                id = "1",
                title = "Anniversaire de Julie",
                date = LocalDateTime.now().plusDays(5),
                location = "Appartement de Julie",
                description = "Fête d'anniversaire pour Julie avec ses amis proches",
                participants = listOf("Marc", "Sophie", "Léa"),
                todoCount = 12,
                completedTodoCount = 5
            ),
            Party(
                id = "2",
                title = "Soirée Jeux",
                date = LocalDateTime.now().plusDays(4),
                location = "Appartement de Thomas",
                description = "Soirée jeux de société entre amis. Chacun peut apporter son jeu préféré.",
                participants = listOf("Claire", "Antoine", "Lucas"),
                todoCount = 8,
                completedTodoCount = 2
            ),
            Party(
                id = "3",
                title = "Barbecue d'été",
                date = LocalDateTime.now().plusDays(12),
                location = "Parc des Buttes-Chaumont",
                description = "Grand barbecue en plein air avec amis et famille",
                participants = listOf("Julie", "Marc", "Sophie", "Antoine"),
                todoCount = 15,
                completedTodoCount = 3
            )
        )
    }

    private fun getDummyTodos(): List<Todo> {
        return listOf(
            // Tâches pour l'anniversaire de Julie (id = "1")
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Acheter un gâteau",
                isCompleted = false,
                assignedTo = "Sophie",
                partyId = "1",
                partyColor = partyColors["Anniversaire de Julie"]
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Acheter les décorations",
                isCompleted = false,
                assignedTo = "Thomas",
                partyId = "1",
                partyColor = partyColors["Anniversaire de Julie"]
            ),
            
            // Tâches pour la soirée jeux (id = "2")
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Apporter Monopoly",
                isCompleted = true,
                assignedTo = "Antoine",
                partyId = "2",
                partyColor = partyColors["Soirée Jeux"]
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Préparer la playlist",
                isCompleted = false,
                assignedTo = "Lucas",
                partyId = "2",
                partyColor = partyColors["Soirée Jeux"]
            ),
            
            // Tâches pour le barbecue d'été (id = "3")
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Réserver les tables de pique-nique",
                isCompleted = false,
                assignedTo = "Marc",
                partyId = "3",
                partyColor = partyColors["Barbecue d'été"]
            ),
            
            // Tâches additionnelles pour les détails - mais cohérentes avec la logique
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Inviter les autres amis",
                isCompleted = true,
                assignedTo = "Léa",
                partyId = "1",
                partyColor = partyColors["Anniversaire de Julie"]
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Préparer les jeux de société",
                isCompleted = false,
                assignedTo = "Claire",
                partyId = "2",
                partyColor = partyColors["Soirée Jeux"]
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Acheter les boissons",
                isCompleted = true,
                assignedTo = "Antoine",
                partyId = "3",
                partyColor = partyColors["Barbecue d'été"]
            )
        )
    }

    private fun getDummyToBuys(): List<ToBuy> {
        return listOf(
            // Articles pour l'anniversaire de Julie
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Gâteau chocolat-framboise",
                quantity = 1,
                estimatedPrice = 35.0f,
                isPurchased = true,
                assignedTo = "Thomas",
                partyId = "1",
                partyColor = partyColors["Anniversaire de Julie"]
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Bouteilles de champagne",
                quantity = 3,
                estimatedPrice = 20.0f,
                isPurchased = false,
                assignedTo = "Emma",
                partyId = "1",
                partyColor = partyColors["Anniversaire de Julie"]
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Ballons",
                quantity = 20,
                estimatedPrice = 5.0f,
                isPurchased = false,
                assignedTo = "Sophie",
                partyId = "1",
                partyColor = partyColors["Anniversaire de Julie"]
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Cadeau surprise",
                quantity = 1,
                estimatedPrice = 50.0f,
                isPurchased = true,
                assignedTo = "Marc",
                partyId = "1",
                partyColor = partyColors["Anniversaire de Julie"]
            ),
            
            // Articles pour la soirée jeux
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Chips variées",
                quantity = 5,
                estimatedPrice = 2.5f,
                isPurchased = false,
                assignedTo = "Lucas",
                partyId = "2",
                partyColor = partyColors["Soirée Jeux"]
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Bouteilles de soda",
                quantity = 4,
                estimatedPrice = 1.5f,
                isPurchased = true,
                assignedTo = "Lucas",
                partyId = "2",
                partyColor = partyColors["Soirée Jeux"]
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Ingrédients pour cocktails",
                quantity = 1,
                estimatedPrice = 25.0f,
                isPurchased = false,
                assignedTo = "Antoine",
                partyId = "2",
                partyColor = partyColors["Soirée Jeux"]
            ),
            
            // Articles pour le barbecue d'été
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Sac de charbon",
                quantity = 2,
                estimatedPrice = 10.0f,
                isPurchased = false,
                assignedTo = "Marc",
                partyId = "3",
                partyColor = partyColors["Barbecue d'été"]
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Viandes diverses",
                quantity = 1,
                estimatedPrice = 50.0f,
                isPurchased = false,
                assignedTo = "Thomas",
                partyId = "3",
                partyColor = partyColors["Barbecue d'été"]
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Salades composées",
                quantity = 3,
                estimatedPrice = 8.0f,
                isPurchased = false,
                assignedTo = "Sophie",
                partyId = "3",
                partyColor = partyColors["Barbecue d'été"]
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Boissons fraîches",
                quantity = 10,
                estimatedPrice = 1.5f,
                isPurchased = false,
                assignedTo = "Claire",
                partyId = "3",
                partyColor = partyColors["Barbecue d'été"]
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Frisbee",
                quantity = 1,
                estimatedPrice = 12.0f,
                isPurchased = false,
                assignedTo = "Emma",
                partyId = "3",
                partyColor = partyColors["Barbecue d'été"]
            )
        )
    }

    /**
     * Ajoute une nouvelle tâche pour cet événement
     */
    fun addTodo(title: String, assignedTo: String? = null) {
        val currentParty = _uiState.value.party ?: return
        
        val newTodo = Todo(
            id = UUID.randomUUID().toString(),
            title = title,
            isCompleted = false,
            assignedTo = assignedTo,
            partyId = currentParty.id,
            partyColor = currentParty.color,
            isPriority = false
        )
        
        viewModelScope.launch {
            saveTodoUseCase(newTodo)
            // La mise à jour du UI State est automatique via les flows
        }
    }
    
    /**
     * Ajoute un nouvel article à acheter pour cet événement
     */
    fun addToBuy(title: String, quantity: Int = 1, estimatedPrice: Float? = null, assignedTo: String? = null) {
        val currentParty = _uiState.value.party ?: return
        
        val newToBuy = ToBuy(
            id = UUID.randomUUID().toString(),
            title = title,
            quantity = quantity,
            estimatedPrice = estimatedPrice,
            isPurchased = false,
            assignedTo = assignedTo,
            partyId = currentParty.id,
            partyColor = currentParty.color,
            isPriority = false
        )
        
        viewModelScope.launch {
            saveToBuyUseCase(newToBuy)
            // La mise à jour du UI State est automatique via les flows
        }
    }
    
    /**
     * Affiche le dialogue de partage d'événement
     */
    fun shareEvent() {
        _uiState.update { it.copy(isShareDialogVisible = true) }
    }
    
    /**
     * Masque le dialogue de partage d'événement
     */
    fun hideShareDialog() {
        _uiState.update { it.copy(isShareDialogVisible = false) }
    }
    
    /**
     * Génère une chaîne de caractères contenant les informations de l'événement
     * pour le partage via QR code
     */
    fun generateEventShareData(): String? {
        val party = _uiState.value.party ?: return null
        
        // Format: ID|TITLE|DATE_ISO|LOCATION|COLOR
        return "${party.id}|${party.title}|${party.date}|${party.location}|${party.color}"
    }

    /**
     * Traite les données partagées via QR code pour rejoindre un événement.
     * Format attendu : ID|TITLE|DATE_ISO|LOCATION|COLOR
     */
    fun processScannedEventData(eventData: String): Boolean {
        try {
            val parts = eventData.split("|")
            if (parts.size < 5) {
                return false
            }
            
            val id = parts[0]
            val title = parts[1]
            val dateStr = parts[2]
            val location = parts[3]
            val colorStr = parts[4]
            
            // Dans une implémentation réelle, vous voudriez:
            // 1. Vérifier si l'événement existe déjà
            // 2. Créer l'événement s'il n'existe pas
            // 3. Ajouter l'utilisateur actuel comme participant
            
            // Nous simulons simplement ici un appel réussi
            Log.d("PartyDetailViewModel", "Événement scanné: $title ($id)")
            
            return true
        } catch (e: Exception) {
            Log.e("PartyDetailViewModel", "Erreur lors du traitement des données de l'événement", e)
            return false
        }
    }
}

/**
 * État de l'interface utilisateur pour l'écran de détail d'une Party
 */
data class PartyDetailUiState(
    val isLoading: Boolean = false,
    val party: Party? = null,
    val todos: List<Todo> = emptyList(),
    val toBuys: List<ToBuy> = emptyList(),
    val error: String? = null,
    val isShareDialogVisible: Boolean = false
) 