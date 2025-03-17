package com.martodev.atoute.core.data.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.martodev.atoute.core.data.firebase.repository.FirestoreEventRepository
import com.martodev.atoute.core.data.firebase.repository.FirestoreEventRepositoryImpl
import com.martodev.atoute.core.data.firebase.repository.FirestoreToBuyRepository
import com.martodev.atoute.core.data.firebase.repository.FirestoreToBuyRepositoryImpl
import com.martodev.atoute.core.data.firebase.repository.FirestoreTodoRepository
import com.martodev.atoute.core.data.firebase.repository.FirestoreTodoRepositoryImpl
import com.martodev.atoute.core.data.firebase.repository.FirestoreUserRepository
import com.martodev.atoute.core.data.firebase.repository.FirestoreUserRepositoryImpl
import com.martodev.atoute.core.data.firebase.sync.FirestoreSyncManager
import com.martodev.atoute.core.data.firebase.sync.FirestoreSyncManagerImpl
import org.koin.dsl.module

/**
 * Module Koin pour l'injection des dépendances réseau
 */
val networkModule = module {
    // Firebase
    single { Firebase.firestore }
    single { Firebase.auth }
    
    // Repositories
    single<FirestoreUserRepository> { FirestoreUserRepositoryImpl(get(), get()) }
    single<FirestoreEventRepository> { FirestoreEventRepositoryImpl(get(), get(), get()) }
    single<FirestoreTodoRepository> { FirestoreTodoRepositoryImpl(get()) }
    single<FirestoreToBuyRepository> { FirestoreToBuyRepositoryImpl(get()) }
    
    // Sync Manager
    single<FirestoreSyncManager> { FirestoreSyncManagerImpl(get(), get(), get(), get(), get(), get(), get(), get()) }
} 