package com.martodev.atoute.home.domain.model

/**
 * Modèle représentant un article à acheter ToBuy dans la couche domaine
 */
data class ToBuy(
    val id: String,
    val title: String,
    val quantity: Int = 1,
    val estimatedPrice: Float? = null,
    val isPurchased: Boolean = false,
    val assignedTo: String? = null,
    val partyId: String,
    val partyColor: Long? = null,
    val isPriority: Boolean = false
) 