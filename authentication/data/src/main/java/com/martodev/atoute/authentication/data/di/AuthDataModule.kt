package com.martodev.atoute.authentication.data.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.martodev.atoute.authentication.data.datasource.AppDatabase
import com.martodev.atoute.authentication.data.datasource.FirebaseAuthDataSource
import com.martodev.atoute.authentication.data.datasource.FirebaseAuthDataSourceImpl
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
    // Firebase
    single { Firebase.auth }
    
    // Base de données
    single { AppDatabase.getInstance(androidContext()) }
    single { get<AppDatabase>().userDao() }
    
    // DataStore
    single<IUserPreferencesDataStore> { UserPreferencesDataStore(androidContext()) }
    
    // Sources de données
    single<FirebaseAuthDataSource> { FirebaseAuthDataSourceImpl(get(), get()) }
    
    // Repository
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
} 