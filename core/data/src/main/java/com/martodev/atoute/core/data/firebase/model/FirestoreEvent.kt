package com.martodev.atoute.core.data.firebase.model

import com.google.firebase.Timestamp

/**
 * Modèle représentant un événement dans Firestore
 * @property color Couleur d'accent de l'événement (optionnel)
 * @property date Date de l'événement en timestamp
 * @property description Description de l'événement
 * @property event_name Nom de l'événement
 * @property location Localisation de l'événement
 * @property participants Map associant le rôle (creator/participant) à l'ID de l'utilisateur
 * @property tobuys Liste des références aux documents to_buys
 * @property todos Liste des références aux documents todos
 */
data class FirestoreEvent(
    val id: String = "",
    val color: Long? = null,
    val date: Timestamp? = null,
    val description: String = "",
    val event_name: String = "",
    val location: String = "",
    val participants: Map<String, String> = emptyMap(), // Map<Role, UserId>
    val tobuys: List<String> = emptyList(), // Liste des références aux documents to_buys
    val todos: List<String> = emptyList() // Liste des références aux documents todos
) {
    companion object {
        const val COLLECTION_NAME = "events"
        const val FIELD_ID = "id"
        const val FIELD_COLOR = "color"
        const val FIELD_DATE = "date"
        const val FIELD_DESCRIPTION = "description"
        const val FIELD_EVENT_NAME = "event_name"
        const val FIELD_LOCATION = "location"
        const val FIELD_PARTICIPANTS = "participants"
        const val FIELD_TOBUYS = "tobuys"
        const val FIELD_TODOS = "todos"
        
        const val ROLE_CREATOR = "creator"
        const val ROLE_PARTICIPANT = "participant"
    }
} 