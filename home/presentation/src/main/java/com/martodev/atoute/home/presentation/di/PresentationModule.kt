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
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get()) }
    
    viewModel { PartyDetailViewModel(get(), get(), get(), get(), get(), get(), get()) }
} 