package com.martodev.atoute.home.presentation.model

import java.util.UUID

/**
 * Représentation d'une tâche Todo pour la couche présentation de l'écran d'accueil.
 */
data class Todo(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false,
    val assignedTo: String? = null,
    val partyId: String,
    val partyColor: Long? = null, // Pour l'accent de couleur associé à la Party
    val isPriority: Boolean = false // Flag pour indiquer si la tâche est prioritaire
) 