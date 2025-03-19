package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.home.domain.model.Party
import com.martodev.atoute.home.domain.repository.PartyRepository
import com.martodev.atoute.home.domain.service.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Cas d'utilisation pour récupérer les parties
 */
class GetPartiesUseCase(
    private val partyRepository: PartyRepository,
    private val authService: AuthService
) {
    
    /**
     * Récupère les parties
     */
    operator fun invoke(): Flow<List<Party>> {
        val currentUserId = authService.getCurrentUserId()
        
        // Si aucun utilisateur n'est connecté, retourner une liste vide
        if (currentUserId == null) {
            return flowOf(emptyList())
        }
        
        // Pour l'instant, récupérer toutes les parties
        return partyRepository.getAllParties()
    }
} 