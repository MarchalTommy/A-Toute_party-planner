package com.martodev.atoute.home.data.di

import com.martodev.atoute.core.data.dao.ParticipantDao
import com.martodev.atoute.core.data.dao.PartyDao
import com.martodev.atoute.core.data.dao.ToBuyDao
import com.martodev.atoute.core.data.dao.TodoDao
import com.martodev.atoute.core.data.firebase.sync.FirestoreSyncManager
import com.martodev.atoute.home.data.repository.PartyRepositoryImpl
import com.martodev.atoute.home.data.repository.ToBuyRepositoryImpl
import com.martodev.atoute.home.data.repository.TodoRepositoryImpl
import com.martodev.atoute.home.domain.repository.PartyRepository
import com.martodev.atoute.home.domain.repository.ToBuyRepository
import com.martodev.atoute.home.domain.repository.TodoRepository
import org.koin.dsl.module

/**
 * Module Koin pour la couche data du feature Home
 */
val homeDataModule = module {
    // Les DAOs sont fournis par le module core:data

    // Repositories
    single<PartyRepository> { 
        PartyRepositoryImpl(
            partyDao = get<PartyDao>(),
            todoDao = get<TodoDao>(),
            toBuyDao = get<ToBuyDao>(),
            participantDao = get<ParticipantDao>(),
            syncManager = get<FirestoreSyncManager>()
        ) 
    }
    
    single<TodoRepository> { 
        TodoRepositoryImpl(
            todoDao = get<TodoDao>(),
            partyDao = get<PartyDao>(),
            syncManager = get<FirestoreSyncManager>()
        ) 
    }
    
    single<ToBuyRepository> { 
        ToBuyRepositoryImpl(
            toBuyDao = get<ToBuyDao>(),
            partyDao = get<PartyDao>(),
            syncManager = get<FirestoreSyncManager>()
        ) 
    }
} 