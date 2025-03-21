package com.martodev.atoute.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.martodev.atoute.core.domain.model.CoreUser

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
    fun toDomainCoreModel(): CoreUser {
        return CoreUser(
            id = id,
            username = username,
            email = email,
            isPremium = isPremium,
            drinksAlcohol = drinksAlcohol,
            isHalal = isHalal,
            isVegetarian = isVegetarian,
            isVegan = isVegan,
            allergies = allergies
        )
    }

    companion object {
        /**
         * Crée une entité à partir d'un modèle de domaine
         */
        fun fromDomainCoreModel(user: CoreUser): UserEntity {
            return UserEntity(
                id = user.id,
                username = user.username,
                email = user.email,
                isPremium = user.isPremium,
                drinksAlcohol = user.drinksAlcohol,
                isHalal = user.isHalal,
                isVegetarian = user.isVegetarian,
                isVegan = user.isVegan,
                allergies = user.allergies
            )
        }
    }
}