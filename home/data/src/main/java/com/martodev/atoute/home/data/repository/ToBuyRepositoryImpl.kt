package com.martodev.atoute.home.data.repository

import com.martodev.atoute.home.data.dao.PartyDao
import com.martodev.atoute.home.data.dao.ToBuyDao
import com.martodev.atoute.home.data.datasource.MockDataSource
import com.martodev.atoute.home.data.mapper.toEntity
import com.martodev.atoute.home.data.mapper.toDomain
import com.martodev.atoute.home.domain.model.ToBuy
import com.martodev.atoute.home.domain.repository.ToBuyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine

class ToBuyRepositoryImpl(
    private val toBuyDao: ToBuyDao,
    private val partyDao: PartyDao,
    private val mockDataSource: MockDataSource
) : ToBuyRepository {

    // Mode mock pour le développement
    private val useMockData = false
    
    override fun getAllToBuys(): Flow<List<ToBuy>> {
        return if (useMockData) {
            flow { emit(mockDataSource.getToBuys()) }
        } else {
            // Utiliser combine pour faire une jointure entre les ToBuys et les Parties
            toBuyDao.getAllToBuys().map { toBuyEntities ->
                toBuyEntities.map { toBuyEntity ->
                    // Dans un contexte map non-suspend, on ne peut pas utiliser la méthode suspend getPartyById
                    // On utilisera une couleur par défaut (null) pour le moment
                    toBuyEntity.toDomain(partyColor = null)
                }
            }
        }
    }
    
    override fun getPriorityToBuys(): Flow<List<ToBuy>> {
        return if (useMockData) {
            flow { emit(mockDataSource.getToBuys().filter { it.isPriority }) }
        } else {
            toBuyDao.getPriorityToBuys().map { toBuyEntities ->
                toBuyEntities.map { toBuyEntity ->
                    // Dans un contexte map non-suspend, on ne peut pas utiliser la méthode suspend getPartyById
                    toBuyEntity.toDomain(partyColor = null)
                }
            }
        }
    }
    
    override fun getToBuysByParty(partyId: String): Flow<List<ToBuy>> {
        return if (useMockData) {
            flow { emit(mockDataSource.getToBuys().filter { it.partyId == partyId }) }
        } else {
            // Utiliser combine avec getPartyWithDetailsById qui retourne un Flow
            // pour récupérer la couleur de la party de manière asynchrone
            partyDao.getPartyWithDetailsById(partyId).combine(toBuyDao.getToBuysByPartyId(partyId)) { partyWithDetails, toBuyEntities ->
                val partyColor = partyWithDetails?.party?.color
                toBuyEntities.map { toBuyEntity ->
                    toBuyEntity.toDomain(partyColor = partyColor)
                }
            }
        }
    }
    
    override suspend fun getToBuyById(toBuyId: String): ToBuy? {
        return if (useMockData) {
            mockDataSource.getToBuys().find { it.id == toBuyId }
        } else {
            val toBuyEntity = toBuyDao.getToBuyById(toBuyId) ?: return null
            val partyColor = partyDao.getPartyById(toBuyEntity.partyId)?.color
            toBuyEntity.toDomain(partyColor = partyColor)
        }
    }
    
    override suspend fun saveToBuy(toBuy: ToBuy) {
        if (!useMockData) {
            toBuyDao.insertToBuy(toBuy.toEntity())
        }
    }
    
    override suspend fun saveToBuys(toBuys: List<ToBuy>) {
        if (!useMockData) {
            toBuys.forEach { saveToBuy(it) }
        }
    }
    
    override suspend fun updateToBuyStatus(toBuyId: String, isPurchased: Boolean) {
        if (!useMockData) {
            toBuyDao.updateToBuyStatus(toBuyId, isPurchased)
        }
    }
    
    override suspend fun updateToBuyPriority(toBuyId: String, isPriority: Boolean) {
        if (!useMockData) {
            toBuyDao.updateToBuyPriority(toBuyId, isPriority)
        }
    }
    
    override suspend fun deleteToBuy(toBuyId: String) {
        if (!useMockData) {
            // Utiliser la nouvelle méthode deleteToBuyById qui accepte directement un String
            toBuyDao.deleteToBuyById(toBuyId)
        }
    }
    
    override suspend fun deleteToBuysByParty(partyId: String) {
        if (!useMockData) {
            toBuyDao.deleteToBuysByPartyId(partyId)
        }
    }
} 