package com.martodev.atoute.home.domain.di

import com.martodev.atoute.home.domain.service.AuthService
import com.martodev.atoute.home.domain.service.InMemoryAuthService
import com.martodev.atoute.home.domain.usecase.GetPartiesUseCase
import com.martodev.atoute.home.domain.usecase.GetPartyDetailUseCase
import com.martodev.atoute.home.domain.usecase.GetPriorityTodosUseCase
import com.martodev.atoute.home.domain.usecase.GetToBuysByPartyUseCase
import com.martodev.atoute.home.domain.usecase.GetTodosByPartyUseCase
import com.martodev.atoute.home.domain.usecase.SavePartyUseCase
import com.martodev.atoute.home.domain.usecase.SaveToBuyUseCase
import com.martodev.atoute.home.domain.usecase.SaveTodoUseCase
import com.martodev.atoute.home.domain.usecase.UpdateToBuyPriorityUseCase
import com.martodev.atoute.home.domain.usecase.UpdateToBuyStatusUseCase
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
    factory { GetPartyDetailUseCase(get()) }
    factory { GetPriorityTodosUseCase(get()) }
    factory { GetTodosByPartyUseCase(get()) }
    factory { GetToBuysByPartyUseCase(get()) }
    factory { SavePartyUseCase(get()) }
    factory { SaveTodoUseCase(get()) }
    factory { SaveToBuyUseCase(get()) }
    factory { UpdateTodoPriorityUseCase(get()) }
    factory { UpdateTodoStatusUseCase(get()) }
    factory { UpdateToBuyStatusUseCase(get()) }
    factory { UpdateToBuyPriorityUseCase(get()) }
} 