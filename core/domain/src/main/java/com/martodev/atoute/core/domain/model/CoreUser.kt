package com.martodev.atoute.core.domain.model

/**
 * Représente un utilisateur dans le système
 *
 * @property id Identifiant unique de l'utilisateur
 * @property username Nom d'utilisateur/pseudo choisi par l'utilisateur
 * @property email Email de l'utilisateur (optionnel si non inscrit)
 * @property isPremium Indique si l'utilisateur a souscrit à l'offre premium
 * @property preferences Préférences de l'utilisateur
 */
data class CoreUser(
    val id: String,
    val username: String,
    val email: String? = null,
    val isPremium: Boolean = false,
    val drinksAlcohol: Boolean,
    val isHalal: Boolean,
    val isVegetarian: Boolean,
    val isVegan: Boolean,
    val allergies: String
)