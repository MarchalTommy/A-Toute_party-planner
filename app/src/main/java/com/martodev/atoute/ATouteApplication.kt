package com.martodev.atoute

import android.app.Application
import com.martodev.atoute.authentication.data.di.authenticationDataModule
import com.martodev.atoute.authentication.domain.di.authDomainModule
import com.martodev.atoute.authentication.presentation.di.authPresentationModule
import com.martodev.atoute.core.data.db.DatabaseInitializer
import com.martodev.atoute.core.data.di.databaseModule
import com.martodev.atoute.core.data.di.networkModule
import com.martodev.atoute.home.data.di.homeDataModule
import com.martodev.atoute.home.domain.di.homeDomainModule
import com.martodev.atoute.home.presentation.di.homePresentationModule
import com.martodev.atoute.party.data.di.partyDataModule
import com.martodev.atoute.party.domain.di.partyDomainModule
import com.martodev.atoute.party.presentation.di.partyPresentationModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.java.KoinJavaComponent.inject

/**
 * Application principale qui initialise les dépendances Koin
 */
class ATouteApplication : Application() {

    // Scope pour les opérations de l'application
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Lazy injection pour éviter les dépendances circulaires
    private val databaseInitializer: DatabaseInitializer by inject(DatabaseInitializer::class.java)

    override fun onCreate() {
        super.onCreate()

        // Initialisation de Koin
        startKoin {
            // Logger pour le debug
            androidLogger(Level.ERROR)
            // Contexte Android
            androidContext(this@ATouteApplication)
            // Liste des modules
            modules(
                listOf(
                    // Module réseau pour Firestore
                    networkModule,
                    
                    // Module de base de données
                    databaseModule,
                    
                    // Modules du feature Home
                    homeDataModule,
                    homeDomainModule,
                    homePresentationModule,

                    // Modules du feature Party
                    partyDomainModule,
                    partyDataModule,
                    partyPresentationModule,
                    
                    // Modules du feature Authentication
                    authDomainModule,
                    authenticationDataModule,
                    authPresentationModule
                )
            )
        }

        // Initialisation de la base de données avec les données mockées
        initDatabase()
    }

    /**
     * Initialise la base de données avec les données mockées
     */
    private fun initDatabase() {
        databaseInitializer.initializeDatabaseIfNeeded(applicationScope)
    }
} 