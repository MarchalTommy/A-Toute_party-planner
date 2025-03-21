package com.martodev.atoute.core.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entité Room représentant un participant à une Party
 */
@Entity(
    tableName = "participants",
    foreignKeys = [
        ForeignKey(
            entity = PartyEntity::class,
            parentColumns = ["id"],
            childColumns = ["partyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("partyId"),
        Index("userId")
    ]
)
data class ParticipantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val partyId: String,
    val userId: String = "" // ID de l'utilisateur Firebase
) 