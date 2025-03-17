package com.martodev.atoute.core.data.firebase.model

/**
 * Modèle représentant une tâche (Todo) dans Firestore
 * @property attributed_to Référence à l'utilisateur assigné (optionnel)
 * @property event Référence à l'événement auquel est lié le todo
 * @property name Nom du todo à afficher
 */
data class FirestoreTodo(
    val id: String = "",
    val attributed_to: String? = null, // Référence à un document user (optionnel)
    val event: String = "", // Référence à un document event
    val name: String = "",
    val isCompleted: Boolean = false
) {
    companion object {
        const val COLLECTION_NAME = "todos"
        const val FIELD_ID = "id"
        const val FIELD_ATTRIBUTED_TO = "attributed_to"
        const val FIELD_EVENT = "event"
        const val FIELD_NAME = "name"
        const val FIELD_IS_COMPLETED = "isCompleted"
    }
} 