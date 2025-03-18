package com.martodev.atoute.home.domain.service

import com.martodev.atoute.home.domain.model.PartyOwner
import java.util.UUID

/**
 * Service d'authentification simple pour gérer l'utilisateur actuel et ses droits
 */
interface AuthService {
    /**
     * Obtient l'ID de l'utilisateur actuellement connecté
     * @return l'ID de l'utilisateur ou null si aucun utilisateur n'est connecté
     */
    fun getCurrentUserId(): String?
    
    /**
     * Vérifie si l'utilisateur courant est le propriétaire d'un événement
     * @param partyId l'ID de l'événement à vérifier
     * @return true si l'utilisateur courant est propriétaire de l'événement, false sinon
     */
    fun isCurrentUserOwnerOfParty(partyId: String): Boolean
    
    /**
     * Enregistre l'utilisateur courant comme propriétaire d'un événement
     * @param partyId l'ID de l'événement dont l'utilisateur devient propriétaire
     */
    fun registerCurrentUserAsOwner(partyId: String)
    
    /**
     * Obtient la liste des événements dont l'utilisateur courant est propriétaire
     * @return la liste des IDs d'événements possédés par l'utilisateur courant
     */
    fun getOwnedParties(): List<String>
}

/**
 * Implémentation simple du service d'authentification qui conserve les données en mémoire
 */
class InMemoryAuthService : AuthService {
    private val currentUserId = UUID.randomUUID().toString()
    private val ownedParties = mutableMapOf<String, MutableList<PartyOwner>>()
    
    override fun getCurrentUserId(): String {
        return currentUserId
    }
    
    override fun isCurrentUserOwnerOfParty(partyId: String): Boolean {
        return ownedParties[partyId]?.any { it.userId == currentUserId && it.isAdmin } == true
    }
    
    override fun registerCurrentUserAsOwner(partyId: String) {
        val owners = ownedParties.getOrPut(partyId) { mutableListOf() }
        owners.add(PartyOwner(currentUserId, partyId))
    }
    
    override fun getOwnedParties(): List<String> {
        return ownedParties.entries
            .filter { entry -> entry.value.any { it.userId == currentUserId } }
            .map { it.key }
    }
} 