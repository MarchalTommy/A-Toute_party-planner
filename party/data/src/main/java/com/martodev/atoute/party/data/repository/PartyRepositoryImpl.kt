package com.martodev.atoute.party.data.repository

import com.martodev.atoute.core.data.dao.ParticipantDao
import com.martodev.atoute.core.data.dao.PartyDao
import com.martodev.atoute.core.data.dao.TodoDao
import com.martodev.atoute.core.data.entity.PartyEntity
import com.martodev.atoute.core.data.firebase.sync.FirestoreSyncManager
import com.martodev.atoute.party.domain.model.Party
import com.martodev.atoute.party.domain.repository.PartyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Implémentation du repository pour gérer les données liées aux fêtes (Parties).
 * Cette implémentation utilise les DAOs du module core:data pour accéder à la base de données.
 */
class PartyRepositoryImpl(
    private val partyDao: PartyDao,
    private val todoDao: TodoDao,
    private val participantDao: ParticipantDao,
    private val syncManager: FirestoreSyncManager
) : PartyRepository {

    override fun getAllParties(): Flow<List<Party>> {
        return partyDao.getAllParties().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getAccessibleParties(userId: String): Flow<List<Party>> {
        return partyDao.getPartiesByParticipantId(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getPartyById(partyId: String): Flow<Party?> {
        return partyDao.getPartyWithDetailsById(partyId).map { wrapper ->
            wrapper?.party?.toDomain()
        }
    }

    override suspend fun getPartyByIdSync(partyId: String): Party? {
        return partyDao.getPartyById(partyId)?.toDomain()
    }

    override suspend fun saveParty(party: Party) {
        val partyId = party.id.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
        val entity = party.toEntity(partyId)
        partyDao.insertParty(entity)
        
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }

    override suspend fun saveParties(parties: List<Party>) {
        val entities = parties.map { party ->
            val partyId = party.id.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
            party.toEntity(partyId)
        }
        partyDao.insertParties(entities)
        
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }

    override suspend fun deleteParty(partyId: String) {
        // Supprimer d'abord les références associées
        todoDao.deleteTodosByPartyId(partyId)
        // Puis supprimer la party
        partyDao.deletePartyById(partyId)
        
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }
    
    // Fonctions de mapping
    private fun PartyEntity.toDomain(): Party {
        return Party(
            id = id,
            title = title,
            date = date,
            location = location,
            description = description,
            color = color,
            todoCount = todoCount,
            completedTodoCount = completedTodoCount
        )
    }
    
    private fun Party.toEntity(id: String = this.id): PartyEntity {
        return PartyEntity(
            id = id,
            title = title,
            date = date,
            location = location,
            description = description,
            color = color,
            todoCount = todoCount,
            completedTodoCount = completedTodoCount
        )
    }
}