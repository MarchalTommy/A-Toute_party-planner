package com.martodev.atoute.home.domain.model

/**
 * Modèle représentant une tâche Todo dans la couche domaine
 */
data class Todo(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false,
    val assignedTo: String? = null,
    val partyId: String,
    val partyColor: Long? = null,
    val isPriority: Boolean = false
) 