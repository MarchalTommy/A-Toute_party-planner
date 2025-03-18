package com.martodev.atoute.home.data.di

import androidx.room.Room
import com.martodev.atoute.home.data.dao.ParticipantDao
import com.martodev.atoute.home.data.dao.PartyDao
import com.martodev.atoute.home.data.dao.ToBuyDao
import com.martodev.atoute.home.data.dao.TodoDao
import com.martodev.atoute.home.data.datasource.MockDataSource
import com.martodev.atoute.home.data.db.ATouteDatabase
import com.martodev.atoute.home.data.db.DatabaseInitializer
import com.martodev.atoute.home.data.repository.PartyRepositoryImpl
import com.martodev.atoute.home.data.repository.ToBuyRepositoryImpl
import com.martodev.atoute.home.data.repository.TodoRepositoryImpl
import com.martodev.atoute.home.domain.repository.PartyRepository
import com.martodev.atoute.home.domain.repository.ToBuyRepository
import com.martodev.atoute.home.domain.repository.TodoRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Module Koin pour la couche data du feature Home
 */
val homeDataModule = module {

    // Source de données mock
    single { MockDataSource() }

    // Base de données Room
    single {
        Room.databaseBuilder(
            androidContext(),
            ATouteDatabase::class.java,
            "atoute_db"
        ).build()
    }

    // DAOs
    single<PartyDao> { get<ATouteDatabase>().partyDao() }
    single<TodoDao> { get<ATouteDatabase>().todoDao() }
    single<ToBuyDao> { get<ATouteDatabase>().toBuyDao() }
    single<ParticipantDao> { get<ATouteDatabase>().participantDao() }

    // Initialisateur de base de données
    single {
        DatabaseInitializer(
            context = androidContext(),
            partyDao = get(),
            todoDao = get(),
            toBuyDao = get(),
            participantDao = get(),
            mockDataSource = get()
        )
    }

    // Repositories
    single<PartyRepository> { PartyRepositoryImpl(get(), get(), get(), get()) }
    single<TodoRepository> { TodoRepositoryImpl(get(), get(), get()) }
    single<ToBuyRepository> { ToBuyRepositoryImpl(get(), get(), get()) }
} 