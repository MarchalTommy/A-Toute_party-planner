package com.martodev.atoute.home.presentation.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Représentation d'une "Party" pour la couche présentation de l'écran d'accueil.
 */
data class Party(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val date: LocalDateTime,
    val location: String = "",
    val description: String = "",
    val participants: List<String> = mutableListOf(),
    val todoCount: Int = 0,
    val completedTodoCount: Int = 0,
    val color: Long? = null
) 