package com.martodev.atoute.party.data.repository

import com.martodev.atoute.home.data.dao.PartyDao
import com.martodev.atoute.home.data.dao.TodoDao
import com.martodev.atoute.home.data.entity.PartyEntity
import com.martodev.atoute.party.domain.model.Party
import com.martodev.atoute.party.domain.repository.PartyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Implémentation du repository pour gérer les données liées aux fêtes (Parties).
 * Cette implémentation utilise les DAOs du module home:data pour accéder à la base de données.
 */
class PartyRepositoryImpl(
    private val partyDao: PartyDao,
    private val todoDao: TodoDao
) : PartyRepository {

    override fun getAllParties(): Flow<List<Party>> {
        return partyDao.getAllParties().map { entities ->
            entities.map { entity ->
                Party(
                    id = entity.id,
                    title = entity.title,
                    date = entity.date,
                    location = entity.location,
                    description = entity.description,
                    color = entity.color,
                    todoCount = entity.todoCount,
                    completedTodoCount = entity.completedTodoCount
                )
            }
        }
    }

    override fun getPartyById(partyId: String): Flow<Party?> {
        return partyDao.getPartyWithDetailsById(partyId).map { wrapper ->
            wrapper?.party?.let { entity ->
                Party(
                    id = entity.id,
                    title = entity.title,
                    date = entity.date,
                    location = entity.location,
                    description = entity.description,
                    color = entity.color,
                    todoCount = entity.todoCount,
                    completedTodoCount = entity.completedTodoCount
                )
            }
        }
    }

    override suspend fun getPartyByIdSync(partyId: String): Party? {
        return partyDao.getPartyById(partyId)?.let { entity ->
            Party(
                id = entity.id,
                title = entity.title,
                date = entity.date,
                location = entity.location,
                description = entity.description,
                color = entity.color,
                todoCount = entity.todoCount,
                completedTodoCount = entity.completedTodoCount
            )
        }
    }

    override suspend fun saveParty(party: Party) {
        val partyId = party.id.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
        val entity = PartyEntity(
            id = partyId,
            title = party.title,
            date = party.date,
            location = party.location,
            description = party.description,
            color = party.color,
            todoCount = party.todoCount,
            completedTodoCount = party.completedTodoCount
        )
        partyDao.insertParty(entity)
    }

    override suspend fun saveParties(parties: List<Party>) {
        val entities = parties.map { party ->
            val partyId = party.id.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
            PartyEntity(
                id = partyId,
                title = party.title,
                date = party.date,
                location = party.location,
                description = party.description,
                color = party.color,
                todoCount = party.todoCount,
                completedTodoCount = party.completedTodoCount
            )
        }
        partyDao.insertParties(entities)
    }

    override suspend fun deleteParty(partyId: String) {
        partyDao.deletePartyById(partyId)
    }

    override suspend fun updatePartyTodoCounters(partyId: String) {
        // Pour l'instant, nous ne faisons rien car nous ne pouvons pas utiliser count()
        // Nous pourrons implémenter cette méthode correctement plus tard
    }
} 