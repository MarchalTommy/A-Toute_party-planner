package com.martodev.atoute.core.data.firebase.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Implémentation abstraite de FirestoreRepository
 * @param T Type du modèle
 * @property firestore Instance de FirebaseFirestore
 * @property collectionPath Chemin de la collection
 */
abstract class FirestoreRepositoryImpl<T : Any>(
    protected val firestore: FirebaseFirestore,
    protected val collectionPath: String
) : FirestoreRepository<T> {

    /**
     * Convertit un DocumentSnapshot en modèle
     * @param document DocumentSnapshot à convertir
     * @return Modèle converti ou null si le document n'existe pas
     */
    protected abstract fun documentToModel(document: DocumentSnapshot): T?

    /**
     * Convertit un modèle en Map pour Firestore
     * @param model Modèle à convertir
     * @return Map pour Firestore
     */
    protected abstract fun modelToMap(model: T): Map<String, Any?>

    /**
     * Récupère l'ID d'un modèle
     * @param model Modèle
     * @return ID du modèle
     */
    protected abstract fun getModelId(model: T): String

    /**
     * Récupère la référence à la collection
     * @return Référence à la collection
     */
    protected fun getCollectionReference(): CollectionReference {
        return firestore.collection(collectionPath)
    }

    override fun getDocumentById(id: String): Flow<T?> = callbackFlow {
        val listenerRegistration = getCollectionReference().document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                trySend(snapshot?.let { documentToModel(it) })
            }
        
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun getDocumentByIdSync(id: String): T? {
        return try {
            val document = getCollectionReference().document(id).get().await()
            documentToModel(document)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveDocument(document: T): String {
        val id = getModelId(document).takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
        val data = modelToMap(document)
        
        getCollectionReference().document(id).set(data).await()
        return id
    }

    override suspend fun deleteDocument(id: String) {
        getCollectionReference().document(id).delete().await()
    }
} 