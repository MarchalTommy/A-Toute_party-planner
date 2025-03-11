package com.martodev.atoute.home.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entité Room représentant une tâche (Todo) dans la base de données
 */
@Entity(
    tableName = "todos",
    foreignKeys = [
        ForeignKey(
            entity = PartyEntity::class,
            parentColumns = ["id"],
            childColumns = ["partyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("partyId")
    ]
)
data class TodoEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val isCompleted: Boolean = false,
    val assignedTo: String? = null,
    val partyId: String,
    val isPriority: Boolean = false
) 