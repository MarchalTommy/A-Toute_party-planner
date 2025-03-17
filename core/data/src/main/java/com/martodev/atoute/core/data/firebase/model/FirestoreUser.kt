package com.martodev.atoute.core.data.firebase.model

/**
 * Modèle représentant un utilisateur dans Firestore
 * @property events Map associant le rôle de l'utilisateur (creator/participant) à l'ID de l'événement
 */
data class FirestoreUser(
    val id: String = "",
    val displayName: String = "",
    val email: String = "",
    val events: Map<String, String> = emptyMap() // Map<Role, EventId>
) {
    companion object {
        const val COLLECTION_NAME = "users"
        const val FIELD_ID = "id"
        const val FIELD_DISPLAY_NAME = "displayName"
        const val FIELD_EMAIL = "email"
        const val FIELD_EVENTS = "events"
        
        const val ROLE_CREATOR = "creator"
        const val ROLE_PARTICIPANT = "participant"
    }
} 