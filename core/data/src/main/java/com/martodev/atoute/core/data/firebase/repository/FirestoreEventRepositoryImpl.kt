package com.martodev.atoute.core.data.firebase.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.martodev.atoute.core.data.firebase.model.FirestoreEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Implémentation de FirestoreEventRepository
 * @property firestore Instance de FirebaseFirestore
 * @property auth Instance de FirebaseAuth
 * @property userRepository Repository pour les utilisateurs
 */
class FirestoreEventRepositoryImpl(
    firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val userRepository: FirestoreUserRepository
) : FirestoreRepositoryImpl<FirestoreEvent>(firestore, FirestoreEvent.COLLECTION_NAME), FirestoreEventRepository {

    override fun documentToModel(document: DocumentSnapshot): FirestoreEvent? {
        if (!document.exists()) return null
        
        val id = document.id
        val color = document.getLong(FirestoreEvent.FIELD_COLOR)
        val date = document.getTimestamp(FirestoreEvent.FIELD_DATE)
        val description = document.getString(FirestoreEvent.FIELD_DESCRIPTION) ?: ""
        val eventName = document.getString(FirestoreEvent.FIELD_EVENT_NAME) ?: ""
        val location = document.getString(FirestoreEvent.FIELD_LOCATION) ?: ""
        
        @Suppress("UNCHECKED_CAST")
        val participants = document.get(FirestoreEvent.FIELD_PARTICIPANTS) as? Map<String, String> ?: emptyMap()
        
        @Suppress("UNCHECKED_CAST")
        val tobuys = document.get(FirestoreEvent.FIELD_TOBUYS) as? List<String> ?: emptyList()
        
        @Suppress("UNCHECKED_CAST")
        val todos = document.get(FirestoreEvent.FIELD_TODOS) as? List<String> ?: emptyList()
        
        return FirestoreEvent(
            id = id,
            color = color,
            date = date,
            description = description,
            event_name = eventName,
            location = location,
            participants = participants,
            tobuys = tobuys,
            todos = todos
        )
    }

    override fun modelToMap(model: FirestoreEvent): Map<String, Any?> {
        return mapOf(
            FirestoreEvent.FIELD_COLOR to model.color,
            FirestoreEvent.FIELD_DATE to model.date,
            FirestoreEvent.FIELD_DESCRIPTION to model.description,
            FirestoreEvent.FIELD_EVENT_NAME to model.event_name,
            FirestoreEvent.FIELD_LOCATION to model.location,
            FirestoreEvent.FIELD_PARTICIPANTS to model.participants,
            FirestoreEvent.FIELD_TOBUYS to model.tobuys,
            FirestoreEvent.FIELD_TODOS to model.todos
        )
    }

    override fun getModelId(model: FirestoreEvent): String {
        return model.id
    }

    override fun getEventsByUserId(userId: String): Flow<List<FirestoreEvent>> {
        return userRepository.getDocumentById(userId).map { user ->
            if (user == null) return@map emptyList()
            
            val eventIds = user.events.values.toList()
            val events = mutableListOf<FirestoreEvent>()
            
            for (eventId in eventIds) {
                val event = getDocumentByIdSync(eventId)
                if (event != null) {
                    events.add(event)
                }
            }
            
            events
        }
    }

    override fun getCurrentUserEvents(): Flow<List<FirestoreEvent>> {
        val currentUser = auth.currentUser ?: return emptyFlow()
        return getEventsByUserId(currentUser.uid)
    }

    override suspend fun addParticipantToEvent(eventId: String, userId: String, isCreator: Boolean) {
        val role = if (isCreator) FirestoreEvent.ROLE_CREATOR else FirestoreEvent.ROLE_PARTICIPANT
        
        getCollectionReference().document(eventId)
            .update(
                "${FirestoreEvent.FIELD_PARTICIPANTS}.$role", userId
            ).await()
    }

    override suspend fun removeParticipantFromEvent(eventId: String, userId: String) {
        // Récupérer l'événement pour trouver le rôle du participant
        val event = getDocumentByIdSync(eventId) ?: return
        
        // Trouver le rôle du participant
        val roleToRemove = event.participants.entries.find { it.value == userId }?.key ?: return
        
        // Supprimer le participant
        getCollectionReference().document(eventId)
            .update(
                "${FirestoreEvent.FIELD_PARTICIPANTS}.$roleToRemove", FieldValue.delete()
            ).await()
    }

    override suspend fun addTodoToEvent(eventId: String, todoId: String) {
        getCollectionReference().document(eventId)
            .update(
                FirestoreEvent.FIELD_TODOS, FieldValue.arrayUnion(todoId)
            ).await()
    }

    override suspend fun removeTodoFromEvent(eventId: String, todoId: String) {
        getCollectionReference().document(eventId)
            .update(
                FirestoreEvent.FIELD_TODOS, FieldValue.arrayRemove(todoId)
            ).await()
    }

    override suspend fun addToBuyToEvent(eventId: String, toBuyId: String) {
        getCollectionReference().document(eventId)
            .update(
                FirestoreEvent.FIELD_TOBUYS, FieldValue.arrayUnion(toBuyId)
            ).await()
    }

    override suspend fun removeToBuyFromEvent(eventId: String, toBuyId: String) {
        getCollectionReference().document(eventId)
            .update(
                FirestoreEvent.FIELD_TOBUYS, FieldValue.arrayRemove(toBuyId)
            ).await()
    }
} 