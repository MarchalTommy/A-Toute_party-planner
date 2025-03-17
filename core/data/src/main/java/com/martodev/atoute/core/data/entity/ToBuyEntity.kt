package com.martodev.atoute.core.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entité Room représentant un article à acheter (ToBuy) dans la base de données
 */
@Entity(
    tableName = "to_buys",
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
data class ToBuyEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val quantity: Int = 1,
    val estimatedPrice: Float? = null,
    val isPurchased: Boolean = false,
    val assignedTo: String? = null,
    val partyId: String,
    val isPriority: Boolean = false
) 