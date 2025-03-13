package com.martodev.atoute.party.domain.di

import com.martodev.atoute.authentication.domain.usecase.GetCurrentUserPremiumStatusUseCase
import com.martodev.atoute.party.domain.usecase.CheckPriorityTodoLimitUseCase
import com.martodev.atoute.party.domain.usecase.GetPartyDetailUseCase
import com.martodev.atoute.party.domain.usecase.GetToBuysByPartyUseCase
import com.martodev.atoute.party.domain.usecase.GetTodosByPartyUseCase
import com.martodev.atoute.party.domain.usecase.SaveToBuyUseCase
import com.martodev.atoute.party.domain.usecase.SaveTodoUseCase
import com.martodev.atoute.party.domain.usecase.UpdateToBuyPriorityUseCase
import com.martodev.atoute.party.domain.usecase.UpdateToBuyStatusUseCase
import com.martodev.atoute.party.domain.usecase.UpdateTodoPriorityUseCase
import com.martodev.atoute.party.domain.usecase.UpdateTodoStatusUseCase
import org.koin.dsl.module

/**
 * Module Koin pour la couche domain du feature Party
 */
val partyDomainModule = module {
    // Use cases
    factory { GetPartyDetailUseCase(get()) }
    factory { GetTodosByPartyUseCase(get()) }
    factory { GetToBuysByPartyUseCase(get()) }
    factory { SaveTodoUseCase(get(), get()) }
    factory { SaveToBuyUseCase(get()) }
    factory { UpdateTodoPriorityUseCase(get(), get()) }
    factory { UpdateTodoStatusUseCase(get()) }
    factory { UpdateToBuyStatusUseCase(get()) }
    factory { UpdateToBuyPriorityUseCase(get()) }
    factory { CheckPriorityTodoLimitUseCase(get(), get()) }
}