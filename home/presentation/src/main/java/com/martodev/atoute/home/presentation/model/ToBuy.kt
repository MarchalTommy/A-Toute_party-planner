package com.martodev.atoute.home.presentation.model

import java.util.UUID

/**
 * Représentation d'un élément à acheter pour la préparation d'une Party.
 */
data class ToBuy(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val quantity: Int = 1,
    val estimatedPrice: Float? = null,
    val isPurchased: Boolean = false,
    val assignedTo: String? = null,
    val partyId: String,
    val partyColor: Long? = null, // Pour l'accent de couleur associé à la Party
    val isPriority: Boolean = false // Flag pour indiquer si l'achat est prioritaire
) 