package com.martodev.atoute.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martodev.atoute.home.domain.model.Party as DomainParty
import com.martodev.atoute.home.domain.model.Todo as DomainTodo
import com.martodev.atoute.home.domain.usecase.GetPartiesUseCase
import com.martodev.atoute.home.domain.usecase.GetPriorityTodosUseCase
import com.martodev.atoute.home.domain.usecase.UpdateTodoPriorityUseCase
import com.martodev.atoute.home.domain.usecase.UpdateTodoStatusUseCase
import com.martodev.atoute.home.domain.usecase.SavePartyUseCase
import com.martodev.atoute.home.domain.service.AuthService
import com.martodev.atoute.home.presentation.mapper.toPresentation
import com.martodev.atoute.home.presentation.model.Party
import com.martodev.atoute.home.presentation.model.Todo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.collections.emptyList

/**
 * ViewModel pour l'écran d'accueil qui gère les données à afficher
 */
class HomeViewModel(
    private val getPartiesUseCase: GetPartiesUseCase,
    private val getPriorityTodosUseCase: GetPriorityTodosUseCase,
    private val savePartyUseCase: SavePartyUseCase,
    private val updateTodoStatusUseCase: UpdateTodoStatusUseCase,
    private val updateTodoPriorityUseCase: UpdateTodoPriorityUseCase,
    private val authService: AuthService
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
     * Charge les données depuis le repository
     */
    private fun loadDataFromRepository() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Collecter les parties et les convertir en modèles de présentation
            launch {
                getPartiesUseCase().collectLatest { domainParties ->
                    val presentationParties = domainParties.map { it.toPresentation() }
                    _uiState.update { it.copy(
                        parties = presentationParties,
                        isLoading = false
                    ) }
                }
            }
            
            // Collecter les todos prioritaires et les convertir en modèles de présentation
            launch {
                getPriorityTodosUseCase().collectLatest { domainTodos ->
                    val presentationTodos = domainTodos.map { it.toPresentation() }
                    _uiState.update { it.copy(
                        todos = presentationTodos,
                        isLoading = false
                    ) }
                }
            }
        }
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
     * Met à jour la priorité d'un todo
     */
    fun updateTodoPriority(todoId: String, isPriority: Boolean) {
        viewModelScope.launch {
            updateTodoPriorityUseCase(todoId, isPriority)
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
            // Créer un nouveau Party avec les informations fournies (en utilisant le modèle de domaine)
            val newParty = DomainParty(
                id = UUID.randomUUID().toString(),
                title = title,
                date = date,
                location = location,
                color = color
            )
            
            // Sauvegarder dans le repository
            val partyId = savePartyUseCase(newParty)
            
            // Enregistrer l'utilisateur courant comme propriétaire
            authService.registerCurrentUserAsOwner(partyId)
            
            // Mettre à jour la liste des événements
            loadParties()
        }
    }
    
    /**
     * Vérifie si l'utilisateur courant est propriétaire d'un événement
     */
    fun isCurrentUserOwnerOfParty(partyId: String): Boolean {
        return authService.isCurrentUserOwnerOfParty(partyId)
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
    val isAddEventDialogVisible: Boolean = false
) 