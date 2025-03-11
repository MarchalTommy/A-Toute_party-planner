package com.martodev.atoute.home.domain.model

/**
 * Représente le propriétaire d'un événement
 * @param userId Identifiant unique de l'utilisateur propriétaire
 * @param partyId Identifiant de l'événement dont l'utilisateur est propriétaire
 * @param isAdmin Indique si l'utilisateur a des droits d'administration sur l'événement
 */
data class PartyOwner(
    val userId: String,
    val partyId: String,
    val isAdmin: Boolean = true
) 