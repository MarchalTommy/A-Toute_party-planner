package com.martodev.atoute.authentication.data.repository

import com.martodev.atoute.authentication.data.datasource.FirebaseAuthDataSource
import com.martodev.atoute.authentication.data.datasource.IUserPreferencesDataStore
import com.martodev.atoute.authentication.data.datasource.UserDao
import com.martodev.atoute.authentication.data.model.UserEntity
import com.martodev.atoute.authentication.domain.model.AuthResult
import com.martodev.atoute.authentication.domain.model.User
import com.martodev.atoute.authentication.domain.model.UserPreferences
import com.martodev.atoute.authentication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Implémentation du repository d'authentification
 *
 * @property firebaseAuthDataSource Source de données Firebase Auth
 * @property userDao DAO pour accéder aux préférences utilisateur
 * @property userPreferencesDataStore DataStore pour les préférences utilisateur
 */
class AuthRepositoryImpl(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val userDao: UserDao,
    private val userPreferencesDataStore: IUserPreferencesDataStore
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
        val result = firebaseAuthDataSource.createAnonymousUser(username)
        
        // Si l'authentification a réussi, sauvegarder les préférences par défaut
        if (result is AuthResult.Success) {
            saveUserPreferences(result.user.id)
            
            // Retourner l'utilisateur avec ses préférences par défaut
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
        firebaseAuthDataSource.signOut()
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
            val updatedUser = if (userEntity != null) {
                userEntity.copy(
                    drinksAlcohol = preferences.drinksAlcohol,
                    isHalal = preferences.isHalal,
                    isVegetarian = preferences.isVegetarian,
                    isVegan = preferences.isVegan,
                    allergies = preferences.hasAllergies.joinToString(",")
                )
            } else {
                UserEntity(
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
            }
            
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
            val updatedUser = if (userEntity != null) {
                userEntity.copy(isPremium = isPremium)
            } else {
                UserEntity(
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
            }
            
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