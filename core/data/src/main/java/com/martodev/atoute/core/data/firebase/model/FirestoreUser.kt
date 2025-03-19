package com.martodev.atoute.core.data.firebase.model

/**
 * Modèle Firestore pour les utilisateurs
 */
data class FirestoreUser(
    val id: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val events: Map<String, String> = emptyMap()
) {
    companion object {
        const val ROLE_CREATOR = "creator"
        const val ROLE_PARTICIPANT = "participant"
        
        // Collection
        const val COLLECTION_NAME = "users"
        
        // Champs
        const val FIELD_DISPLAY_NAME = "displayName"
        const val FIELD_EMAIL = "email"
        const val FIELD_EVENTS = "events"
    }
} 