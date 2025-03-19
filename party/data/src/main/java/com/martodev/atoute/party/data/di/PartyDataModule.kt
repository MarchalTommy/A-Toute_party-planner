package com.martodev.atoute.party.data.di

import com.martodev.atoute.core.data.firebase.sync.FirestoreSyncManager
import com.martodev.atoute.party.data.repository.PartyRepositoryImpl
import com.martodev.atoute.party.data.repository.ToBuyRepositoryAdapter
import com.martodev.atoute.party.data.repository.TodoRepositoryAdapter
import com.martodev.atoute.party.domain.repository.PartyRepository
import com.martodev.atoute.party.domain.repository.ToBuyRepository
import com.martodev.atoute.party.domain.repository.TodoRepository
import org.koin.dsl.module

/**
 * Module Koin pour la couche data du module Party
 */
val partyDataModule = module {
    // Repositories pour le module Party
    single<PartyRepository> { 
        PartyRepositoryImpl(
            partyDao = get(),
            todoDao = get(),
            participantDao = get(),
            syncManager = get<FirestoreSyncManager>()
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