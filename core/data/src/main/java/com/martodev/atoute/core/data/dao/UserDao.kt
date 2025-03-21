package com.martodev.atoute.core.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.martodev.atoute.core.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour accéder aux utilisateurs dans la base de données
 */
@Dao
interface UserDao {
    /**
     * Récupère un utilisateur par son ID
     *
     * @param id ID de l'utilisateur
     * @return Flow contenant l'utilisateur ou null
     */
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: String): Flow<UserEntity?>

    /**
     * Récupère un utilisateur par son email
     *
     * @param email Email de l'utilisateur
     * @return Utilisateur ou null
     */
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    /**
     * Insère un nouvel utilisateur
     *
     * @param user Utilisateur à insérer
     * @return ID de l'utilisateur inséré
     */
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    /**
     * Met à jour un utilisateur
     *
     * @param user Utilisateur à mettre à jour
     */
    @Update
    suspend fun updateUser(user: UserEntity)

    /**
     * Supprime un utilisateur
     *
     * @param user Utilisateur à supprimer
     */
    @Delete
    suspend fun deleteUser(user: UserEntity)

    /**
     * Supprime un utilisateur par son ID
     *
     * @param id ID de l'utilisateur à supprimer
     */
    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: String)

    /**
     * Supprime tous les utilisateurs de la base de données
     */
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}