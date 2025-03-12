package com.martodev.atoute.party.data.mapper

import com.martodev.atoute.home.data.entity.PartyEntity
import com.martodev.atoute.home.data.entity.TodoEntity
import com.martodev.atoute.home.data.entity.ToBuyEntity
import com.martodev.atoute.party.domain.model.Party
import com.martodev.atoute.party.domain.model.Todo
import com.martodev.atoute.party.domain.model.ToBuy

/**
 * Convertit une entité PartyEntity en modèle de domaine Party
 */
fun PartyEntity.toParty(): Party {
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

/**
 * Convertit un modèle de domaine Party en entité PartyEntity
 */
fun Party.toPartyEntity(): PartyEntity {
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
 * Convertit TodoEntity en modèle de domaine Todo
 */
fun TodoEntity.toTodo(): Todo {
    return Todo(
        id = id,
        title = title,
        isCompleted = isCompleted,
        assignedTo = assignedTo,
        partyId = partyId,
        partyColor = null, // On ne stocke pas la couleur dans l'entité
        isPriority = isPriority
    )
}

/**
 * Convertit ToBuyEntity en modèle de domaine ToBuy
 */
fun ToBuyEntity.toToBuy(): ToBuy {
    return ToBuy(
        id = id,
        title = title,
        quantity = quantity,
        estimatedPrice = estimatedPrice?.toFloat(),
        isPurchased = isPurchased,
        assignedTo = assignedTo,
        partyId = partyId,
        partyColor = null, // On ne stocke pas la couleur dans l'entité
        isPriority = isPriority
    )
} 