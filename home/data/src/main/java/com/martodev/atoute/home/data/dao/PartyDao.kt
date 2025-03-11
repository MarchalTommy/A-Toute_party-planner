package com.martodev.atoute.home.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.martodev.atoute.home.data.entity.PartyEntity
import com.martodev.atoute.home.data.entity.PartyWithDetails
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
    
    @Query("SELECT * FROM parties WHERE id = :partyId")
    suspend fun getPartyById(partyId: String): PartyEntity?
    
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