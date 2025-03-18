package com.martodev.atoute.home.data.mapper

import com.martodev.atoute.home.data.entity.ParticipantEntity
import com.martodev.atoute.home.data.entity.PartyEntity
import com.martodev.atoute.home.data.entity.PartyWithDetails
import com.martodev.atoute.home.domain.model.Party

/**
 * Extension pour convertir une PartyEntity en modèle Party du domaine
 */
fun PartyEntity.toDomain(
    participants: List<String> = emptyList(),
    todoCount: Int = 0,
    completedTodoCount: Int = 0
): Party {
    return Party(
        id = id,
        title = title,
        date = date,
        location = location,
        description = description,
        participants = participants,
        todoCount = todoCount,
        completedTodoCount = completedTodoCount,
        color = color
    )
}

/**
 * Extension pour convertir un PartyWithDetails en modèle Party du domaine
 */
fun PartyWithDetails.toDomain(): Party {
    return party.toDomain(
        participants = participants.map { it.name },
        todoCount = todos.size,
        completedTodoCount = todos.count { it.isCompleted }
    )
}

/**
 * Extension pour convertir un modèle Party du domaine en PartyEntity
 */
fun Party.toEntity(): PartyEntity {
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

/**
 * Extension pour convertir un modèle Party du domaine en liste de ParticipantEntity
 */
fun Party.toParticipantEntities(): List<ParticipantEntity> {
    return participants.map { name ->
        ParticipantEntity(
            name = name,
            partyId = id
        )
    }
} 