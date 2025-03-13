package com.martodev.atoute.authentication.domain.model

/**
 * Représente le résultat d'une opération d'authentification
 */
sealed class AuthResult {
    /**
     * L'opération a réussi
     *
     * @property user L'utilisateur authentifié
     */
    data class Success(val user: User) : AuthResult()

    /**
     * L'opération a échoué
     *
     * @property message Message d'erreur
     */
    data class Error(val message: String) : AuthResult()
} 