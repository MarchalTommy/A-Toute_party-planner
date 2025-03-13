package com.martodev.atoute.authentication.domain.repository

import com.martodev.atoute.authentication.domain.model.AuthResult
import com.martodev.atoute.authentication.domain.model.User
import com.martodev.atoute.authentication.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Interface définissant les opérations d'authentification
 */
interface AuthRepository {
    /**
     * Récupère l'utilisateur actuel s'il existe
     *
     * @return Flow contenant l'utilisateur actuel ou null
     */
    fun getCurrentUser(): Flow<User?>

    /**
     * Crée un utilisateur anonyme avec un pseudo
     *
     * @param username Pseudo de l'utilisateur
     * @return Résultat de l'opération
     */
    suspend fun createAnonymousUser(username: String): AuthResult

    /**
     * Crée un compte utilisateur
     *
     * @param username Pseudo de l'utilisateur
     * @param email Email de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     * @return Résultat de l'opération
     */
    suspend fun createAccount(username: String, email: String, password: String): AuthResult

    /**
     * Connecte un utilisateur
     *
     * @param email Email de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     * @return Résultat de l'opération
     */
    suspend fun signIn(email: String, password: String): AuthResult

    /**
     * Déconnecte l'utilisateur actuel
     */
    suspend fun signOut()

    /**
     * Met à jour les préférences de l'utilisateur
     *
     * @param userId ID de l'utilisateur
     * @param preferences Nouvelles préférences
     * @return Résultat de l'opération
     */
    suspend fun updateUserPreferences(userId: String, preferences: UserPreferences): AuthResult

    /**
     * Met à jour le statut premium de l'utilisateur
     *
     * @param userId ID de l'utilisateur
     * @param isPremium Nouveau statut premium
     * @return Résultat de l'opération
     */
    suspend fun updatePremiumStatus(userId: String, isPremium: Boolean): AuthResult
} 