package com.martodev.atoute.authentication.domain.model

/**
 * Préférences de l'utilisateur
 *
 * @property drinksAlcohol Indique si l'utilisateur consomme de l'alcool
 * @property isHalal Indique si l'utilisateur suit un régime halal
 * @property isVegetarian Indique si l'utilisateur est végétarien
 * @property isVegan Indique si l'utilisateur est végétalien
 * @property hasAllergies Liste des allergies de l'utilisateur
 */
data class UserPreferences(
    val drinksAlcohol: Boolean = true,
    val isHalal: Boolean = false,
    val isVegetarian: Boolean = false,
    val isVegan: Boolean = false,
    val hasAllergies: List<String> = emptyList()
) 