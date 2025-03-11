package com.martodev.atoute.home.data.mapper

import com.martodev.atoute.home.data.entity.ToBuyEntity
import com.martodev.atoute.home.domain.model.ToBuy

/**
 * Mapper pour convertir ToBuyEntity en ToBuy et vice versa
 */

/**
 * Convertit une entité ToBuyEntity en modèle de domaine ToBuy
 * @param partyColor couleur de la fête à laquelle appartient cet article
 * @return ToBuy modèle de domaine
 */
fun ToBuyEntity.toDomain(partyColor: Long? = null): ToBuy {
    return ToBuy(
        id = id,
        title = title,
        quantity = quantity,
        estimatedPrice = estimatedPrice,
        isPurchased = isPurchased,
        assignedTo = assignedTo,
        partyId = partyId,
        partyColor = partyColor,
        isPriority = isPriority
    )
}

/**
 * Convertit un modèle de domaine ToBuy en entité ToBuyEntity
 * @return ToBuyEntity entité pour la base de données
 */
fun ToBuy.toEntity(): ToBuyEntity {
    return ToBuyEntity(
        id = id,
        title = title,
        quantity = quantity,
        estimatedPrice = estimatedPrice,
        isPurchased = isPurchased,
        assignedTo = assignedTo,
        partyId = partyId,
        isPriority = isPriority
    )
} 