package com.martodev.atoute.party.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martodev.atoute.party.domain.model.Party
import com.martodev.atoute.party.domain.model.ToBuy
import com.martodev.atoute.party.domain.model.Todo
import com.martodev.atoute.party.domain.usecase.GetPartyDetailUseCase
import com.martodev.atoute.party.domain.usecase.GetToBuysByPartyUseCase
import com.martodev.atoute.party.domain.usecase.GetTodosByPartyUseCase
import com.martodev.atoute.party.domain.usecase.SaveToBuyUseCase
import com.martodev.atoute.party.domain.usecase.SaveTodoUseCase
import com.martodev.atoute.party.domain.usecase.UpdateToBuyPriorityUseCase
import com.martodev.atoute.party.domain.usecase.UpdateToBuyStatusUseCase
import com.martodev.atoute.party.domain.usecase.UpdateTodoPriorityUseCase
import com.martodev.atoute.party.domain.usecase.UpdateTodoStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

/**
 * ViewModel pour l'écran de détail d'une party
 */
class PartyDetailViewModel(
    private val partyId: String,
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

    init {
        loadPartyDetails()
    }

    /**
     * Charge les détails de la party
     */
    private fun loadPartyDetails() {
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
                }
            }
        }
    }

    /**
     * Mettre à jour le statut d'une tâche (complétée ou non)
     */
    fun updateTodoStatus(todoId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            updateTodoStatusUseCase(todoId, isCompleted)
            // La mise à jour du UI State est automatique via les flows
        }
    }

    /**
     * Mettre à jour le statut d'un achat (acheté ou non)
     */
    fun updateToBuyStatus(toBuyId: String, isPurchased: Boolean) {
        viewModelScope.launch {
            updateToBuyStatusUseCase(toBuyId, isPurchased)
            // La mise à jour du UI State est automatique via les flows
        }
    }

    /**
     * Mettre à jour la priorité d'une tâche
     */
    fun updateTodoPriority(todoId: String, isPriority: Boolean) {
        viewModelScope.launch {
            try {
                updateTodoPriorityUseCase(todoId, isPriority)
                // La mise à jour du UI State est automatique via les flows
                if (isPriority) {
                    _uiState.update { it.copy(snackbarMessage = "Tâche marquée comme prioritaire") }
                } else {
                    _uiState.update { it.copy(snackbarMessage = "Priorité retirée de la tâche") }
                }
            } catch (e: UpdateTodoPriorityUseCase.PriorityTodoLimitReachedException) {
                _uiState.update { it.copy(error = e.message) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Erreur lors de la mise à jour de la priorité: ${e.message}") }
            }
        }
    }

    /**
     * Mettre à jour la priorité d'un article à acheter
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
        return java.time.temporal.ChronoUnit.DAYS.between(now.toLocalDate(), date.toLocalDate())
    }

    /**
     * Ajoute une nouvelle tâche
     */
    fun addTodo(title: String, assignedTo: String? = null, isPriority: Boolean = false) {
        val currentParty = _uiState.value.party ?: return
        
        val newTodo = Todo(
            id = UUID.randomUUID().toString(),
            title = title,
            isCompleted = false,
            assignedTo = assignedTo,
            partyId = currentParty.id,
            partyColor = currentParty.color,
            isPriority = isPriority
        )
        
        viewModelScope.launch {
            try {
                saveTodoUseCase(newTodo)
                // La mise à jour du UI State est automatique via les flows
                _uiState.update { it.copy(
                    snackbarMessage = "Tâche ajoutée avec succès",
                    showAddTodoDialog = false
                ) }
            } catch (e: SaveTodoUseCase.PriorityTodoLimitReachedException) {
                _uiState.update { it.copy(
                    error = e.message,
                    showAddTodoDialog = false
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = "Erreur lors de l'ajout de la tâche: ${e.message}",
                    showAddTodoDialog = false
                ) }
            }
        }
    }

    /**
     * Ajoute un nouvel article à acheter
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
     * Affiche le dialogue de partage d'événement avec QR code
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
     * Génère les données de partage d'événement pour le QR code
     * @return Une chaîne formatée contenant toutes les informations nécessaires
     */
    fun generateEventShareData(): String? {
        val party = _uiState.value.party ?: return null
        val todos = _uiState.value.todos
        val toBuys = _uiState.value.toBuys
        
        // Format complet incluant toutes les informations nécessaires pour dupliquer l'événement
        // Format: ID|TITLE|DATE_ISO|LOCATION|COLOR|DESCRIPTION|PARTICIPANTS|TODOS|TOBUYS
        
        val participantsData = party.participants.joinToString(",")
        
        // Sérialisation des Todos
        // Format pour chaque todo: id::title::isCompleted::assignedTo::isPriority
        val todosData = todos.joinToString(",") { todo ->
            "${todo.id}::${todo.title}::${todo.isCompleted}::${todo.assignedTo ?: ""}::${todo.isPriority}"
        }
        
        // Sérialisation des ToBuys
        // Format pour chaque tobuy: id::title::quantity::estimatedPrice::isPurchased::assignedTo::isPriority
        val toBuysData = toBuys.joinToString(",") { toBuy ->
            "${toBuy.id}::${toBuy.title}::${toBuy.quantity}::${toBuy.estimatedPrice ?: ""}::${toBuy.isPurchased}::${toBuy.assignedTo ?: ""}::${toBuy.isPriority}"
        }
        
        return listOf(
            party.id,
            party.title,
            party.date.toString(),
            party.location,
            party.color?.toString() ?: "0xFF2196F3", // Bleu par défaut
            party.description,
            participantsData,
            todosData,
            toBuysData
        ).joinToString("|")
    }

    /**
     * Efface le message de la Snackbar
     */
    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}

/**
 * État UI pour l'écran de détail d'une party
 */
data class PartyDetailUiState(
    val isLoading: Boolean = false,
    val party: Party? = null,
    val todos: List<Todo> = emptyList(),
    val toBuys: List<ToBuy> = emptyList(),
    val error: String? = null,
    val isShareDialogVisible: Boolean = false,
    val snackbarMessage: String? = null,
    val showAddTodoDialog: Boolean = true
) 