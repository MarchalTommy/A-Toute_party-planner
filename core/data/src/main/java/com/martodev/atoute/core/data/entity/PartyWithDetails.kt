package com.martodev.atoute.core.data.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Classe contenant une Party avec tous ses détails associés (participants, tâches, achats)
 */
data class PartyWithDetails(
    @Embedded
    val party: PartyEntity,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "partyId"
    )
    val participants: List<ParticipantEntity>,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "partyId"
    )
    val todos: List<TodoEntity>,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "partyId"
    )
    val toBuys: List<ToBuyEntity>
) 