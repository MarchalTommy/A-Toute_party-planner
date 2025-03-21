package com.martodev.atoute.authentication.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.martodev.atoute.authentication.data.datasource.FirebaseAuthDataSource
import com.martodev.atoute.authentication.data.datasource.IUserPreferencesDataStore
import com.martodev.atoute.authentication.data.datasource.UserDao
import com.martodev.atoute.authentication.data.model.UserEntity
import com.martodev.atoute.authentication.domain.model.AuthResult
import com.martodev.atoute.authentication.domain.model.User
import com.martodev.atoute.authentication.domain.model.UserPreferences
import com.martodev.atoute.authentication.domain.repository.AuthRepository
import com.martodev.atoute.core.data.firebase.model.FirestoreUser
import com.martodev.atoute.core.data.firebase.repository.FirestoreUserRepository
import com.martodev.atoute.core.data.firebase.sync.FirestoreSyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Implémentation du repository d'authentification
 *
 * @property firebaseAuthDataSource Source de données Firebase Auth
 * @property userDao DAO pour accéder aux préférences utilisateur
 * @property userPreferencesDataStore DataStore pour les préférences utilisateur
 * @property auth Instance de FirebaseAuth
 * @property userRepository Repository pour les opérations sur Firestore
 * @property syncManager Manager pour synchroniser les données avec Firestore
 * @property context Context de l'application
 */
