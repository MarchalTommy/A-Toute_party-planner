package com.martodev.atoute.home.presentation.di

import com.martodev.atoute.home.presentation.HomeViewModel
import com.martodev.atoute.home.presentation.PartyDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Module Koin pour la couche présentation du feature Home
 */
val homePresentationModule = module {
    
    // ViewModels
    viewModel { 
        HomeViewModel(
            getPartiesUseCase = get(),
            getPriorityTodosUseCase = get(),
            updateTodoStatusUseCase = get(),
            updateTodoPriorityUseCase = get()
        ) 
    }
    
    viewModel { 
        PartyDetailViewModel(
            getPartyDetailUseCase = get(),
            getTodosByPartyUseCase = get(),
            getToBuysByPartyUseCase = get(),
            updateTodoStatusUseCase = get(),
            updateTodoPriorityUseCase = get(),
            updateToBuyStatusUseCase = get(),
            updateToBuyPriorityUseCase = get()
        ) 
    }
} 