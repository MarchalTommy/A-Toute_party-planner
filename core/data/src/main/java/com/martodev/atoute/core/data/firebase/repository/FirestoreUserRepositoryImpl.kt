package com.martodev.atoute.core.data.firebase.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.martodev.atoute.core.data.firebase.model.FirestoreUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * Implémentation de FirestoreUserRepository
 * @property firestore Instance de FirebaseFirestore
 * @property auth Instance de FirebaseAuth
 */
class FirestoreUserRepositoryImpl(
    firestore: FirebaseFirestore = Firebase.firestore,
    private val auth: FirebaseAuth
) : FirestoreRepositoryImpl<FirestoreUser>(firestore, FirestoreUser.COLLECTION_NAME), FirestoreUserRepository {

    override fun documentToModel(document: DocumentSnapshot): FirestoreUser? {
        if (!document.exists()) return null
        
        val id = document.id
        val displayName = document.getString(FirestoreUser.FIELD_DISPLAY_NAME) ?: ""
        val email = document.getString(FirestoreUser.FIELD_EMAIL) ?: ""
        
        @Suppress("UNCHECKED_CAST")
        val events = document.get(FirestoreUser.FIELD_EVENTS) as? Map<String, String> ?: emptyMap()
        
        return FirestoreUser(
            id = id,
            displayName = displayName,
            email = email,
            events = events
        )
    }

    override fun modelToMap(model: FirestoreUser): Map<String, Any?> {
        return mapOf(
            FirestoreUser.FIELD_DISPLAY_NAME to model.displayName,
            FirestoreUser.FIELD_EMAIL to model.email,
            FirestoreUser.FIELD_EVENTS to model.events
        )
    }

    override fun getModelId(model: FirestoreUser): String {
        return model.id
    }

    override suspend fun addEventToUser(userId: String, eventId: String, isCreator: Boolean) {
        try {
            val userRef = firestore.collection(FirestoreUser.COLLECTION_NAME).document(userId)
            val role = if (isCreator) FirestoreUser.ROLE_CREATOR else FirestoreUser.ROLE_PARTICIPANT
            
            firestore.runTransaction { transaction ->
                val userDoc = transaction.get(userRef)
                if (userDoc.exists()) {
                    val events = userDoc.get(FirestoreUser.FIELD_EVENTS) as? Map<String, String> ?: emptyMap()
                    val updatedEvents = events + (eventId to role)
                    transaction.update(userRef, FirestoreUser.FIELD_EVENTS, updatedEvents)
                }
            }.await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun removeEventFromUser(userId: String, eventId: String) {
        try {
            val userRef = firestore.collection(FirestoreUser.COLLECTION_NAME).document(userId)
            
            firestore.runTransaction { transaction ->
                val userDoc = transaction.get(userRef)
                if (userDoc.exists()) {
                    val events = userDoc.get(FirestoreUser.FIELD_EVENTS) as? Map<String, String> ?: emptyMap()
                    val updatedEvents = events - eventId
                    transaction.update(userRef, FirestoreUser.FIELD_EVENTS, updatedEvents)
                }
            }.await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getDocumentByIdSync(id: String): FirestoreUser? {
        return try {
            firestore.collection(FirestoreUser.COLLECTION_NAME)
                .document(id)
                .get()
                .await()
                .toObject(FirestoreUser::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveDocument(document: FirestoreUser): String {
        return try {
            firestore.collection(FirestoreUser.COLLECTION_NAME)
                .document(document.id)
                .set(document)
                .await()
            document.id
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteDocument(id: String) {
        try {
            // Supprimer le document utilisateur
            firestore.collection(FirestoreUser.COLLECTION_NAME)
                .document(id)
                .delete()
                .await()
        } catch (e: Exception) {
            // Gérer l'erreur si nécessaire
            throw e
        }
    }

    override fun getDocumentById(id: String): Flow<FirestoreUser?> = flow {
        try {
            val document = firestore.collection(FirestoreUser.COLLECTION_NAME)
                .document(id)
                .get()
                .await()
            emit(document.toObject(FirestoreUser::class.java))
        } catch (e: Exception) {
            emit(null)
        }
    }
} 