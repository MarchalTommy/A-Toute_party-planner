package com.martodev.atoute.party.data.repository

import com.martodev.atoute.home.domain.repository.ToBuyRepository as HomeToBuyRepository
import com.martodev.atoute.party.domain.model.ToBuy
import com.martodev.atoute.party.domain.repository.ToBuyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flow

/**
 * Adaptateur pour le repository ToBuyRepository du module Home
 * Permet d'utiliser l'implémentation du module Home avec l'interface du module Party
 */
class ToBuyRepositoryAdapter(
    private val homeRepository: HomeToBuyRepository
) : ToBuyRepository {

    override fun getAllToBuys(): Flow<List<ToBuy>> {
        return homeRepository.getAllToBuys().map { homeToBuys ->
            homeToBuys.map { homeToBuy ->
                ToBuy(
                    id = homeToBuy.id,
                    title = homeToBuy.title,
                    quantity = homeToBuy.quantity,
                    estimatedPrice = homeToBuy.estimatedPrice,
                    isPurchased = homeToBuy.isPurchased,
                    assignedTo = homeToBuy.assignedTo,
                    partyId = homeToBuy.partyId,
                    partyColor = homeToBuy.partyColor,
                    isPriority = homeToBuy.isPriority
                )
            }
        }
    }

    override fun getPriorityToBuys(): Flow<List<ToBuy>> {
        return homeRepository.getPriorityToBuys().map { homeToBuys ->
            homeToBuys.map { homeToBuy ->
                ToBuy(
                    id = homeToBuy.id,
                    title = homeToBuy.title,
                    quantity = homeToBuy.quantity,
                    estimatedPrice = homeToBuy.estimatedPrice,
                    isPurchased = homeToBuy.isPurchased,
                    assignedTo = homeToBuy.assignedTo,
                    partyId = homeToBuy.partyId,
                    partyColor = homeToBuy.partyColor,
                    isPriority = homeToBuy.isPriority
                )
            }
        }
    }

    override fun getToBuysByParty(partyId: String): Flow<List<ToBuy>> {
        return homeRepository.getToBuysByParty(partyId).map { homeToBuys ->
            homeToBuys.map { homeToBuy ->
                ToBuy(
                    id = homeToBuy.id,
                    title = homeToBuy.title,
                    quantity = homeToBuy.quantity,
                    estimatedPrice = homeToBuy.estimatedPrice,
                    isPurchased = homeToBuy.isPurchased,
                    assignedTo = homeToBuy.assignedTo,
                    partyId = homeToBuy.partyId,
                    partyColor = homeToBuy.partyColor,
                    isPriority = homeToBuy.isPriority
                )
            }
        }
    }

    override fun getToBuyById(toBuyId: String): Flow<ToBuy?> {
        return flow {
            val toBuy = homeRepository.getToBuyById(toBuyId)
            emit(toBuy?.let {
                ToBuy(
                    id = it.id,
                    title = it.title,
                    quantity = it.quantity,
                    estimatedPrice = it.estimatedPrice,
                    isPurchased = it.isPurchased,
                    assignedTo = it.assignedTo,
                    partyId = it.partyId,
                    partyColor = it.partyColor,
                    isPriority = it.isPriority
                )
            })
        }
    }

    override suspend fun saveToBuy(toBuy: ToBuy) {
        homeRepository.saveToBuy(
            com.martodev.atoute.home.domain.model.ToBuy(
                id = toBuy.id,
                title = toBuy.title,
                quantity = toBuy.quantity,
                estimatedPrice = toBuy.estimatedPrice,
                isPurchased = toBuy.isPurchased,
                assignedTo = toBuy.assignedTo,
                partyId = toBuy.partyId,
                partyColor = toBuy.partyColor,
                isPriority = toBuy.isPriority
            )
        )
    }

    override suspend fun saveToBuys(toBuys: List<ToBuy>) {
        homeRepository.saveToBuys(
            toBuys.map {
                com.martodev.atoute.home.domain.model.ToBuy(
                    id = it.id,
                    title = it.title,
                    quantity = it.quantity,
                    estimatedPrice = it.estimatedPrice,
                    isPurchased = it.isPurchased,
                    assignedTo = it.assignedTo,
                    partyId = it.partyId,
                    partyColor = it.partyColor,
                    isPriority = it.isPriority
                )
            }
        )
    }

    override suspend fun updateToBuyStatus(toBuyId: String, isPurchased: Boolean) {
        homeRepository.updateToBuyStatus(toBuyId, isPurchased)
    }

    override suspend fun updateToBuyPriority(toBuyId: String, isPriority: Boolean) {
        homeRepository.updateToBuyPriority(toBuyId, isPriority)
    }

    override suspend fun deleteToBuy(toBuyId: String) {
        homeRepository.deleteToBuy(toBuyId)
    }

    override suspend fun deleteToBuysByParty(partyId: String) {
        homeRepository.deleteToBuysByParty(partyId)
    }
} 