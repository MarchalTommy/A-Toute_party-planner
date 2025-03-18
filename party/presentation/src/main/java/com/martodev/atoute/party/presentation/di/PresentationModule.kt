package com.martodev.atoute.party.presentation.di

import com.martodev.atoute.party.presentation.viewmodel.PartyDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Module Koin pour la couche présentation du feature Party
 */
val partyPresentationModule = module {
    
    // ViewModels
    viewModel { parameters ->
        PartyDetailViewModel(
            partyId = parameters.get(),
            getPartyDetailUseCase = get(),
            getTodosByPartyUseCase = get(),
            getToBuysByPartyUseCase = get(),
            updateTodoStatusUseCase = get(),
            updateTodoPriorityUseCase = get(),
            updateToBuyStatusUseCase = get(),
            updateToBuyPriorityUseCase = get(),
            saveTodoUseCase = get(),
            saveToBuyUseCase = get()
        )
    }
} 