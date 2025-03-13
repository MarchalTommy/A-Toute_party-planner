package com.martodev.atoute.home.domain.usecase

import com.martodev.atoute.authentication.domain.usecase.GetCurrentUserPremiumStatusUseCase
import com.martodev.atoute.home.domain.repository.PartyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

/**
 * Cas d'utilisation pour vérifier si l'utilisateur a atteint la limite d'événements pour un utilisateur non-premium
 * 
 * Les utilisateurs non-premium sont limités à 3 événements simultanés.
 */
class CheckPartyLimitUseCase(
    private val partyRepository: PartyRepository,
    private val isPremiumUseCase: GetCurrentUserPremiumStatusUseCase
) {
    companion object {
        const val NON_PREMIUM_PARTY_LIMIT = 3
    }
    
    /**
     * Vérifie si l'utilisateur peut créer un nouvel événement
     * 
     * @return Flow contenant un Boolean indiquant si l'utilisateur peut créer un nouvel événement
     *         true: l'utilisateur peut créer un nouvel événement
     *         false: l'utilisateur a atteint sa limite
     */
    operator fun invoke(): Flow<Boolean> {
        return partyRepository.getAllParties().combine(isPremiumUseCase()) { parties, isPremium ->
            // Les utilisateurs premium n'ont pas de limite
            if (isPremium) {
                return@combine true
            }
            
            // Pour les utilisateurs non-premium, vérifier la limite
            parties.size < NON_PREMIUM_PARTY_LIMIT
        }
    }
    
    /**
     * Vérifie de manière synchrone si l'utilisateur peut créer un nouvel événement
     * 
     * @return Boolean indiquant si l'utilisateur peut créer un nouvel événement
     *         true: l'utilisateur peut créer un nouvel événement
     *         false: l'utilisateur a atteint sa limite
     */
    suspend fun checkSync(): Boolean {
        val isPremium = isPremiumUseCase().first()
        
        // Les utilisateurs premium n'ont pas de limite
        if (isPremium) {
            return true
        }
        
        // Pour les utilisateurs non-premium, vérifier la limite
        val parties = partyRepository.getAllParties().first()
        return parties.size < NON_PREMIUM_PARTY_LIMIT
    }
} 