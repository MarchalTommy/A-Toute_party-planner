package com.martodev.atoute.home.domain.di

import com.martodev.atoute.home.domain.service.AuthService
import com.martodev.atoute.home.domain.service.InMemoryAuthService
import com.martodev.atoute.home.domain.usecase.CheckPartyLimitUseCase
import com.martodev.atoute.home.domain.usecase.GetPartiesUseCase
import com.martodev.atoute.home.domain.usecase.GetPriorityTodosUseCase
import com.martodev.atoute.home.domain.usecase.SavePartyUseCase
import com.martodev.atoute.home.domain.usecase.SaveToBuyUseCase
import com.martodev.atoute.home.domain.usecase.SaveTodoUseCase
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
    factory { GetPartiesUseCase(get(), get()) }
    factory { GetPriorityTodosUseCase(get()) }
    factory { SavePartyUseCase(get(), get()) }
    factory { SaveTodoUseCase(get()) }
    factory { UpdateTodoStatusUseCase(get()) }
    factory { UpdateTodoPriorityUseCase(get()) }
    factory { SaveToBuyUseCase(get()) }
    factory { CheckPartyLimitUseCase(get(), get()) }
}