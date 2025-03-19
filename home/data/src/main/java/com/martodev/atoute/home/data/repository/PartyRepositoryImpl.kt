package com.martodev.atoute.home.data.repository

import com.martodev.atoute.core.data.dao.ParticipantDao
import com.martodev.atoute.core.data.dao.PartyDao
import com.martodev.atoute.core.data.dao.ToBuyDao
import com.martodev.atoute.core.data.dao.TodoDao
import com.martodev.atoute.core.data.entity.ParticipantEntity
import com.martodev.atoute.core.data.entity.PartyEntity
import com.martodev.atoute.core.data.entity.PartyWithDetails
import com.martodev.atoute.core.data.firebase.sync.FirestoreSyncManager
import com.martodev.atoute.home.domain.model.Party
import com.martodev.atoute.home.domain.repository.PartyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PartyRepositoryImpl(
    private val partyDao: PartyDao,
    private val todoDao: TodoDao,
    private val toBuyDao: ToBuyDao,
    private val participantDao: ParticipantDao,
    private val syncManager: FirestoreSyncManager
) : PartyRepository {

    override fun getAllParties(): Flow<List<Party>> {
        return partyDao.getAllPartiesWithDetails().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getAccessibleParties(userId: String): Flow<List<Party>> {
        return partyDao.getPartiesByParticipantId(userId).map { entities ->
            entities.map { PartyWithDetails(
                party = it,
                participants = participantDao.getParticipantsByPartyIdSync(it.id),
                todos = todoDao.getTodosByPartyIdSync(it.id),
                toBuys = toBuyDao.getToBuysByPartyIdSync(it.id)
            ).toDomainModel() }
        }
    }

    override fun getPartyById(partyId: String): Flow<Party?> {
        return partyDao.getPartyWithDetailsById(partyId).map { 
            it?.toDomainModel() 
        }
    }

    override suspend fun getPartyByIdSync(partyId: String): Party? {
        val party = partyDao.getPartyById(partyId) ?: return null
        
        // Récupérer les détails manuellement
        val participants = participantDao.getParticipantsByPartyIdSync(partyId)
        val todos = todoDao.getTodosByPartyIdSync(partyId)
        val toBuys = toBuyDao.getToBuysByPartyIdSync(partyId)
        
        return Party(
            id = party.id,
            title = party.title,
            date = party.date,
            location = party.location,
            description = party.description,
            participants = participants.map { it.name },
            todoCount = todos.size,
            completedTodoCount = todos.count { it.isCompleted },
            color = party.color
        )
    }

    override suspend fun saveParty(party: Party) {
        partyDao.insertParty(party.toEntity())
        
        // Ajouter les participants
        party.participants.forEach { participantName ->
            participantDao.insertParticipant(
                ParticipantEntity(
                    name = participantName,
                    partyId = party.id,
                    userId = "" // Par défaut, nous ne connaissons pas l'userId
                )
            )
        }
        
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }

    override suspend fun saveParties(parties: List<Party>) {
        parties.forEach { saveParty(it) }
        
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }

    override suspend fun deleteParty(partyId: String) {
        // Supprimer d'abord les références associées
        participantDao.deleteParticipantsByPartyId(partyId)
        todoDao.deleteTodosByPartyId(partyId)
        toBuyDao.deleteToBuysByPartyId(partyId)
        // Puis supprimer la party
        partyDao.deletePartyById(partyId)
        
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }

    override suspend fun updatePartyTodoCounters(partyId: String) {
        val todos = todoDao.getTodosByPartyIdSync(partyId)
        val todoCount = todos.size
        val completedTodoCount = todos.count { it.isCompleted }
        partyDao.updateTodoCounts(partyId, todoCount, completedTodoCount)
    }
    
    /**
     * Ajoute un utilisateur comme participant à une party
     */
    override suspend fun addParticipant(partyId: String, userId: String, name: String) {
        participantDao.insertParticipant(
            ParticipantEntity(
                name = name,
                partyId = partyId,
                userId = userId
            )
        )
        
        // Synchroniser avec Firestore
        syncManager.pushLocalChanges()
    }
    
    // Mappers
    private fun PartyWithDetails.toDomainModel(): Party {
        return Party(
            id = party.id,
            title = party.title,
            date = party.date,
            location = party.location,
            description = party.description,
            participants = participants.map { it.name },
            todoCount = todos.size,
            completedTodoCount = todos.count { it.isCompleted },
            color = party.color
        )
    }
    
    private fun Party.toEntity(): PartyEntity {
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