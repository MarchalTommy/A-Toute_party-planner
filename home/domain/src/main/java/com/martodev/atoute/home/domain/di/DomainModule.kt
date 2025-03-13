package com.martodev.atoute.home.domain.di

import com.martodev.atoute.home.domain.service.AuthService
import com.martodev.atoute.home.domain.service.InMemoryAuthService
import com.martodev.atoute.home.domain.usecase.GetPartiesUseCase
import com.martodev.atoute.home.domain.usecase.GetPriorityTodosUseCase
import com.martodev.atoute.home.domain.usecase.SavePartyUseCase
import com.martodev.atoute.home.domain.usecase.UpdateTodoPriorityUseCase
import com.martodev.atoute.home.domain.usecase.UpdateTodoStatusUseCase
import org.koin.dsl.module

/**
 * Module Koin pour la couche domain du feature Home
 */
val homeDomainModule = module {

    // Services
    single<AuthService> { InMemoryAuthService() }

    // Use cases
    factory { GetPartiesUseCase(get()) }
    factory { GetPriorityTodosUseCase(get()) }
    factory { SavePartyUseCase(get()) }
    factory { UpdateTodoPriorityUseCase(get()) }
    factory { UpdateTodoStatusUseCase(get()) }
} 