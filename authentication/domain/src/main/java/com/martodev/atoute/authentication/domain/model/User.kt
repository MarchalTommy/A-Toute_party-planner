package com.martodev.atoute.authentication.domain.model

/**
 * Représente un utilisateur dans le système
 *
 * @property id Identifiant unique de l'utilisateur
 * @property username Nom d'utilisateur/pseudo choisi par l'utilisateur
 * @property email Email de l'utilisateur (optionnel si non inscrit)
 * @property isPremium Indique si l'utilisateur a souscrit à l'offre premium
 * @property preferences Préférences de l'utilisateur
 */
data class User(
    val id: String,
    val username: String,
    val email: String? = null,
    val isPremium: Boolean = false,
    val preferences: UserPreferences = UserPreferences()
) 