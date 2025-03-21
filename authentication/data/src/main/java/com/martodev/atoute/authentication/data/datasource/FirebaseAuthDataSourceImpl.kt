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
            
            println("INFO: Création d'un utilisateur anonyme avec le nom: $username")
            
            // Sauvegarder d'abord le pseudo dans les préférences locales
            userPreferencesDataStore.saveCurrentUserName(username)
            println("INFO: Nom d'utilisateur sauvegardé dans les préférences locales: $username")
            
            // Mettre à jour le profil avec le pseudo
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
            
            // Mettre à jour le profil et attendre que ce soit fait
            firebaseUser.updateProfile(profileUpdates).await()
            println("INFO: Profil mis à jour avec le displayName: $username")
            
            // Recharger l'utilisateur pour s'assurer que les modifications ont été appliquées
            firebaseUser.reload().await()
            
            // Vérifier que le displayName a bien été mis à jour
            val updatedUser = auth.currentUser
            if (updatedUser?.displayName != username) {
                println("AVERTISSEMENT: Le displayName n'a pas été mis à jour correctement pour l'utilisateur anonyme.")
                println("DisplayName actuel: ${updatedUser?.displayName}, username souhaité: $username")
                
                // Faire une seconde tentative
                updatedUser?.updateProfile(profileUpdates)?.await()
                updatedUser?.reload()?.await()
                
                // Vérifier à nouveau
                if (auth.currentUser?.displayName != username) {
                    println("ÉCHEC: Deuxième tentative de mise à jour du displayName a échoué.")
                    println("Utilisation du nom stocké dans les préférences locales: $username")
                }
            } else {
                println("SUCCÈS: DisplayName mis à jour avec succès pour l'utilisateur anonyme: $username")
            }
            
            // Retourner le résultat avec les informations user mises à jour
            val user = auth.currentUser?.toDomainModel() ?: firebaseUser.toDomainModel()
            println("INFO: Nom d'utilisateur final dans le domaine: ${user.username}")
            AuthResult.Success(user)
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
            // Messages d'erreur plus détaillés selon le type d'erreur
            val errorMessage = when {
                e.message?.contains("no user record") == true -> 
                    "Aucun compte trouvé avec cet email. Veuillez vérifier votre adresse email ou créer un compte."
                e.message?.contains("password is invalid") == true -> 
                    "Mot de passe incorrect. Veuillez réessayer."
                e.message?.contains("blocked all requests") == true || e.message?.contains("network error") == true ->
                    "Problème de connexion réseau. Veuillez vérifier votre connexion internet et réessayer."
                e.message?.contains("too many unsuccessful login attempts") == true ->
                    "Trop de tentatives de connexion échouées. Veuillez réessayer plus tard."
                else -> "Erreur lors de la connexion: ${e.message}"
            }
            AuthResult.Error(errorMessage)
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
                profileUpdatesBuilder.displayName = username
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
        // Récupérer le nom d'utilisateur stocké localement (pour les utilisateurs anonymes)
        val storedUsername = userPreferencesDataStore.getCurrentUserNameSync()
        
        // Déterminer le meilleur nom d'utilisateur à utiliser
        val finalUsername: String = when {
            // Si c'est un utilisateur anonyme et que nous avons un nom stocké, utiliser ce nom
            isAnonymous && storedUsername.isNotEmpty() -> storedUsername

            // Si un displayName existe (défini via updateProfile), l'utiliser
            displayName != null -> displayName

            // Si l'utilisateur a un email, utiliser la partie avant @
            email != null -> email?.substringBefore('@')

            // En dernier recours, utiliser "Utilisateur anonyme"
            else -> "Utilisateur anonyme"
        }.toString()
        
        return User(
            id = uid,
            username = finalUsername,
            email = email,
            isPremium = false,
            preferences = UserPreferences() // Les préférences seront récupérées séparément
        )
    }
} 