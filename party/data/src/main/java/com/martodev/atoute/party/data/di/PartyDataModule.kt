package com.martodev.atoute.party.data.di

import com.martodev.atoute.home.data.dao.PartyDao
import com.martodev.atoute.home.data.dao.TodoDao
import com.martodev.atoute.home.domain.repository.ToBuyRepository as HomeToBuyRepository
import com.martodev.atoute.home.domain.repository.TodoRepository as HomeTodoRepository
import com.martodev.atoute.party.data.repository.PartyRepositoryImpl
import com.martodev.atoute.party.data.repository.ToBuyRepositoryAdapter
import com.martodev.atoute.party.data.repository.TodoRepositoryAdapter
import com.martodev.atoute.party.domain.repository.PartyRepository
import com.martodev.atoute.party.domain.repository.ToBuyRepository
import com.martodev.atoute.party.domain.repository.TodoRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.core.definition.Definition

/**
 * Module Koin pour la couche data du module Party
 */
val partyDataModule = module {
    // Repositories pour le module Party
    single<PartyRepository> { 
        PartyRepositoryImpl(
            partyDao = get(),
            todoDao = get()
        ) 
    }
    
    // Adaptateurs pour les repositories du module Home
    single<TodoRepository> { 
        TodoRepositoryAdapter(
            homeRepository = get()
        )
    }
    
    single<ToBuyRepository> { 
        ToBuyRepositoryAdapter(
            homeRepository = get()
        )
    }
} 