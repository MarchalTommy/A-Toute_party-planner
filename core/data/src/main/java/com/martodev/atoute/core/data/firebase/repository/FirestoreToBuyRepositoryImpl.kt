package com.martodev.atoute.core.data.firebase.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.martodev.atoute.core.data.firebase.model.FirestoreToBuy
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Implémentation de FirestoreToBuyRepository
 * @property firestore Instance de FirebaseFirestore
 */
class FirestoreToBuyRepositoryImpl(
    firestore: FirebaseFirestore
) : FirestoreRepositoryImpl<FirestoreToBuy>(firestore, FirestoreToBuy.COLLECTION_NAME), FirestoreToBuyRepository {

    override fun documentToModel(document: DocumentSnapshot): FirestoreToBuy? {
        if (!document.exists()) return null
        
        val id = document.id
        val attributedTo = document.getString(FirestoreToBuy.FIELD_ATTRIBUTED_TO)
        val event = document.getString(FirestoreToBuy.FIELD_EVENT) ?: ""
        val name = document.getString(FirestoreToBuy.FIELD_NAME) ?: ""
        val quantity = document.getLong(FirestoreToBuy.FIELD_QUANTITY)?.toInt()
        val isPurchased = document.getBoolean(FirestoreToBuy.FIELD_IS_PURCHASED) == true
        
        return FirestoreToBuy(
            id = id,
            attributed_to = attributedTo,
            event = event,
            name = name,
            quantity = quantity,
            isPurchased = isPurchased
        )
    }

    override fun modelToMap(model: FirestoreToBuy): Map<String, Any?> {
        return mapOf(
            FirestoreToBuy.FIELD_ATTRIBUTED_TO to model.attributed_to,
            FirestoreToBuy.FIELD_EVENT to model.event,
            FirestoreToBuy.FIELD_NAME to model.name,
            FirestoreToBuy.FIELD_QUANTITY to model.quantity,
            FirestoreToBuy.FIELD_IS_PURCHASED to model.isPurchased
        )
    }

    override fun getModelId(model: FirestoreToBuy): String {
        return model.id
    }

    override fun getToBuysByEventId(eventId: String): Flow<List<FirestoreToBuy>> = callbackFlow {
        val listenerRegistration = getCollectionReference()
            .whereEqualTo(FirestoreToBuy.FIELD_EVENT, eventId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val tobuys = snapshot?.documents?.mapNotNull { documentToModel(it) } ?: emptyList()
                trySend(tobuys)
            }
        
        awaitClose { listenerRegistration.remove() }
    }

    override fun getToBuysByUserId(userId: String): Flow<List<FirestoreToBuy>> = callbackFlow {
        val listenerRegistration = getCollectionReference()
            .whereEqualTo(FirestoreToBuy.FIELD_ATTRIBUTED_TO, userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val tobuys = snapshot?.documents?.mapNotNull { documentToModel(it) } ?: emptyList()
                trySend(tobuys)
            }
        
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun assignToBuyToUser(toBuyId: String, userId: String) {
        getCollectionReference().document(toBuyId)
            .update(FirestoreToBuy.FIELD_ATTRIBUTED_TO, userId)
            .await()
    }

    override suspend fun unassignToBuy(toBuyId: String) {
        getCollectionReference().document(toBuyId)
            .update(FirestoreToBuy.FIELD_ATTRIBUTED_TO, null)
            .await()
    }

    override suspend fun setToBuyPurchased(toBuyId: String, isPurchased: Boolean) {
        getCollectionReference().document(toBuyId)
            .update(FirestoreToBuy.FIELD_IS_PURCHASED, isPurchased)
            .await()
    }

    override suspend fun updateToBuyQuantity(toBuyId: String, quantity: Int) {
        getCollectionReference().document(toBuyId)
            .update(FirestoreToBuy.FIELD_QUANTITY, quantity)
            .await()
    }
} 