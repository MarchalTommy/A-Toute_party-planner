package com.martodev.atoute.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martodev.atoute.home.domain.service.AuthService
import com.martodev.atoute.home.domain.usecase.CheckPartyLimitUseCase
import com.martodev.atoute.home.domain.usecase.GetPartiesUseCase
import com.martodev.atoute.home.domain.usecase.GetPriorityTodosUseCase
import com.martodev.atoute.home.domain.usecase.SavePartyUseCase
import com.martodev.atoute.home.domain.usecase.SaveToBuyUseCase
import com.martodev.atoute.home.domain.usecase.SaveTodoUseCase
import com.martodev.atoute.home.domain.usecase.UpdateTodoStatusUseCase
import com.martodev.atoute.home.presentation.mapper.toPresentation
import com.martodev.atoute.home.presentation.model.Party
import com.martodev.atoute.home.presentation.model.Todo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID
import com.martodev.atoute.home.domain.model.Party as DomainParty

/**
 * ViewModel pour l'écran d'accueil qui gère les données à afficher
 */
class HomeViewModel(
    private val getPartiesUseCase: GetPartiesUseCase,
    private val getPriorityTodosUseCase: GetPriorityTodosUseCase,
    private val savePartyUseCase: SavePartyUseCase,
    private val updateTodoStatusUseCase: UpdateTodoStatusUseCase,
    private val authService: AuthService,
    private val checkPartyLimitUseCase: CheckPartyLimitUseCase,
    private val saveTodoUseCase: SaveTodoUseCase,
    private val saveToBuyUseCase: SaveToBuyUseCase
) : ViewModel() {

    // État UI actuel
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    /**
     * Charge les données initiales (parties et todos)
     */
    private fun loadData() {
        loadParties()
        loadPriorityTodos()
    }

    /**
     * Met à jour le statut d'un todo
     */
    fun updateTodoStatus(todoId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            updateTodoStatusUseCase(todoId, isCompleted)
            // La mise à jour du UI State est automatique via les flows
        }
    }

    /**
     * Calcule le nombre de jours entre aujourd'hui et une date
     */
    fun calculateDaysUntil(date: LocalDateTime): Long {
        val now = LocalDateTime.now()
        return ChronoUnit.DAYS.between(now.toLocalDate(), date.toLocalDate())
    }

    /**
     * Charge la liste des événements
     */
    fun loadParties() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                getPartiesUseCase().collectLatest { parties ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            parties = parties.map { party -> party.toPresentation() }
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    /**
     * Charge la liste des tâches prioritaires
     */
    private fun loadPriorityTodos() {
        viewModelScope.launch {
            try {
                getPriorityTodosUseCase().collectLatest { todos ->
                    _uiState.update {
                        it.copy(
                            todos = todos.map { todo -> todo.toPresentation() }
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    /**
     * Crée un nouvel événement avec les détails fournis
     */
    fun createEvent(title: String, date: LocalDateTime, location: String, color: Long) {
        viewModelScope.launch {
            // Récupérer l'ID de l'utilisateur courant
            val currentUserId = authService.getCurrentUserId() ?: return@launch
            
            // Créer un nouveau Party avec les informations fournies (en utilisant le modèle de domaine)
            val newParty = DomainParty(
                id = UUID.randomUUID().toString(),
                title = title,
                date = date,
                location = location,
                color = color
            )

            try {
                // Sauvegarder dans le repository
                val partyId = savePartyUseCase(newParty)

                // Enregistrer l'utilisateur courant comme propriétaire et participant
                // Récupérer le nom de l'utilisateur ou utiliser "Moi" par défaut
                val userName = "Moi" // Peut être remplacé par le nom réel de l'utilisateur si disponible
                authService.registerCurrentUserAsOwner(partyId, userName)
                
                // Mettre à jour la liste des événements
                loadParties()
            } catch (e: SavePartyUseCase.PartyLimitReachedException) {
                // Afficher l'erreur de limite d'événements atteinte
                _uiState.update { it.copy(error = e.message) }
            } catch (e: Exception) {
                // Gérer les autres erreurs
                _uiState.update { it.copy(error = "Erreur lors de la création de l'événement: ${e.message}") }
            }
        }
    }

    /**
     * Vérifie si l'utilisateur courant est propriétaire d'un événement
     */
    fun isCurrentUserOwnerOfParty(partyId: String): Boolean {
        return authService.isCurrentUserOwnerOfParty(partyId)
    }

    /**
     * Affiche le dialogue de scan de QR Code
     */
    fun showQrScanDialog() {
        _uiState.update { it.copy(isQrScanDialogVisible = true) }
    }

    /**
     * Masque le dialogue de scan de QR Code
     */
    fun hideQrScanDialog() {
        _uiState.update { it.copy(isQrScanDialogVisible = false) }
    }

    /**
     * Traite les données d'un QR code scanné et crée un événement local
     */
    fun processScannedQrCode(eventData: String) {
        try {
            val parts = eventData.split("|")
            if (parts.size < 7) {
                _uiState.update {
                    it.copy(
                        error = "Format de QR code invalide",
                        isQrScanDialogVisible = false
                    )
                }
                return
            }

            val id = parts[0]
            val title = parts[1]
            val dateStr = parts[2]
            val location = parts[3]
            val colorStr = parts[4]
            val description = parts[5]
            val participantsStr = parts[6]
            
            // Récupération des todos et tobuys (nouveaux champs)
            val todosStr = if (parts.size > 7) parts[7] else ""
            val toBuysStr = if (parts.size > 8) parts[8] else ""

            // Conversion des données
            val date = try {
                LocalDateTime.parse(dateStr)
            } catch (e: Exception) {
                LocalDateTime.now()
            }

            val color = try {
                colorStr.toLong()
            } catch (e: Exception) {
                0xFF2196F3 // Couleur bleue par défaut
            }

            // Liste des participants (sans ajouter automatiquement le scan)
            val participants = participantsStr.split(",").filter { it.isNotEmpty() }.toMutableList()
            
            // Parsing des todos
            val todos = mutableListOf<com.martodev.atoute.home.domain.model.Todo>()
            if (todosStr.isNotEmpty()) {
                val todoItems = todosStr.split(",")
                for (todoItem in todoItems) {
                    try {
                        val todoData = todoItem.split("::")
                        if (todoData.size >= 5) {
                            val todoId = UUID.randomUUID().toString() // Générer un nouvel ID
                            val todoTitle = todoData[1]
                            val isCompleted = todoData[2].toBoolean()
                            val assignedTo = todoData[3].takeIf { it.isNotEmpty() }
                            val isPriority = todoData[4].toBoolean()
                            
                            todos.add(
                                com.martodev.atoute.home.domain.model.Todo(
                                    id = todoId,
                                    title = todoTitle,
                                    isCompleted = isCompleted,
                                    assignedTo = assignedTo,
                                    partyId = "", // sera rempli après la création de la party
                                    isPriority = isPriority
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("HomeViewModel", "Erreur lors du parsing d'un todo: ${e.message}")
                    }
                }
            }
            
            // Parsing des toBuys
            val toBuys = mutableListOf<com.martodev.atoute.home.domain.model.ToBuy>()
            if (toBuysStr.isNotEmpty()) {
                val toBuyItems = toBuysStr.split(",")
                for (toBuyItem in toBuyItems) {
                    try {
                        val toBuyData = toBuyItem.split("::")
                        if (toBuyData.size >= 7) {
                            val toBuyId = UUID.randomUUID().toString() // Générer un nouvel ID
                            val toBuyTitle = toBuyData[1]
                            val quantity = toBuyData[2].toIntOrNull() ?: 1
                            val estimatedPrice = toBuyData[3].toFloatOrNull()
                            val isPurchased = toBuyData[4].toBoolean()
                            val assignedTo = toBuyData[5].takeIf { it.isNotEmpty() }
                            val isPriority = toBuyData[6].toBoolean()
                            
                            toBuys.add(
                                com.martodev.atoute.home.domain.model.ToBuy(
                                    id = toBuyId,
                                    title = toBuyTitle,
                                    quantity = quantity,
                                    estimatedPrice = estimatedPrice,
                                    isPurchased = isPurchased,
                                    assignedTo = assignedTo,
                                    partyId = "", // sera rempli après la création de la party
                                    isPriority = isPriority
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("HomeViewModel", "Erreur lors du parsing d'un toBuy: ${e.message}")
                    }
                }
            }

            // Créer l'événement localement 
            viewModelScope.launch {
                try {
                    // Créer un nouvel événement avec un nouvel identifiant unique
                    val newPartyId = UUID.randomUUID().toString()
                    val newParty = DomainParty(
                        id = newPartyId,
                        title = title,
                        date = date,
                        location = location,
                        description = description,
                        participants = participants,
                        color = color
                    )

                    // Sauvegarder dans le repository
                    val partyId = savePartyUseCase(newParty)

                    // Enregistrer l'utilisateur courant comme propriétaire
                    authService.registerCurrentUserAsOwner(partyId)
                    
                    // Sauvegarder les todos et les toBuys
                    todos.forEach { todo ->
                        try {
                            // Mettre à jour l'ID de la party
                            val todoWithPartyId = todo.copy(partyId = partyId)
                            saveTodoUseCase(todoWithPartyId)
                        } catch (e: Exception) {
                            Log.e("HomeViewModel", "Erreur lors de la sauvegarde d'un todo: ${e.message}")
                        }
                    }
                    
                    toBuys.forEach { toBuy ->
                        try {
                            // Mettre à jour l'ID de la party
                            val toBuyWithPartyId = toBuy.copy(partyId = partyId)
                            saveToBuyUseCase(toBuyWithPartyId)
                        } catch (e: Exception) {
                            Log.e("HomeViewModel", "Erreur lors de la sauvegarde d'un toBuy: ${e.message}")
                        }
                    }

                    // Mettre à jour la liste des événements
                    loadParties()

                    // Fermer le dialogue
                    _uiState.update { 
                        it.copy(
                            isQrScanDialogVisible = false,
                            successMessage = "Événement importé avec ${todos.size} tâches et ${toBuys.size} achats"
                        ) 
                    }
                } catch (e: SavePartyUseCase.PartyLimitReachedException) {
                    // Afficher l'erreur de limite d'événements atteinte
                    _uiState.update { 
                        it.copy(
                            error = e.message,
                            isQrScanDialogVisible = false
                        ) 
                    }
                } catch (e: Exception) {
                    // Gérer les autres erreurs
                    _uiState.update { 
                        it.copy(
                            error = "Erreur lors de la création de l'événement: ${e.message}",
                            isQrScanDialogVisible = false
                        )
                    }
                }
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    error = "Erreur lors du traitement du QR code: ${e.message}",
                    isQrScanDialogVisible = false
                )
            }
        }
    }

    /**
     * Efface le message de succès
     */
    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}

/**
 * État de l'UI pour l'écran d'accueil
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val parties: List<Party> = arrayListOf(),
    val todos: List<Todo> = arrayListOf(),
    val error: String? = null,
    val isAddEventDialogVisible: Boolean = false,
    val isQrScanDialogVisible: Boolean = false,
    val successMessage: String? = null
) 