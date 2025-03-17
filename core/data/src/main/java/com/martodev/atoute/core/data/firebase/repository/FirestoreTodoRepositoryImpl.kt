package com.martodev.atoute.core.data.firebase.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.martodev.atoute.core.data.firebase.model.FirestoreTodo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Implémentation de FirestoreTodoRepository
 * @property firestore Instance de FirebaseFirestore
 */
class FirestoreTodoRepositoryImpl(
    firestore: FirebaseFirestore
) : FirestoreRepositoryImpl<FirestoreTodo>(firestore, FirestoreTodo.COLLECTION_NAME), FirestoreTodoRepository {

    override fun documentToModel(document: DocumentSnapshot): FirestoreTodo? {
        if (!document.exists()) return null
        
        val id = document.id
        val attributedTo = document.getString(FirestoreTodo.FIELD_ATTRIBUTED_TO)
        val event = document.getString(FirestoreTodo.FIELD_EVENT) ?: ""
        val name = document.getString(FirestoreTodo.FIELD_NAME) ?: ""
        val isCompleted = document.getBoolean(FirestoreTodo.FIELD_IS_COMPLETED) ?: false
        
        return FirestoreTodo(
            id = id,
            attributed_to = attributedTo,
            event = event,
            name = name,
            isCompleted = isCompleted
        )
    }

    override fun modelToMap(model: FirestoreTodo): Map<String, Any?> {
        return mapOf(
            FirestoreTodo.FIELD_ATTRIBUTED_TO to model.attributed_to,
            FirestoreTodo.FIELD_EVENT to model.event,
            FirestoreTodo.FIELD_NAME to model.name,
            FirestoreTodo.FIELD_IS_COMPLETED to model.isCompleted
        )
    }

    override fun getModelId(model: FirestoreTodo): String {
        return model.id
    }

    override fun getTodosByEventId(eventId: String): Flow<List<FirestoreTodo>> = callbackFlow {
        val listenerRegistration = getCollectionReference()
            .whereEqualTo(FirestoreTodo.FIELD_EVENT, eventId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val todos = snapshot?.documents?.mapNotNull { documentToModel(it) } ?: emptyList()
                trySend(todos)
            }
        
        awaitClose { listenerRegistration.remove() }
    }

    override fun getTodosByUserId(userId: String): Flow<List<FirestoreTodo>> = callbackFlow {
        val listenerRegistration = getCollectionReference()
            .whereEqualTo(FirestoreTodo.FIELD_ATTRIBUTED_TO, userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val todos = snapshot?.documents?.mapNotNull { documentToModel(it) } ?: emptyList()
                trySend(todos)
            }
        
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun assignTodoToUser(todoId: String, userId: String) {
        getCollectionReference().document(todoId)
            .update(FirestoreTodo.FIELD_ATTRIBUTED_TO, userId)
            .await()
    }

    override suspend fun unassignTodo(todoId: String) {
        getCollectionReference().document(todoId)
            .update(FirestoreTodo.FIELD_ATTRIBUTED_TO, null)
            .await()
    }

    override suspend fun setTodoCompleted(todoId: String, isCompleted: Boolean) {
        getCollectionReference().document(todoId)
            .update(FirestoreTodo.FIELD_IS_COMPLETED, isCompleted)
            .await()
    }
} 