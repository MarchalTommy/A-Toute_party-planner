package com.martodev.atoute.core.utils

import java.time.LocalDateTime

/**
 * Utilitaire de validation des données pour éviter la duplication de logique de validation
 * à travers différents composants de l'application.
 */
object ValidationUtils {
    /**
     * Valide les données d'un événement
     * @param title Titre de l'événement
     * @param date Date de l'événement
     * @param location Lieu de l'événement
     * @return true si les données sont valides, false sinon
     */
    fun validatePartyData(title: String, date: LocalDateTime, location: String): Boolean {
        if (title.isBlank()) return false
        if (date.isBefore(LocalDateTime.now())) return false
        if (location.isBlank()) return false
        return true
    }
    
    /**
     * Valide les données d'une tâche (todo)
     * @param title Titre de la tâche
     * @param partyId ID de l'événement associé
     * @return true si les données sont valides, false sinon
     */
    fun validateTodoData(title: String, partyId: String): Boolean {
        if (title.isBlank()) return false
        if (partyId.isBlank()) return false
        return true
    }
    
    /**
     * Valide les données d'un élément à acheter (tobuy)
     * @param title Titre de l'élément
     * @param quantity Quantité
     * @param partyId ID de l'événement associé
     * @return true si les données sont valides, false sinon
     */
    fun validateToBuyData(title: String, quantity: Int, partyId: String): Boolean {
        if (title.isBlank()) return false
        if (quantity <= 0) return false
        if (partyId.isBlank()) return false
        return true
    }
    
    /**
     * Valide les préférences utilisateur
     * @param username Nom d'utilisateur
     * @return true si les préférences sont valides, false sinon
     */
    fun validateUserPreferences(username: String): Boolean {
        return username.isNotBlank()
    }
} 