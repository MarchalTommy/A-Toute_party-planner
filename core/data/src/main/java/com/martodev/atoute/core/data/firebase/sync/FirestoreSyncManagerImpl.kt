package com.martodev.atoute.core.data.firebase.sync

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.martodev.atoute.core.data.dao.PartyDao
import com.martodev.atoute.core.data.dao.ToBuyDao
import com.martodev.atoute.core.data.dao.TodoDao
import com.martodev.atoute.core.data.entity.PartyEntity
import com.martodev.atoute.core.data.entity.ToBuyEntity
import com.martodev.atoute.core.data.entity.TodoEntity
import com.martodev.atoute.core.data.firebase.model.FirestoreEvent
import com.martodev.atoute.core.data.firebase.model.FirestoreToBuy
import com.martodev.atoute.core.data.firebase.model.FirestoreTodo
import com.martodev.atoute.core.data.firebase.repository.FirestoreEventRepository
import com.martodev.atoute.core.data.firebase.repository.FirestoreToBuyRepository
import com.martodev.atoute.core.data.firebase.repository.FirestoreTodoRepository
import com.martodev.atoute.core.data.firebase.repository.FirestoreUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Implémentation de FirestoreSyncManager
 */
class FirestoreSyncManagerImpl(
    private val auth: FirebaseAuth,
    private val userRepository: FirestoreUserRepository,
    private val eventRepository: FirestoreEventRepository,
    private val todoRepository: FirestoreTodoRepository,
    private val toBuyRepository: FirestoreToBuyRepository,
    private val partyDao: PartyDao,
    private val todoDao: TodoDao,
    private val toBuyDao: ToBuyDao
) : FirestoreSyncManager {

    override fun syncData(): Flow<SyncState> = flow {
        emit(SyncState.Loading)

        try {
            val currentUser = auth.currentUser ?: throw Exception("Utilisateur non connecté")
            val firestoreUser = userRepository.getDocumentByIdSync(currentUser.uid)
                ?: throw Exception("Utilisateur non trouvé dans Firestore")

            var updatedItems = 0

            // Synchroniser les événements de l'utilisateur
            val eventIds = firestoreUser.events.values.toList()
            for (eventId in eventIds) {
                val syncState = syncEvent(eventId)
                if (syncState is SyncState.Success) {
                    updatedItems += syncState.updatedItems
                }
            }

            emit(SyncState.Success(updatedItems))
        } catch (e: Exception) {
            emit(SyncState.Error("Erreur lors de la synchronisation: ${e.message}", e))
        }
    }.catch { e ->
        emit(SyncState.Error("Erreur lors de la synchronisation: ${e.message}", e as? Exception))
    }

    override suspend fun syncEvent(eventId: String): SyncState {
        return try {
            var updatedItems = 0

            // Récupérer l'événement depuis Firestore
            val firestoreEvent = eventRepository.getDocumentByIdSync(eventId)
                ?: return SyncState.Error("Événement non trouvé dans Firestore")

            // Récupérer l'événement local
            val localEvent = partyDao.getPartyById(eventId)

            // Si l'événement existe localement, vérifier s'il a été modifié
            if (localEvent != null) {
                // Convertir le timestamp Firestore en LocalDateTime
                val firestoreDate = firestoreEvent.date?.let {
                    LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(it.seconds * 1000 + it.nanoseconds / 1000000),
                        ZoneId.systemDefault()
                    )
                }

                // Mettre à jour l'événement local si nécessaire
                if (firestoreDate != null && firestoreDate != localEvent.date ||
                    firestoreEvent.event_name != localEvent.title ||
                    firestoreEvent.description != localEvent.description ||
                    firestoreEvent.location != localEvent.location ||
                    firestoreEvent.color != localEvent.color
                ) {

                    val updatedEvent = PartyEntity(
                        id = eventId,
                        title = firestoreEvent.event_name,
                        date = firestoreDate ?: localEvent.date,
                        location = firestoreEvent.location,
                        description = firestoreEvent.description,
                        color = firestoreEvent.color,
                        todoCount = localEvent.todoCount,
                        completedTodoCount = localEvent.completedTodoCount
                    )

                    partyDao.insertParty(updatedEvent)
                    updatedItems++
                }
            } else {
                // L'événement n'existe pas localement, le créer
                val firestoreDate = firestoreEvent.date?.let {
                    LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(it.seconds * 1000 + it.nanoseconds / 1000000),
                        ZoneId.systemDefault()
                    )
                } ?: LocalDateTime.now()

                val newEvent = PartyEntity(
                    id = eventId,
                    title = firestoreEvent.event_name,
                    date = firestoreDate,
                    location = firestoreEvent.location,
                    description = firestoreEvent.description,
                    color = firestoreEvent.color,
                    todoCount = 0,
                    completedTodoCount = 0
                )

                partyDao.insertParty(newEvent)
                updatedItems++
            }

            // Synchroniser les todos et tobuys de l'événement
            val todoSyncState = syncEventTodos(eventId)
            if (todoSyncState is SyncState.Success) {
                updatedItems += todoSyncState.updatedItems
            }

            val toBuySyncState = syncEventToBuys(eventId)
            if (toBuySyncState is SyncState.Success) {
                updatedItems += toBuySyncState.updatedItems
            }

            SyncState.Success(updatedItems)
        } catch (e: Exception) {
            SyncState.Error("Erreur lors de la synchronisation de l'événement: ${e.message}", e)
        }
    }

    override suspend fun syncEventTodos(eventId: String): SyncState {
        return try {
            var updatedItems = 0

            // Récupérer les todos de l'événement depuis Firestore
            val firestoreTodos =
                todoRepository.getTodosByEventId(eventId).catch { emptyList<FirestoreTodo>() }
                    .collect { todos ->
                        // Récupérer les todos locaux
                        val localTodos = todoDao.getTodosByPartyIdSync(eventId)

                        // Créer une map des todos locaux par ID
                        val localTodosMap = localTodos.associateBy { it.id }

                        // Pour chaque todo Firestore
                        for (firestoreTodo in todos) {
                            val localTodo = localTodosMap[firestoreTodo.id]

                            // Si le todo existe localement, vérifier s'il a été modifié
                            if (localTodo != null) {
                                if (firestoreTodo.name != localTodo.title ||
                                    firestoreTodo.isCompleted != localTodo.isCompleted ||
                                    firestoreTodo.attributed_to != localTodo.assignedTo
                                ) {

                                    val updatedTodo = TodoEntity(
                                        id = firestoreTodo.id,
                                        title = firestoreTodo.name,
                                        isCompleted = firestoreTodo.isCompleted,
                                        assignedTo = firestoreTodo.attributed_to,
                                        partyId = eventId,
                                        isPriority = localTodo.isPriority
                                    )

                                    todoDao.insertTodo(updatedTodo)
                                    updatedItems++
                                }
                            } else {
                                // Le todo n'existe pas localement, le créer
                                val newTodo = TodoEntity(
                                    id = firestoreTodo.id,
                                    title = firestoreTodo.name,
                                    isCompleted = firestoreTodo.isCompleted,
                                    assignedTo = firestoreTodo.attributed_to,
                                    partyId = eventId,
                                    isPriority = false
                                )

                                todoDao.insertTodo(newTodo)
                                updatedItems++
                            }
                        }
                    }

            SyncState.Success(updatedItems)
        } catch (e: Exception) {
            SyncState.Error("Erreur lors de la synchronisation des todos: ${e.message}", e)
        }
    }

    override suspend fun syncEventToBuys(eventId: String): SyncState {
        return try {
            var updatedItems = 0

            // Récupérer les tobuys de l'événement depuis Firestore
            val firestoreTobuys =
                toBuyRepository.getToBuysByEventId(eventId).catch { emptyList<FirestoreToBuy>() }
                    .collect { tobuys ->
                        // Récupérer les tobuys locaux
                        val localTobuys = toBuyDao.getToBuysByPartyIdSync(eventId)

                        // Créer une map des tobuys locaux par ID
                        val localTobuysMap = localTobuys.associateBy { it.id }

                        // Pour chaque tobuy Firestore
                        for (firestoreTobuy in tobuys) {
                            val localTobuy = localTobuysMap[firestoreTobuy.id]

                            // Si le tobuy existe localement, vérifier s'il a été modifié
                            if (localTobuy != null) {
                                if (firestoreTobuy.name != localTobuy.title ||
                                    firestoreTobuy.isPurchased != localTobuy.isPurchased ||
                                    firestoreTobuy.attributed_to != localTobuy.assignedTo ||
                                    firestoreTobuy.quantity != localTobuy.quantity
                                ) {

                                    val updatedTobuy = ToBuyEntity(
                                        id = firestoreTobuy.id,
                                        title = firestoreTobuy.name,
                                        quantity = firestoreTobuy.quantity ?: 1,
                                        estimatedPrice = localTobuy.estimatedPrice,
                                        isPurchased = firestoreTobuy.isPurchased,
                                        assignedTo = firestoreTobuy.attributed_to,
                                        partyId = eventId,
                                        isPriority = localTobuy.isPriority
                                    )

                                    toBuyDao.insertToBuy(updatedTobuy)
                                    updatedItems++
                                }
                            } else {
                                // Le tobuy n'existe pas localement, le créer
                                val newTobuy = ToBuyEntity(
                                    id = firestoreTobuy.id,
                                    title = firestoreTobuy.name,
                                    quantity = firestoreTobuy.quantity ?: 1,
                                    estimatedPrice = null,
                                    isPurchased = firestoreTobuy.isPurchased,
                                    assignedTo = firestoreTobuy.attributed_to,
                                    partyId = eventId,
                                    isPriority = false
                                )

                                toBuyDao.insertToBuy(newTobuy)
                                updatedItems++
                            }
                        }
                    }

            SyncState.Success(updatedItems)
        } catch (e: Exception) {
            SyncState.Error("Erreur lors de la synchronisation des tobuys: ${e.message}", e)
        }
    }

    override suspend fun pushLocalChanges(): SyncState {
        return try {
            var updatedItems = 0

            // Récupérer l'utilisateur courant
            val currentUser = auth.currentUser ?: throw Exception("Utilisateur non connecté")

            // Récupérer tous les événements locaux
            val localEvents = partyDao.getAllPartiesSync()

            for (localEvent in localEvents) {
                // Vérifier si l'événement existe dans Firestore
                val firestoreEvent = eventRepository.getDocumentByIdSync(localEvent.id)

                if (firestoreEvent != null) {
                    // Convertir la date locale en Timestamp Firestore
                    val firestoreDate = Timestamp(
                        localEvent.date.atZone(ZoneId.systemDefault()).toEpochSecond(),
                        localEvent.date.atZone(ZoneId.systemDefault()).nano
                    )

                    // Mettre à jour l'événement Firestore si nécessaire
                    if (firestoreEvent.event_name != localEvent.title ||
                        firestoreEvent.description != localEvent.description ||
                        firestoreEvent.location != localEvent.location ||
                        firestoreEvent.color != localEvent.color ||
                        firestoreEvent.date?.seconds != firestoreDate.seconds
                    ) {

                        val updatedEvent = FirestoreEvent(
                            id = localEvent.id,
                            color = localEvent.color,
                            date = firestoreDate,
                            description = localEvent.description,
                            event_name = localEvent.title,
                            location = localEvent.location,
                            participants = firestoreEvent.participants,
                            tobuys = firestoreEvent.tobuys,
                            todos = firestoreEvent.todos
                        )

                        eventRepository.saveDocument(updatedEvent)
                        updatedItems++
                    }

                    // Synchroniser les todos et tobuys locaux vers Firestore
                    val localTodos = todoDao.getTodosByPartyIdSync(localEvent.id)
                    for (localTodo in localTodos) {
                        val firestoreTodo = todoRepository.getDocumentByIdSync(localTodo.id)

                        if (firestoreTodo != null) {
                            // Mettre à jour le todo Firestore si nécessaire
                            if (firestoreTodo.name != localTodo.title ||
                                firestoreTodo.isCompleted != localTodo.isCompleted ||
                                firestoreTodo.attributed_to != localTodo.assignedTo
                            ) {

                                val updatedTodo = FirestoreTodo(
                                    id = localTodo.id,
                                    attributed_to = localTodo.assignedTo,
                                    event = localEvent.id,
                                    name = localTodo.title,
                                    isCompleted = localTodo.isCompleted
                                )

                                todoRepository.saveDocument(updatedTodo)
                                updatedItems++
                            }
                        } else {
                            // Le todo n'existe pas dans Firestore, le créer
                            val newTodo = FirestoreTodo(
                                id = localTodo.id,
                                attributed_to = localTodo.assignedTo,
                                event = localEvent.id,
                                name = localTodo.title,
                                isCompleted = localTodo.isCompleted
                            )

                            val todoId = todoRepository.saveDocument(newTodo)

                            // Ajouter le todo à l'événement
                            eventRepository.addTodoToEvent(localEvent.id, todoId)
                            updatedItems++
                        }
                    }

                    val localTobuys = toBuyDao.getToBuysByPartyIdSync(localEvent.id)
                    for (localTobuy in localTobuys) {
                        val firestoreTobuy = toBuyRepository.getDocumentByIdSync(localTobuy.id)

                        if (firestoreTobuy != null) {
                            // Mettre à jour le tobuy Firestore si nécessaire
                            if (firestoreTobuy.name != localTobuy.title ||
                                firestoreTobuy.isPurchased != localTobuy.isPurchased ||
                                firestoreTobuy.attributed_to != localTobuy.assignedTo ||
                                firestoreTobuy.quantity != localTobuy.quantity
                            ) {

                                val updatedTobuy = FirestoreToBuy(
                                    id = localTobuy.id,
                                    attributed_to = localTobuy.assignedTo,
                                    event = localEvent.id,
                                    name = localTobuy.title,
                                    quantity = localTobuy.quantity,
                                    isPurchased = localTobuy.isPurchased
                                )

                                toBuyRepository.saveDocument(updatedTobuy)
                                updatedItems++
                            }
                        } else {
                            // Le tobuy n'existe pas dans Firestore, le créer
                            val newTobuy = FirestoreToBuy(
                                id = localTobuy.id,
                                attributed_to = localTobuy.assignedTo,
                                event = localEvent.id,
                                name = localTobuy.title,
                                quantity = localTobuy.quantity,
                                isPurchased = localTobuy.isPurchased
                            )

                            val toBuyId = toBuyRepository.saveDocument(newTobuy)

                            // Ajouter le tobuy à l'événement
                            eventRepository.addToBuyToEvent(localEvent.id, toBuyId)
                            updatedItems++
                        }
                    }
                } else {
                    // L'événement n'existe pas dans Firestore, le créer
                    val firestoreDate = Timestamp(
                        localEvent.date.atZone(ZoneId.systemDefault()).toEpochSecond(),
                        localEvent.date.atZone(ZoneId.systemDefault()).nano
                    )

                    val newEvent = FirestoreEvent(
                        id = localEvent.id,
                        color = localEvent.color,
                        date = firestoreDate,
                        description = localEvent.description,
                        event_name = localEvent.title,
                        location = localEvent.location,
                        participants = mapOf(FirestoreEvent.ROLE_CREATOR to currentUser.uid),
                        tobuys = emptyList(),
                        todos = emptyList()
                    )

                    val eventId = eventRepository.saveDocument(newEvent)

                    // Ajouter l'événement à l'utilisateur
                    userRepository.addEventToUser(currentUser.uid, eventId, true)

                    // Créer les todos et tobuys dans Firestore
                    val localTodos = todoDao.getTodosByPartyIdSync(localEvent.id)
                    for (localTodo in localTodos) {
                        val newTodo = FirestoreTodo(
                            id = localTodo.id,
                            attributed_to = localTodo.assignedTo,
                            event = eventId,
                            name = localTodo.title,
                            isCompleted = localTodo.isCompleted
                        )

                        val todoId = todoRepository.saveDocument(newTodo)

                        // Ajouter le todo à l'événement
                        eventRepository.addTodoToEvent(eventId, todoId)
                    }

                    val localTobuys = toBuyDao.getToBuysByPartyIdSync(localEvent.id)
                    for (localTobuy in localTobuys) {
                        val newTobuy = FirestoreToBuy(
                            id = localTobuy.id,
                            attributed_to = localTobuy.assignedTo,
                            event = eventId,
                            name = localTobuy.title,
                            quantity = localTobuy.quantity,
                            isPurchased = localTobuy.isPurchased
                        )

                        val toBuyId = toBuyRepository.saveDocument(newTobuy)

                        // Ajouter le tobuy à l'événement
                        eventRepository.addToBuyToEvent(eventId, toBuyId)
                    }

                    updatedItems++
                }
            }

            SyncState.Success(updatedItems)
        } catch (e: Exception) {
            SyncState.Error("Erreur lors de l'envoi des modifications locales: ${e.message}", e)
        }
    }
} 