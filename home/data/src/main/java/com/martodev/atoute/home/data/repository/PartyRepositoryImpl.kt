package com.martodev.atoute.home.data.repository

import com.martodev.atoute.home.data.dao.ParticipantDao
import com.martodev.atoute.home.data.dao.PartyDao
import com.martodev.atoute.home.data.dao.TodoDao
import com.martodev.atoute.home.data.datasource.MockDataSource
import com.martodev.atoute.home.data.entity.ParticipantEntity
import com.martodev.atoute.home.data.mapper.toDomain
import com.martodev.atoute.home.data.mapper.toEntity
import com.martodev.atoute.home.domain.model.Party
import com.martodev.atoute.home.domain.repository.PartyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PartyRepositoryImpl(
    private val partyDao: PartyDao,
    private val participantDao: ParticipantDao,
    private val todoDao: TodoDao,
    private val mockDataSource: MockDataSource
) : PartyRepository {
    
    // Mode mock pour le développement
    private val useMockData = false
    
    override fun getAllParties(): Flow<List<Party>> {
        return if (useMockData) {
            // Utiliser les données mock
            flow { emit(mockDataSource.getParties()) }
        } else {
            // Utiliser la base de données
            partyDao.getAllPartiesWithDetails().map { partyWithDetailsList ->
                partyWithDetailsList.map { it.toDomain() }
            }
        }
    }
    
    override fun getPartyById(partyId: String): Flow<Party?> {
        return if (useMockData) {
            // Utiliser les données mock
            flow { 
                val party = mockDataSource.getParties().find { it.id == partyId }
                emit(party)
            }
        } else {
            // Utiliser la base de données
            partyDao.getPartyWithDetailsById(partyId).map { it?.toDomain() }
        }
    }
    
    override suspend fun getPartyByIdSync(partyId: String): Party? {
        if (useMockData) {
            // Utiliser les données mock
            return mockDataSource.getParties().find { it.id == partyId }
        }
        
        // Utiliser la base de données
        val partyEntity = partyDao.getPartyById(partyId) ?: return null
        // Nous devons manuellement charger les détails pour la version synchrone
        val participants = participantDao.getParticipantsByPartyIdSync(partyId)
        val todos = todoDao.getTodosByPartyIdSync(partyId)
        
        return Party(
            id = partyEntity.id,
            title = partyEntity.title,
            date = partyEntity.date,
            location = partyEntity.location,
            description = partyEntity.description,
            participants = participants.map { it.name },
            todoCount = todos.size,
            completedTodoCount = todos.count { it.isCompleted },
            color = partyEntity.color
        )
    }
    
    override suspend fun saveParty(party: Party) {
        if (!useMockData) {
            // S'assurer que la fête a une couleur (utiliser une couleur aléatoire si non spécifiée)
            val partyWithColor = if (party.color == null) {
                party.copy(color = mockDataSource.getRandomPartyColor())
            } else {
                party
            }
            
            // Sauvegarde de la Party
            partyDao.insertParty(partyWithColor.toEntity())
            
            // Sauvegarde des participants
            partyWithColor.participants.forEach { participantName ->
                participantDao.insertParticipant(
                    ParticipantEntity(
                        // Ne pas définir l'ID car il est auto-généré (autoGenerate = true)
                        name = participantName,
                        partyId = partyWithColor.id
                    )
                )
            }
        }
    }
    
    override suspend fun saveParties(parties: List<Party>) {
        if (!useMockData) {
            parties.forEach { saveParty(it) }
        }
    }
    
    override suspend fun deleteParty(partyId: String) {
        if (!useMockData) {
            // Supprimer les participants associés à la fête
            participantDao.deleteParticipantsByPartyId(partyId)
            
            // Utiliser la méthode deletePartyById qui prend directement l'ID
            partyDao.deletePartyById(partyId)
        }
    }
    
    override suspend fun updatePartyTodoCounters(partyId: String) {
        if (!useMockData) {
            // Les compteurs todoCount et completedTodoCount ne sont pas stockés directement 
            // dans PartyEntity mais sont calculés dans le mapper PartyMapper.kt
            // lors de la conversion de PartyWithDetails vers Party (modèle du domaine)
            
            // Nous n'avons donc pas besoin de mettre à jour ces compteurs dans l'entité
            // car ils seront automatiquement calculés lors de la récupération des données
            
            // Cette méthode est appelée après des modifications de todos, mais
            // aucune action n'est nécessaire car les compteurs sont calculés dynamiquement
        }
    }
} 