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
} 