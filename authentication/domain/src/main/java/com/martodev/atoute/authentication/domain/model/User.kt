package com.martodev.atoute.authentication.domain.model

import com.martodev.atoute.core.domain.model.CoreUser

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
) {

    companion object {
        /**
         * Crée une entité à partir d'un modèle de domaine
         */
        fun fromDomainModel(user: User): CoreUser {
            return CoreUser(
                id = user.id,
                username = user.username,
                email = user.email,
                isPremium = user.isPremium,
                drinksAlcohol = user.preferences.drinksAlcohol,
                isHalal = user.preferences.isHalal,
                isVegetarian = user.preferences.isVegetarian,
                isVegan = user.preferences.isVegan,
                allergies = user.preferences.hasAllergies.joinToString(",")
            )
        }
    }
}
/**
 * Convertit l'entité en modèle de domaine
 */
fun CoreUser.toDomainModel(): User {
    return User(
        id = id,
        username = username,
        email = email,
        isPremium = isPremium,
        preferences = UserPreferences(
            drinksAlcohol = drinksAlcohol,
            isHalal = isHalal,
            isVegetarian = isVegetarian,
            isVegan = isVegan,
            hasAllergies = if (allergies.isBlank()) emptyList() else allergies.split(",")
        )
    )
}