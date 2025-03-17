package com.martodev.atoute.core.data.firebase.model

/**
 * Modèle représentant un article à acheter (ToBuy) dans Firestore
 * @property attributed_to Référence à l'utilisateur assigné (optionnel)
 * @property event Référence à l'événement auquel est lié l'article
 * @property name Nom de l'article à afficher
 * @property quantity Quantité à acheter (optionnel)
 */
data class FirestoreToBuy(
    val id: String = "",
    val attributed_to: String? = null, // Référence à un document user (optionnel)
    val event: String = "", // Référence à un document event
    val name: String = "",
    val quantity: Int? = null,
    val isPurchased: Boolean = false
) {
    companion object {
        const val COLLECTION_NAME = "to_buys"
        const val FIELD_ID = "id"
        const val FIELD_ATTRIBUTED_TO = "attributed_to"
        const val FIELD_EVENT = "event"
        const val FIELD_NAME = "name"
        const val FIELD_QUANTITY = "quantity"
        const val FIELD_IS_PURCHASED = "isPurchased"
    }
} 