package com.martodev.atoute.party.domain.model

import java.time.LocalDateTime

/**
 * Modèle représentant une Party dans la couche domaine
 */
data class Party(
    val id: String,
    val title: String,
    val date: LocalDateTime,
    val location: String = "",
    val description: String = "",
    val participants: List<String> = ArrayList(),
    val todoCount: Int = 0,
    val completedTodoCount: Int = 0,
    val color: Long? = null
) 