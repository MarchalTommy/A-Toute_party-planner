package com.martodev.atoute.home.data.mapper

import com.martodev.atoute.home.data.entity.TodoEntity
import com.martodev.atoute.home.domain.model.Todo

/**
 * Extension pour convertir une TodoEntity en modèle Todo du domaine
 */
fun TodoEntity.toDomain(partyColor: Long? = null): Todo {
    return Todo(
        id = id,
        title = title,
        isCompleted = isCompleted,
        assignedTo = assignedTo,
        partyId = partyId,
        partyColor = partyColor,
        isPriority = isPriority
    )
}

/**
 * Extension pour convertir un modèle Todo du domaine en TodoEntity
 */
fun Todo.toEntity(): TodoEntity {
    return TodoEntity(
        id = id,
        title = title,
        isCompleted = isCompleted,
        assignedTo = assignedTo,
        partyId = partyId,
        isPriority = isPriority
    )
} 