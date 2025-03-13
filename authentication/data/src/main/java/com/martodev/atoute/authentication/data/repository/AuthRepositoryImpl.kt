package com.martodev.atoute.authentication.data.repository

import com.martodev.atoute.authentication.data.datasource.UserDao
import com.martodev.atoute.authentication.data.datasource.UserPreferencesDataStore
import com.martodev.atoute.authentication.data.model.UserEntity
import com.martodev.atoute.authentication.domain.model.AuthResult
import com.martodev.atoute.authentication.domain.model.User
import com.martodev.atoute.authentication.domain.model.UserPreferences
import com.martodev.atoute.authentication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Implémentation du repository d'authentification
 *
 * @property userDao DAO pour accéder aux utilisateurs
 * @property userPreferencesDataStore DataStore pour les préférences utilisateur
 */
class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : AuthRepository {

    /**
     * Récupère l'utilisateur actuel s'il existe
     *
     * @return Flow contenant l'utilisateur actuel ou null
     */
    override fun getCurrentUser(): Flow<User?> {
        return userPreferencesDataStore.getCurrentUserId().combine(
            userPreferencesDataStore.getCurrentUserName()
        ) { userId, userName ->
            if (userId != null) {
                // Utilisateur enregistré, récupérer depuis la base de données
                userDao.getUserById(userId).first()?.toDomainModel()
            } else if (userName != null) {
                // Utilisateur anonyme, créer un objet User avec seulement le nom
                User(
                    id = "",
                    username = userName
                )
            } else {
                // Aucun utilisateur
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
            // Enregistrer le nom d'utilisateur dans les préférences
            userPreferencesDataStore.saveCurrentUserName(username)
            
            // Créer un utilisateur temporaire pour le retour
            val user = User(
                id = "",
                username = username
            )
            
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error("Erreur lors de la création de l'utilisateur anonyme: ${e.message}")
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
        return try {
            // Vérifier si l'email existe déjà
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                return AuthResult.Error("Un compte avec cet email existe déjà")
            }
            
            // Créer un nouvel utilisateur
            val userId = UUID.randomUUID().toString()
            val user = UserEntity(
                id = userId,
                username = username,
                email = email,
                isPremium = false,
                drinksAlcohol = true,
                isHalal = false,
                isVegetarian = false,
                isVegan = false,
                allergies = ""
            )
            
            // Insérer l'utilisateur dans la base de données
            userDao.insertUser(user)
            
            // Enregistrer l'ID de l'utilisateur dans les préférences
            userPreferencesDataStore.saveCurrentUserId(userId)
            userPreferencesDataStore.saveCurrentUserName(username)
            
            AuthResult.Success(user.toDomainModel())
        } catch (e: Exception) {
            AuthResult.Error("Erreur lors de la création du compte: ${e.message}")
        }
    }

    /**
     * Connecte un utilisateur
     *
     * @param email Email de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     * @return Résultat de l'opération
     */
    override suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            // Récupérer l'utilisateur par email
            val user = userDao.getUserByEmail(email)
                ?: return AuthResult.Error("Email ou mot de passe incorrect")
            
            // Dans une vraie application, on vérifierait le mot de passe ici
            // Pour simplifier, on considère que le mot de passe est correct
            
            // Enregistrer l'ID de l'utilisateur dans les préférences
            userPreferencesDataStore.saveCurrentUserId(user.id)
            userPreferencesDataStore.saveCurrentUserName(user.username)
            
            AuthResult.Success(user.toDomainModel())
        } catch (e: Exception) {
            AuthResult.Error("Erreur lors de la connexion: ${e.message}")
        }
    }

    /**
     * Déconnecte l'utilisateur actuel
     */
    override suspend fun signOut() {
        userPreferencesDataStore.clearCurrentUser()
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
                ?: return AuthResult.Error("Utilisateur non trouvé")
            
            // Mettre à jour les préférences
            val updatedUser = userEntity.copy(
                drinksAlcohol = preferences.drinksAlcohol,
                isHalal = preferences.isHalal,
                isVegetarian = preferences.isVegetarian,
                isVegan = preferences.isVegan,
                allergies = preferences.hasAllergies.joinToString(",")
            )
            
            // Mettre à jour l'utilisateur dans la base de données
            userDao.updateUser(updatedUser)
            
            AuthResult.Success(updatedUser.toDomainModel())
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
                ?: return AuthResult.Error("Utilisateur non trouvé")
            
            // Mettre à jour le statut premium
            val updatedUser = userEntity.copy(isPremium = isPremium)
            
            // Mettre à jour l'utilisateur dans la base de données
            userDao.updateUser(updatedUser)
            
            AuthResult.Success(updatedUser.toDomainModel())
        } catch (e: Exception) {
            AuthResult.Error("Erreur lors de la mise à jour du statut premium: ${e.message}")
        }
    }
} 