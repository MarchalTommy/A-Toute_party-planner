package com.martodev.atoute.authentication.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.martodev.atoute.authentication.domain.model.AuthResult
import com.martodev.atoute.authentication.domain.model.User
import com.martodev.atoute.authentication.domain.model.UserPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Implémentation de la source de données Firebase Auth
 *
 * @property auth Instance de FirebaseAuth
 * @property userPreferencesDataStore DataStore pour les préférences utilisateur
 */
class FirebaseAuthDataSourceImpl(
    private val auth: FirebaseAuth,
    private val userPreferencesDataStore: IUserPreferencesDataStore
) : FirebaseAuthDataSource {

    /**
     * Récupère l'utilisateur actuellement connecté
     *
     * @return Flow contenant l'utilisateur actuel ou null
     */
    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                trySend(firebaseUser.toDomainModel())
            } else {
                trySend(null)
            }
        }

        auth.addAuthStateListener(authStateListener)
        
        // Émettre l'état initial
        val currentUser = auth.currentUser
        if (currentUser != null) {
            trySend(currentUser.toDomainModel())
        } else {
            trySend(null)
        }
        
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    /**
     * Crée un utilisateur anonyme avec un pseudo
     *
     * @param username Pseudo de l'utilisateur
     * @return Résultat de l'opération
     */
    override suspend fun createAnonymousUser(username: String): AuthResult {
        return try {
            // Créer un utilisateur anonyme dans Firebase
            val authResult = auth.signInAnonymously().await()
            val firebaseUser = authResult.user ?: throw Exception("Échec de la création de l'utilisateur anonyme")
            
            // Mettre à jour le profil avec le pseudo
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
            
            firebaseUser.updateProfile(profileUpdates).await()
            
            // Sauvegarder le pseudo dans les préférences locales
            userPreferencesDataStore.saveCurrentUserName(username)
            
            // Retourner le résultat
            AuthResult.Success(firebaseUser.toDomainModel())
        } catch (e: Exception) {
            AuthResult.Error("Erreur lors de la création de l'utilisateur anonyme: ${e.message}")
        }
    }

    /**
     * Crée un compte utilisateur avec email et mot de passe
     *
     * @param username Pseudo de l'utilisateur
     * @param email Email de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     * @return Résultat de l'opération
     */
    override suspend fun createAccount(
        username: String,
        email: String,
        password: String
    ): AuthResult {
        return try {
            // Créer un compte avec email et mot de passe
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Échec de la création du compte")
            
            // Mettre à jour le profil avec le pseudo
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
            
            firebaseUser.updateProfile(profileUpdates).await()
            
            // Sauvegarder le pseudo dans les préférences locales
            userPreferencesDataStore.saveCurrentUserName(username)
            
            // Retourner le résultat
            AuthResult.Success(firebaseUser.toDomainModel())
        } catch (e: Exception) {
            AuthResult.Error("Erreur lors de la création du compte: ${e.message}")
        }
    }

    /**
     * Connecte un utilisateur avec email et mot de passe
     *
     * @param email Email de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     * @return Résultat de l'opération
     */
    override suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            // Connecter l'utilisateur avec email et mot de passe
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Échec de la connexion")
            
            // Sauvegarder le pseudo dans les préférences locales
            val username = firebaseUser.displayName ?: email.substringBefore('@')
            userPreferencesDataStore.saveCurrentUserName(username)
            
            // Retourner le résultat
            AuthResult.Success(firebaseUser.toDomainModel())
        } catch (e: Exception) {
            AuthResult.Error("Erreur lors de la connexion: ${e.message}")
        }
    }

    /**
     * Déconnecte l'utilisateur actuel
     */
    override suspend fun signOut() {
        auth.signOut()
        userPreferencesDataStore.clearCurrentUser()
    }

    /**
     * Met à jour le profil de l'utilisateur
     *
     * @param userId ID de l'utilisateur
     * @param username Nouveau pseudo (optionnel)
     * @return Résultat de l'opération
     */
    override suspend fun updateUserProfile(
        userId: String,
        username: String?
    ): AuthResult {
        return try {
            val currentUser = auth.currentUser
                ?: return AuthResult.Error("Aucun utilisateur connecté")
            
            if (currentUser.uid != userId) {
                return AuthResult.Error("ID utilisateur invalide")
            }
            
            // Mettre à jour le profil
            val profileUpdatesBuilder = UserProfileChangeRequest.Builder()
            
            if (username != null) {
                profileUpdatesBuilder.setDisplayName(username)
                userPreferencesDataStore.saveCurrentUserName(username)
            }
            
            val profileUpdates = profileUpdatesBuilder.build()
            currentUser.updateProfile(profileUpdates).await()
            
            // Retourner le résultat
            AuthResult.Success(currentUser.toDomainModel())
        } catch (e: Exception) {
            AuthResult.Error("Erreur lors de la mise à jour du profil: ${e.message}")
        }
    }

    /**
     * Convertit un FirebaseUser en modèle de domaine User
     */
    private fun FirebaseUser.toDomainModel(): User {
        return User(
            id = uid,
            username = displayName ?: email?.substringBefore('@') ?: "Utilisateur",
            email = email,
            isPremium = false,
            preferences = UserPreferences() // Les préférences seront récupérées séparément
        )
    }
} 