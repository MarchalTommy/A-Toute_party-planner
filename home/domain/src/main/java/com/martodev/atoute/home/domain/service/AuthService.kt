package com.martodev.atoute.home.domain.service

/**
 * Interface pour gérer les services d'authentification
 */
interface AuthService {
    /**
     * Récupère l'ID de l'utilisateur courant
     * @return ID de l'utilisateur, ou null si non connecté
     */
    fun getCurrentUserId(): String?
    
    /**
     * Vérifie si l'utilisateur courant est propriétaire d'une party
     * @param partyId l'ID de la party à vérifier
     * @return true si l'utilisateur est propriétaire, false sinon
     */
    fun isCurrentUserOwnerOfParty(partyId: String): Boolean
    
    /**
     * Enregistre l'utilisateur courant comme propriétaire d'un événement
     * @param partyId l'ID de l'événement dont l'utilisateur devient propriétaire
     * @param userName nom de l'utilisateur (optionnel)
     */
    fun registerCurrentUserAsOwner(partyId: String, userName: String? = null)
    
    /**
     * Récupère la liste des événements dont l'utilisateur courant est propriétaire
     * @return liste des IDs de parties
     */
    fun getOwnedParties(): List<String>
}

/**
 * Implémentation en mémoire de AuthService pour les tests
 */
class InMemoryAuthService : AuthService {
    private val currentUserId = "user123"
    private val ownedParties = mutableMapOf<String, Boolean>()
    
    override fun getCurrentUserId(): String? {
        return currentUserId
    }
    
    override fun isCurrentUserOwnerOfParty(partyId: String): Boolean {
        return ownedParties[partyId] == true
    }
    
    override fun registerCurrentUserAsOwner(partyId: String, userName: String?) {
        ownedParties[partyId] = true
    }
    
    override fun getOwnedParties(): List<String> {
        return ownedParties.filter { it.value }.map { it.key }
    }
} 