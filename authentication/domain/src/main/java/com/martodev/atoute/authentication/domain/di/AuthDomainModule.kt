package com.martodev.atoute.authentication.domain.di

import com.martodev.atoute.authentication.domain.usecase.*
import org.koin.dsl.module

/**
 * Module Koin pour l'injection des dépendances du domaine d'authentification
 */
val authDomainModule = module {
    // Use cases
    factory { GetCurrentUserUseCase(get()) }
    factory { CreateAnonymousUserUseCase(get()) }
    factory { CreateAccountUseCase(get()) }
    factory { SignInUseCase(get()) }
    factory { SignOutUseCase(get()) }
    factory { UpdateUserPreferencesUseCase(get()) }
    factory { UpdatePremiumStatusUseCase(get()) }
    factory { GetCurrentUserPremiumStatusUseCase(get()) }
} 