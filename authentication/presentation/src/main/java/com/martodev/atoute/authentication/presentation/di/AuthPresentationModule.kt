package com.martodev.atoute.authentication.presentation.di

import com.martodev.atoute.authentication.presentation.viewmodel.AuthViewModel
import com.martodev.atoute.authentication.presentation.viewmodel.UserPreferencesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Module Koin pour l'injection des dépendances de la couche présentation d'authentification
 */
val authPresentationModule = module {
    // ViewModels
    viewModel { AuthViewModel(get(), get(), get(), get(), get()) }
    viewModel { UserPreferencesViewModel(get(), get(), get(), get()) }
} 