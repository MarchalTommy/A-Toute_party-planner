package com.martodev.atoute.home.data.repository

import com.martodev.atoute.core.data.dao.PartyDao
import com.martodev.atoute.core.data.dao.ToBuyDao
import com.martodev.atoute.core.data.entity.ToBuyEntity
import com.martodev.atoute.core.data.firebase.sync.FirestoreSyncManager
import com.martodev.atoute.home.domain.model.ToBuy
import com.martodev.atoute.home.domain.repository.ToBuyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ToBuyRepositoryImpl(
    private val toBuyDao: ToBuyDao,
    private val partyDao: PartyDao,
    private val syncManager: FirestoreSyncManager
) : ToBuyRepository {

    override fun getAllToBuys(): Flow<List<ToBuy>> {
        return toBuyDao.getAllToBuys().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getPriorityToBuys(): Flow<List<ToBuy>> {
        return toBuyDao.getPriorityToBuys().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getToBuysByParty(partyId: String): Flow<List<ToBuy>> {
        return toBuyDao.getToBuysByPartyId(partyId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getToBuyById(toBuyId: String): ToBuy? {
        return toBuyDao.getToBuyById(toBuyId)?.toDomainModel()
    }

    override suspend fun saveToBuy(toBuy: ToBuy) {
        toBuyDao.insertToBuy(toBuy.toEntity())
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }

    override suspend fun saveToBuys(toBuys: List<ToBuy>) {
        toBuys.forEach { saveToBuy(it) }
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }

    override suspend fun updateToBuyStatus(toBuyId: String, isPurchased: Boolean) {
        toBuyDao.updateToBuyStatus(toBuyId, isPurchased)
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }

    override suspend fun updateToBuyPriority(toBuyId: String, isPriority: Boolean) {
        toBuyDao.updateToBuyPriority(toBuyId, isPriority)
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }

    override suspend fun deleteToBuy(toBuyId: String) {
        toBuyDao.deleteToBuyById(toBuyId)
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }

    override suspend fun deleteToBuysByParty(partyId: String) {
        toBuyDao.deleteToBuysByPartyId(partyId)
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }
    
    // Mappers
    private fun ToBuyEntity.toDomainModel(): ToBuy {
        return ToBuy(
            id = id,
            title = title,
            quantity = quantity,
            estimatedPrice = estimatedPrice,
            isPurchased = isPurchased,
            assignedTo = assignedTo,
            partyId = partyId,
            partyColor = null, // On ne récupère pas la couleur pour simplifier
            isPriority = isPriority
        )
    }
    
    private fun ToBuy.toEntity(): ToBuyEntity {
        return ToBuyEntity(
            id = id,
            title = title,
            quantity = quantity,
            estimatedPrice = estimatedPrice,
            isPurchased = isPurchased,
            assignedTo = assignedTo,
            partyId = partyId,
            isPriority = isPriority
        )
    }
} 