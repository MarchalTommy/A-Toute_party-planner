package com.martodev.atoute.home.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.martodev.atoute.home.data.util.DateConverters
import java.time.LocalDateTime

/**
 * Entité Room représentant une Party dans la base de données
 */
@Entity(tableName = "parties")
@TypeConverters(DateConverters::class)
data class PartyEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val date: LocalDateTime,
    val location: String = "",
    val description: String = "",
    val color: Long? = null,
    val todoCount: Int = 0,
    val completedTodoCount: Int = 0
) 