package com.martodev.atoute.authentication.data.di

import com.martodev.atoute.authentication.data.datasource.AppDatabase
import com.martodev.atoute.authentication.data.datasource.IUserPreferencesDataStore
import com.martodev.atoute.authentication.data.datasource.UserPreferencesDataStore
import com.martodev.atoute.authentication.data.repository.AuthRepositoryImpl
import com.martodev.atoute.authentication.domain.repository.AuthRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Module Koin pour l'injection des dépendances de la couche data d'authentification
 */
val authDataModule = module {
    // Base de données
    single { AppDatabase.getInstance(androidContext()) }
    single { get<AppDatabase>().userDao() }
    
    // DataStore
    single<IUserPreferencesDataStore> { UserPreferencesDataStore(androidContext()) }
    
    // Repository
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
} 