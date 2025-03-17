package com.martodev.atoute.authentication.data.datasource

import com.martodev.atoute.authentication.domain.model.AuthResult
import com.martodev.atoute.authentication.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Interface pour la source de données Firebase Auth
 */
interface FirebaseAuthDataSource {
    /**
     * Récupère l'utilisateur actuellement connecté
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
     * Crée un compte utilisateur avec email et mot de passe
     * 
     * @param username Pseudo de l'utilisateur
     * @param email Email de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     * @return Résultat de l'opération
     */
    suspend fun createAccount(username: String, email: String, password: String): AuthResult
    
    /**
     * Connecte un utilisateur avec email et mot de passe
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
     * Met à jour le profil de l'utilisateur
     * 
     * @param userId ID de l'utilisateur
     * @param username Nouveau pseudo (optionnel)
     * @return Résultat de l'opération
     */
    suspend fun updateUserProfile(userId: String, username: String? = null): AuthResult
} 