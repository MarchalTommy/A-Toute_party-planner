package com.martodev.atoute.authentication.data.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.martodev.atoute.authentication.data.datasource.AppDatabase
import com.martodev.atoute.authentication.data.datasource.FirebaseAuthDataSource
import com.martodev.atoute.authentication.data.datasource.FirebaseAuthDataSourceImpl
import com.martodev.atoute.authentication.data.datasource.IUserPreferencesDataStore
import com.martodev.atoute.authentication.data.datasource.UserDao
import com.martodev.atoute.authentication.data.datasource.UserPreferencesDataStore
import com.martodev.atoute.authentication.data.repository.AuthRepositoryImpl
import com.martodev.atoute.authentication.domain.repository.AuthRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Module Koin pour la couche data du feature Authentication
 */
val authenticationDataModule = module {
    // Base de données Room
    single {
        AppDatabase.getInstance(androidContext())
    }

    // DAOs
    single<UserDao> { get<AppDatabase>().userDao() }

    // DataStore
    single<IUserPreferencesDataStore> { UserPreferencesDataStore(androidContext()) }

    // Firebase
    single { Firebase.auth }
    
    // Sources de données
    single<FirebaseAuthDataSource> { FirebaseAuthDataSourceImpl(get(), get()) }

    // Repositories
    single<AuthRepository> {
        AuthRepositoryImpl(
            firebaseAuthDataSource = get(),
            userDao = get(),
            userPreferencesDataStore = get(),
            auth = get(),
            userRepository = get(),
            syncManager = get(),
            context = androidContext()
        )
    }
} 