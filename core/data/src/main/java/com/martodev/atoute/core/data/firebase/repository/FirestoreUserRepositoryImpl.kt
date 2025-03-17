package com.martodev.atoute.core.data.firebase.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.martodev.atoute.core.data.firebase.model.FirestoreUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Implémentation de FirestoreUserRepository
 * @property firestore Instance de FirebaseFirestore
 * @property auth Instance de FirebaseAuth
 */
class FirestoreUserRepositoryImpl(
    firestore: FirebaseFirestore,
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

    override fun getCurrentUser(): Flow<FirestoreUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                getCollectionReference().document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        trySend(documentToModel(document))
                    }
                    .addOnFailureListener {
                        trySend(null)
                    }
            } else {
                trySend(null)
            }
        }
        
        auth.addAuthStateListener(authStateListener)
        
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    override suspend fun getCurrentUserSync(): FirestoreUser? {
        val firebaseUser = auth.currentUser ?: return null
        return getDocumentByIdSync(firebaseUser.uid)
    }

    override suspend fun addEventToUser(userId: String, eventId: String, isCreator: Boolean) {
        val role = if (isCreator) FirestoreUser.ROLE_CREATOR else FirestoreUser.ROLE_PARTICIPANT
        
        getCollectionReference().document(userId)
            .update(
                "${FirestoreUser.FIELD_EVENTS}.$role", eventId
            ).await()
    }

    override suspend fun removeEventFromUser(userId: String, eventId: String) {
        // Récupérer l'utilisateur pour trouver le rôle de l'événement
        val user = getDocumentByIdSync(userId) ?: return
        
        // Trouver le rôle de l'événement
        val roleToRemove = user.events.entries.find { it.value == eventId }?.key ?: return
        
        // Supprimer l'événement
        getCollectionReference().document(userId)
            .update(
                "${FirestoreUser.FIELD_EVENTS}.$roleToRemove", FieldValue.delete()
            ).await()
    }
} 