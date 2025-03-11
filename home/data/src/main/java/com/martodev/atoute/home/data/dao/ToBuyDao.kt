package com.martodev.atoute.home.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.martodev.atoute.home.data.entity.ToBuyEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour accéder aux entités ToBuy dans la base de données
 */
@Dao
interface ToBuyDao {
    
    @Query("SELECT * FROM to_buys")
    fun getAllToBuys(): Flow<List<ToBuyEntity>>
    
    @Query("SELECT * FROM to_buys WHERE isPriority = 1")
    fun getPriorityToBuys(): Flow<List<ToBuyEntity>>
    
    @Query("SELECT * FROM to_buys WHERE partyId = :partyId")
    fun getToBuysByPartyId(partyId: String): Flow<List<ToBuyEntity>>
    
    @Query("SELECT * FROM to_buys WHERE id = :toBuyId")
    suspend fun getToBuyById(toBuyId: String): ToBuyEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToBuy(toBuy: ToBuyEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToBuys(toBuys: List<ToBuyEntity>)
    
    @Update
    suspend fun updateToBuy(toBuy: ToBuyEntity)
    
    @Query("UPDATE to_buys SET isPriority = :isPriority WHERE id = :toBuyId")
    suspend fun updateToBuyPriority(toBuyId: String, isPriority: Boolean)
    
    @Query("UPDATE to_buys SET isPurchased = :isPurchased WHERE id = :toBuyId")
    suspend fun updateToBuyStatus(toBuyId: String, isPurchased: Boolean)
    
    @Delete
    suspend fun deleteToBuy(toBuy: ToBuyEntity)
    
    /**
     * Supprime un ToBuy par son ID
     */
    @Query("DELETE FROM to_buys WHERE id = :toBuyId")
    suspend fun deleteToBuyById(toBuyId: String)
    
    @Query("DELETE FROM to_buys WHERE partyId = :partyId")
    suspend fun deleteToBuysByPartyId(partyId: String)
} 