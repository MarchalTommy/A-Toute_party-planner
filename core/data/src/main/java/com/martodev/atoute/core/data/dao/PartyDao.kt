package com.martodev.atoute.core.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.martodev.atoute.core.data.entity.PartyEntity
import com.martodev.atoute.core.data.entity.PartyWithDetails
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour accéder aux entités Party dans la base de données
 */
@Dao
interface PartyDao {
    
    @Transaction
    @Query("SELECT * FROM parties ORDER BY date ASC")
    fun getAllPartiesWithDetails(): Flow<List<PartyWithDetails>>
    
    @Transaction
    @Query("SELECT * FROM parties WHERE id = :partyId")
    fun getPartyWithDetailsById(partyId: String): Flow<PartyWithDetails?>
    
    @Query("SELECT * FROM parties ORDER BY date ASC")
    fun getAllParties(): Flow<List<PartyEntity>>
    
    @Query("SELECT * FROM parties ORDER BY date ASC")
    suspend fun getAllPartiesSync(): List<PartyEntity>
    
    @Query("SELECT * FROM parties WHERE id = :partyId")
    suspend fun getPartyById(partyId: String): PartyEntity?
    
    /**
     * Récupère les événements où l'utilisateur est participant
     */
    @Query("SELECT p.* FROM parties p INNER JOIN participants part ON p.id = part.partyId WHERE part.userId = :userId ORDER BY p.date ASC")
    fun getPartiesByParticipantId(userId: String): Flow<List<PartyEntity>>
    
    /**
     * Récupère les événements où l'utilisateur est participant (version synchrone)
     */
    @Query("SELECT p.* FROM parties p INNER JOIN participants part ON p.id = part.partyId WHERE part.userId = :userId ORDER BY p.date ASC")
    suspend fun getPartiesByParticipantIdSync(userId: String): List<PartyEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParty(party: PartyEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParties(parties: List<PartyEntity>)
    
    @Update
    suspend fun updateParty(party: PartyEntity)
    
    /**
     * Met à jour les compteurs de tâches pour une fête
     */
    @Query("UPDATE parties SET todoCount = :todoCount, completedTodoCount = :completedTodoCount WHERE id = :partyId")
    suspend fun updateTodoCounts(partyId: String, todoCount: Int, completedTodoCount: Int)
    
    @Delete
    suspend fun deleteParty(party: PartyEntity)
    
    @Query("DELETE FROM parties WHERE id = :partyId")
    suspend fun deletePartyById(partyId: String)
} 