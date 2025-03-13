package com.martodev.atoute.authentication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.martodev.atoute.authentication.domain.model.User
import com.martodev.atoute.authentication.domain.model.UserPreferences

/**
 * Entité Room représentant un utilisateur dans la base de données
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val email: String?,
    val isPremium: Boolean,
    val drinksAlcohol: Boolean,
    val isHalal: Boolean,
    val isVegetarian: Boolean,
    val isVegan: Boolean,
    val allergies: String // Liste d'allergies séparées par des virgules
) {
    /**
     * Convertit l'entité en modèle de domaine
     */
    fun toDomainModel(): User {
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

    companion object {
        /**
         * Crée une entité à partir d'un modèle de domaine
         */
        fun fromDomainModel(user: User): UserEntity {
            return UserEntity(
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