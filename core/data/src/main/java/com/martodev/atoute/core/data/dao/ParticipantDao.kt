package com.martodev.atoute.core.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.martodev.atoute.core.data.entity.ParticipantEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour les participants
 */
@Dao
interface ParticipantDao {
    
    /**
     * Récupère tous les participants
     */
    @Query("SELECT * FROM participants")
    fun getAllParticipants(): Flow<List<ParticipantEntity>>
    
    /**
     * Récupère les participants d'une party spécifique
     */
    @Query("SELECT * FROM participants WHERE partyId = :partyId")
    fun getParticipantsByPartyId(partyId: String): Flow<List<ParticipantEntity>>
    
    /**
     * Récupère les participants d'une party spécifique (version synchrone)
     */
    @Query("SELECT * FROM participants WHERE partyId = :partyId")
    suspend fun getParticipantsByPartyIdSync(partyId: String): List<ParticipantEntity>
    
    /**
     * Récupère les participations d'un utilisateur spécifique
     */
    @Query("SELECT * FROM participants WHERE userId = :userId")
    fun getParticipationsByUserId(userId: String): Flow<List<ParticipantEntity>>
    
    /**
     * Récupère les participations d'un utilisateur spécifique (version synchrone)
     */
    @Query("SELECT * FROM participants WHERE userId = :userId")
    suspend fun getParticipationsByUserIdSync(userId: String): List<ParticipantEntity>
    
    /**
     * Vérifie si un utilisateur est participant à un événement
     */
    @Query("SELECT EXISTS(SELECT 1 FROM participants WHERE partyId = :partyId AND userId = :userId)")
    suspend fun isUserParticipant(partyId: String, userId: String): Boolean
    
    /**
     * Insère un participant
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: ParticipantEntity)
    
    /**
     * Insère des participants
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<ParticipantEntity>)
    
    /**
     * Supprime un participant
     */
    @Delete
    suspend fun deleteParticipant(participant: ParticipantEntity)
    
    /**
     * Supprime les participants d'une party spécifique
     */
    @Query("DELETE FROM participants WHERE partyId = :partyId")
    suspend fun deleteParticipantsByPartyId(partyId: String)
    
    /**
     * Supprime les participations d'un utilisateur spécifique
     */
    @Query("DELETE FROM participants WHERE userId = :userId")
    suspend fun deleteParticipationsByUserId(userId: String)
} 