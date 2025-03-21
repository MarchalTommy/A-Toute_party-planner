package com.martodev.atoute.core.data.entity

import androidx.room.Embedded

/**
 * Classe pour représenter un Todo avec la couleur de son événement associé
 * Cette classe est utilisée pour les requêtes SQL qui joignent les tables todos et parties
 */
data class TodoWithPartyColor(
    @Embedded val todo: TodoEntity,
    val partyColor: Long?
) 