package com.martodev.atoute.core.data.di

import com.martodev.atoute.core.data.dao.ParticipantDao
import com.martodev.atoute.core.data.dao.PartyDao
import com.martodev.atoute.core.data.dao.ToBuyDao
import com.martodev.atoute.core.data.dao.TodoDao
import com.martodev.atoute.core.data.dao.UserDao
import com.martodev.atoute.core.data.db.ATouteDatabase
import com.martodev.atoute.core.data.db.DatabaseInitializer
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Module Koin pour la base de données et les DAOs
 */
val databaseModule = module {
    // Base de données Room
    single { ATouteDatabase.getInstance(androidContext()) }
    
    // DAOs
    single<PartyDao> { get<ATouteDatabase>().partyDao() }
    single<TodoDao> { get<ATouteDatabase>().todoDao() }
    single<ToBuyDao> { get<ATouteDatabase>().toBuyDao() }
    single<ParticipantDao> { get<ATouteDatabase>().participantDao() }
    single<UserDao> { get<ATouteDatabase>().userDao() }
    
    // Initialisation de la base de données
    single { 
        DatabaseInitializer(
            context = androidContext(),
            partyDao = get(),
            todoDao = get(),
            toBuyDao = get(),
            participantDao = get()
        )
    }
} 