class AuthRepositoryImpl(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val userDao: UserDao,
    private val userPreferencesDataStore: IUserPreferencesDataStore,
    private val auth: FirebaseAuth,
    private val userRepository: FirestoreUserRepository,
    private val syncManager: FirestoreSyncManager,
    private val context: Context
) : AuthRepository {

    /**
     * Récupère l'utilisateur actuel s'il existe
     *
     * @return Flow contenant l'utilisateur actuel ou null
     */
    override fun getCurrentUser(): Flow<User?> {
        return firebaseAuthDataSource.getCurrentUser().map { firebaseUser ->
            if (firebaseUser != null) {
                // Récupérer les préférences de l'utilisateur depuis la base de données locale
                val userEntity = userDao.getUserById(firebaseUser.id).first()
                if (userEntity != null) {
                    // Combiner l'utilisateur Firebase avec les préférences locales
                    firebaseUser.copy(
                        preferences = UserPreferences(
                            drinksAlcohol = userEntity.drinksAlcohol,
                            isHalal = userEntity.isHalal,
                            isVegetarian = userEntity.isVegetarian,
                            isVegan = userEntity.isVegan,
                            hasAllergies = userEntity.allergies.split(",").filter { it.isNotBlank() }
                        ),
                        isPremium = userEntity.isPremium
                    )
                } else {
                    // Aucun utilisateur en local, créer une entrée par défaut
                    saveUserPreferences(firebaseUser.id)
                    firebaseUser
                }
            } else {
                null
            }
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
            val result = auth.signInAnonymously().await()
            val firebaseUser = result.user ?: throw Exception("Échec de la création de l'utilisateur anonyme")

            // Créer l'entité utilisateur locale
            val userEntity = UserEntity(
                id = firebaseUser.uid,
                username = username,
                email = null,
                isPremium = false,
                drinksAlcohol = true,
                isHalal = false,
                isVegetarian = false,
                isVegan = false,
                allergies = ""
            )
            userDao.insertUser(userEntity)

            // Créer l'utilisateur dans Firestore
            val firestoreUser = FirestoreUser(
                id = firebaseUser.uid,
                displayName = username,
                email = null,
                events = emptyMap()
            )
            userRepository.saveDocument(firestoreUser)
            
            // Synchroniser les modifications avec Firestore
            syncManager.pushLocalChanges()

            // Sauvegarder les préférences utilisateur
            userPreferencesDataStore.saveCurrentUserId(firebaseUser.uid)
            userPreferencesDataStore.saveCurrentUserName(username)

            AuthResult.Success(userEntity.toDomainModel())
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Une erreur est survenue")
        }
    }

    /**
     * Crée un compte utilisateur
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
        val result = firebaseAuthDataSource.createAccount(username, email, password)
        
        // Si l'authentification a réussi, sauvegarder les préférences par défaut
        if (result is AuthResult.Success) {
            saveUserPreferences(result.user.id)
            
            // Créer l'utilisateur dans Firestore s'il n'existe pas déjà
            val existingUser = userRepository.getDocumentByIdSync(result.user.id)
            if (existingUser == null) {
                val firestoreUser = FirestoreUser(
                    id = result.user.id,
                    displayName = username,
                    email = email,
                    events = emptyMap()
                )
                userRepository.saveDocument(firestoreUser)
                
                // Synchroniser les modifications avec Firestore
                syncManager.pushLocalChanges()
            }
            
            // Retourner l'utilisateur avec ses préférences par défaut
            // Ces préférences ont été sauvegardées en base locale par saveUserPreferences
            val defaultPrefs = UserPreferences(
                drinksAlcohol = true,
                isHalal = false,
                isVegetarian = false,
                isVegan = false,
                hasAllergies = emptyList()
            )
            
            return AuthResult.Success(
                result.user.copy(preferences = defaultPrefs)
            )
        }
        
        return result
    }

    /**
     * Connecte un utilisateur
     *
     * @param email Email de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     * @return Résultat de l'opération
     */
    override suspend fun signIn(email: String, password: String): AuthResult {
        val result = firebaseAuthDataSource.signIn(email, password)
        
        // Si l'authentification a réussi, vérifier si l'utilisateur existe en local
        if (result is AuthResult.Success) {
            // Vérifier si l'utilisateur existe déjà dans la base de données locale
            val userEntity = userDao.getUserById(result.user.id).first()
            if (userEntity == null) {
                // L'utilisateur n'existe pas en local, créer une entrée par défaut
                saveUserPreferences(result.user.id)
            } else {
                // Mettre à jour l'utilisateur avec les préférences existantes
                return AuthResult.Success(
                    result.user.copy(
                        preferences = UserPreferences(
                            drinksAlcohol = userEntity.drinksAlcohol,
                            isHalal = userEntity.isHalal,
                            isVegetarian = userEntity.isVegetarian,
                            isVegan = userEntity.isVegan,
                            hasAllergies = userEntity.allergies.split(",").filter { it.isNotBlank() }
                        ),
                        isPremium = userEntity.isPremium
                    )
                )
            }
        }
        
        return result
    }

    /**
     * Déconnecte l'utilisateur actuel
     */
    override suspend fun signOut() {
        // Vérifier si l'utilisateur est anonyme avant de le déconnecter
        val currentUser = auth.currentUser
        val isAnonymous = currentUser?.isAnonymous ?: false
        val userId = currentUser?.uid
        
        // Si l'utilisateur est anonyme, supprimer ses données de Firestore
        if (isAnonymous && userId != null) {
            try {
                // Supprimer l'utilisateur de Firestore
                userRepository.deleteDocument(userId)
                
                // Supprimer l'utilisateur de Firebase Auth
                currentUser.delete().await()
            } catch (e: Exception) {
                // En cas d'échec, continuer avec la déconnexion normale
                // Logger l'erreur si nécessaire
            }
        }
        
        // Déconnexion normale
        firebaseAuthDataSource.signOut()
        userPreferencesDataStore.clearCurrentUser()
        
        // Supprimer explicitement l'utilisateur de la base de données locale
        try {
            if (userId != null) {
                userDao.deleteUserById(userId)
            }
            
            // Pour assurer que toutes les données user sont nettoyées proprement
            cleanupUserData()
        } catch (e: Exception) {
            // Si l'erreur se produit pendant le nettoyage, nous pouvons continuer
            // mais nous devrions logger pour le débogage
            println("Erreur lors du nettoyage des données utilisateur: ${e.message}")
        }
    }

    /**
     * Nettoie les données utilisateur pour assurer qu'elles ne persistent pas
     * après une désinstallation ou une déconnexion
     */
    private suspend fun cleanupUserData() {
        // Nettoyer toutes les données utilisateur potentiellement persistantes
        context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
        
        // Si nous avons d'autres SharedPreferences, nous pourrions les nettoyer ici aussi
    }

    /**
     * Met à jour les préférences de l'utilisateur
     *
     * @param userId ID de l'utilisateur
     * @param preferences Nouvelles préférences
     * @return Résultat de l'opération
     */
    override suspend fun updateUserPreferences(
        userId: String,
        preferences: UserPreferences
    ): AuthResult {
        return try {
            // Récupérer l'utilisateur
            val userEntity = userDao.getUserById(userId).first()
            
            // Créer ou mettre à jour l'entité utilisateur
            val updatedUser = userEntity?.copy(
                drinksAlcohol = preferences.drinksAlcohol,
                isHalal = preferences.isHalal,
                isVegetarian = preferences.isVegetarian,
                isVegan = preferences.isVegan,
                allergies = preferences.hasAllergies.joinToString(",")
            )
                ?: UserEntity(
                    id = userId,
                    username = "", // Le nom sera récupéré depuis Firebase
                    email = null, // L'email sera récupéré depuis Firebase
                    isPremium = false,
                    drinksAlcohol = preferences.drinksAlcohol,
                    isHalal = preferences.isHalal,
                    isVegetarian = preferences.isVegetarian,
                    isVegan = preferences.isVegan,
                    allergies = preferences.hasAllergies.joinToString(",")
                )
            
            // Mettre à jour ou insérer l'utilisateur dans la base de données
            if (userEntity != null) {
                userDao.updateUser(updatedUser)
            } else {
                userDao.insertUser(updatedUser)
            }
            
            // Récupérer l'utilisateur Firebase actuel
            val currentUser = firebaseAuthDataSource.getCurrentUser().first()
                ?: return AuthResult.Error("Aucun utilisateur connecté")
            
            // Retourner le résultat avec l'utilisateur mis à jour
            AuthResult.Success(currentUser.copy(preferences = preferences))
        } catch (e: Exception) {
            AuthResult.Error("Erreur lors de la mise à jour des préférences: ${e.message}")
        }
    }

    /**
     * Met à jour le statut premium de l'utilisateur
     *
     * @param userId ID de l'utilisateur
     * @param isPremium Nouveau statut premium
     * @return Résultat de l'opération
     */
    override suspend fun updatePremiumStatus(userId: String, isPremium: Boolean): AuthResult {
        return try {
            // Récupérer l'utilisateur
            val userEntity = userDao.getUserById(userId).first()
            
            // Créer ou mettre à jour l'entité utilisateur
            val updatedUser = userEntity?.copy(isPremium = isPremium)
                ?: UserEntity(
                    id = userId,
                    username = "", // Le nom sera récupéré depuis Firebase
                    email = null, // L'email sera récupéré depuis Firebase
                    isPremium = isPremium,
                    drinksAlcohol = true,
                    isHalal = false,
                    isVegetarian = false,
                    isVegan = false,
                    allergies = ""
                )
            
            // Mettre à jour ou insérer l'utilisateur dans la base de données
            if (userEntity != null) {
                userDao.updateUser(updatedUser)
            } else {
                userDao.insertUser(updatedUser)
            }
            
            // Récupérer l'utilisateur Firebase actuel
            val currentUser = firebaseAuthDataSource.getCurrentUser().first()
                ?: return AuthResult.Error("Aucun utilisateur connecté")
            
            // Retourner le résultat avec l'utilisateur mis à jour
            AuthResult.Success(currentUser.copy(isPremium = isPremium))
        } catch (e: Exception) {
            AuthResult.Error("Erreur lors de la mise à jour du statut premium: ${e.message}")
        }
    }
    
    /**
     * Sauvegarde les préférences par défaut pour un nouvel utilisateur
     */
    private suspend fun saveUserPreferences(userId: String) {
        val userEntity = UserEntity(
            id = userId,
            username = "", // Le nom sera récupéré depuis Firebase
            email = null, // L'email sera récupéré depuis Firebase
            isPremium = false,
            drinksAlcohol = true,
            isHalal = false,
            isVegetarian = false,
            isVegan = false,
            allergies = ""
        )
        
        userDao.insertUser(userEntity)
    }
} 