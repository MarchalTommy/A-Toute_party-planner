package com.martodev.atoute.home.presentation.mapper

import com.martodev.atoute.home.domain.model.Party as DomainParty
import com.martodev.atoute.home.domain.model.Todo as DomainTodo
import com.martodev.atoute.home.domain.model.ToBuy as DomainToBuy
import com.martodev.atoute.home.presentation.model.Party as PresentationParty
import com.martodev.atoute.home.presentation.model.Todo as PresentationTodo
import com.martodev.atoute.home.presentation.model.ToBuy as PresentationToBuy

/**
 * Extension pour convertir un modèle Party du domaine vers la présentation
 */
fun DomainParty.toPresentation(): PresentationParty {
    return PresentationParty(
        id = this.id,
        title = this.title,
        date = this.date,
        location = this.location,
        description = this.description,
        participants = this.participants,
        todoCount = this.todoCount,
        completedTodoCount = this.completedTodoCount,
        color = this.color
    )
}

/**
 * Extension pour convertir une liste de modèles Party du domaine vers la présentation
 */
fun List<DomainParty>.toPartyPresentationList(): List<PresentationParty> {
    return map { it.toPresentation() }
}

/**
 * Extension pour convertir un modèle Todo du domaine vers la présentation
 */
fun DomainTodo.toPresentation(): PresentationTodo {
    return PresentationTodo(
        id = this.id,
        title = this.title,
        isCompleted = this.isCompleted,
        assignedTo = this.assignedTo,
        partyId = this.partyId,
        partyColor = this.partyColor,
        isPriority = this.isPriority
    )
}

/**
 * Extension pour convertir une liste de modèles Todo du domaine vers la présentation
 */
fun List<DomainTodo>.toTodoPresentationList(): List<PresentationTodo> {
    return map { it.toPresentation() }
}

/**
 * Extension pour convertir un modèle ToBuy du domaine vers la présentation
 */
fun DomainToBuy.toPresentation(): PresentationToBuy {
    return PresentationToBuy(
        id = this.id,
        title = this.title,
        quantity = this.quantity,
        estimatedPrice = this.estimatedPrice,
        isPurchased = this.isPurchased,
        assignedTo = this.assignedTo,
        partyId = this.partyId,
        partyColor = this.partyColor,
        isPriority = this.isPriority
    )
}

/**
 * Extension pour convertir une liste de modèles ToBuy du domaine vers la présentation
 */
fun List<DomainToBuy>.toBuyPresentationList(): List<PresentationToBuy> {
    return map { it.toPresentation() }
} 