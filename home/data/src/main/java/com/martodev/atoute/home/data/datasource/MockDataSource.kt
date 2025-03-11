package com.martodev.atoute.home.data.datasource

import com.martodev.atoute.home.domain.model.Party
import com.martodev.atoute.home.domain.model.ToBuy
import com.martodev.atoute.home.domain.model.Todo
import java.time.LocalDateTime
import java.util.UUID

/**
 * Source de données mock pour fournir des données fictives à l'application
 */
class MockDataSource {

    // Identifiants prédéfinis pour faciliter les relations entre les objets
    companion object {
        // IDs des parties
        const val BIRTHDAY_PARTY_ID = "party-001"
        const val GAME_NIGHT_ID = "party-002"
        const val SUMMER_BBQ_ID = "party-003"
        const val NEW_YEAR_PARTY_ID = "party-004"
        const val HOUSEWARMING_ID = "party-005"
        
        // Couleurs prédéfinies pour les parties
        const val PINK_COLOR = 0xFFE91E63 // Rose
        const val BLUE_COLOR = 0xFF2196F3 // Bleu
        const val GREEN_COLOR = 0xFF4CAF50 // Vert
        const val PURPLE_COLOR = 0xFF9C27B0 // Violet
        const val ORANGE_COLOR = 0xFFFF9800 // Orange
    }
    
    /**
     * Génère une liste de Party fictives
     */
    fun getParties(): List<Party> {
        return listOf(
            Party(
                id = BIRTHDAY_PARTY_ID,
                title = "Anniversaire de Julie",
                date = LocalDateTime.now().plusDays(15),
                location = "12 Rue des Lilas, Paris",
                description = "Fête d'anniversaire surprise pour Julie qui fête ses 30 ans. " +
                        "N'oubliez pas de venir déguisés et d'apporter un petit cadeau!",
                participants = listOf("Marc", "Sophie", "Thomas", "Léa", "Alexandre"),
                todoCount = 4,
                completedTodoCount = 1,
                color = PINK_COLOR
            ),
            Party(
                id = GAME_NIGHT_ID,
                title = "Soirée Jeux",
                date = LocalDateTime.now().plusDays(5),
                location = "8 Avenue Victor Hugo, Lyon",
                description = "Soirée jeux de société chez moi. J'ai prévu quelques jeux, " +
                        "mais n'hésitez pas à apporter les vôtres. Pizza et bières au menu!",
                participants = listOf("Julien", "Emma", "Nicolas", "Clara"),
                todoCount = 3,
                completedTodoCount = 2,
                color = BLUE_COLOR
            ),
            Party(
                id = SUMMER_BBQ_ID,
                title = "Barbecue d'été",
                date = LocalDateTime.now().plusDays(30),
                location = "Parc de la Tête d'Or, Lyon",
                description = "Grand barbecue au parc pour célébrer l'arrivée de l'été. " +
                        "Je m'occupe du BBQ, apportez vos spécialités et boissons!",
                participants = listOf("Paul", "Marie", "Antoine", "Sarah", "Lucie", "Maxime"),
                todoCount = 5,
                completedTodoCount = 0,
                color = GREEN_COLOR
            ),
            Party(
                id = NEW_YEAR_PARTY_ID,
                title = "Réveillon du Nouvel An",
                date = LocalDateTime.of(LocalDateTime.now().year + 1, 1, 1, 0, 0),
                location = "25 Rue de la République, Bordeaux",
                description = "Grande fête pour célébrer la nouvelle année! Tenue élégante exigée.",
                participants = listOf("Charlotte", "Hugo", "Camille", "Antoine"),
                todoCount = 6,
                completedTodoCount = 1,
                color = PURPLE_COLOR
            ),
            Party(
                id = HOUSEWARMING_ID,
                title = "Pendaison de crémaillère",
                date = LocalDateTime.now().plusDays(10),
                location = "42 Rue des Fleurs, Lille",
                description = "J'emménage dans mon nouvel appartement et vous invite à le découvrir autour d'un verre. " +
                        "Les petits plats maison sont les bienvenus!",
                participants = listOf("Mathilde", "Lucas", "Alice", "Nathan"),
                todoCount = 4,
                completedTodoCount = 3,
                color = ORANGE_COLOR
            )
        )
    }
    
    /**
     * Génère une liste de Todo fictives
     */
    fun getTodos(): List<Todo> {
        return listOf(
            // Todos pour l'anniversaire de Julie
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Acheter un gâteau",
                isCompleted = true,
                assignedTo = "Marc",
                partyId = BIRTHDAY_PARTY_ID,
                partyColor = PINK_COLOR,
                isPriority = true
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Préparer la décoration",
                isCompleted = false,
                assignedTo = "Sophie",
                partyId = BIRTHDAY_PARTY_ID,
                partyColor = PINK_COLOR,
                isPriority = false
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Envoyer les invitations",
                isCompleted = false,
                assignedTo = "Thomas",
                partyId = BIRTHDAY_PARTY_ID,
                partyColor = PINK_COLOR,
                isPriority = true
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Organiser la playlist",
                isCompleted = false,
                assignedTo = "Léa",
                partyId = BIRTHDAY_PARTY_ID,
                partyColor = PINK_COLOR,
                isPriority = false
            ),
            
            // Todos pour la soirée jeux
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Acheter des snacks",
                isCompleted = true,
                assignedTo = "Julien",
                partyId = GAME_NIGHT_ID,
                partyColor = BLUE_COLOR,
                isPriority = false
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Sélectionner les jeux",
                isCompleted = true,
                assignedTo = "Emma",
                partyId = GAME_NIGHT_ID,
                partyColor = BLUE_COLOR,
                isPriority = true
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Commander des pizzas",
                isCompleted = false,
                assignedTo = "Nicolas",
                partyId = GAME_NIGHT_ID,
                partyColor = BLUE_COLOR,
                isPriority = true
            ),
            
            // Todos pour le barbecue d'été
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Réserver l'espace au parc",
                isCompleted = false,
                assignedTo = "Paul",
                partyId = SUMMER_BBQ_ID,
                partyColor = GREEN_COLOR,
                isPriority = true
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Acheter le charbon",
                isCompleted = false,
                assignedTo = "Marie",
                partyId = SUMMER_BBQ_ID,
                partyColor = GREEN_COLOR,
                isPriority = true
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Préparer la marinade",
                isCompleted = false,
                assignedTo = "Antoine",
                partyId = SUMMER_BBQ_ID,
                partyColor = GREEN_COLOR,
                isPriority = false
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Apporter les couverts",
                isCompleted = false,
                assignedTo = "Sarah",
                partyId = SUMMER_BBQ_ID,
                partyColor = GREEN_COLOR,
                isPriority = false
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Préparer une salade",
                isCompleted = false,
                assignedTo = "Lucie",
                partyId = SUMMER_BBQ_ID,
                partyColor = GREEN_COLOR,
                isPriority = false
            ),
            
            // Todos pour le réveillon du nouvel an
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Acheter des cotillons",
                isCompleted = true,
                assignedTo = "Charlotte",
                partyId = NEW_YEAR_PARTY_ID,
                partyColor = PURPLE_COLOR,
                isPriority = false
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Préparer le champagne",
                isCompleted = false,
                assignedTo = "Hugo",
                partyId = NEW_YEAR_PARTY_ID,
                partyColor = PURPLE_COLOR,
                isPriority = true
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Choisir la playlist de minuit",
                isCompleted = false,
                assignedTo = "Camille",
                partyId = NEW_YEAR_PARTY_ID,
                partyColor = PURPLE_COLOR,
                isPriority = false
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Décorer la salle",
                isCompleted = false,
                assignedTo = "Antoine",
                partyId = NEW_YEAR_PARTY_ID,
                partyColor = PURPLE_COLOR,
                isPriority = true
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Organiser des jeux",
                isCompleted = false,
                assignedTo = "Charlotte",
                partyId = NEW_YEAR_PARTY_ID,
                partyColor = PURPLE_COLOR,
                isPriority = false
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Inviter les voisins",
                isCompleted = false,
                assignedTo = "Hugo",
                partyId = NEW_YEAR_PARTY_ID,
                partyColor = PURPLE_COLOR,
                isPriority = false
            ),
            
            // Todos pour la pendaison de crémaillère
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Ranger l'appartement",
                isCompleted = true,
                assignedTo = "Mathilde",
                partyId = HOUSEWARMING_ID,
                partyColor = ORANGE_COLOR,
                isPriority = true
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Acheter des boissons",
                isCompleted = true,
                assignedTo = "Lucas",
                partyId = HOUSEWARMING_ID,
                partyColor = ORANGE_COLOR,
                isPriority = true
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Préparer des tapas",
                isCompleted = true,
                assignedTo = "Alice",
                partyId = HOUSEWARMING_ID,
                partyColor = ORANGE_COLOR,
                isPriority = false
            ),
            Todo(
                id = UUID.randomUUID().toString(),
                title = "Faire un plan pour les invités",
                isCompleted = false,
                assignedTo = "Nathan",
                partyId = HOUSEWARMING_ID,
                partyColor = ORANGE_COLOR,
                isPriority = false
            )
        )
    }
    
    /**
     * Génère une liste de ToBuy fictives
     */
    fun getToBuys(): List<ToBuy> {
        return listOf(
            // ToBuys pour l'anniversaire de Julie
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Gâteau au chocolat",
                quantity = 1,
                estimatedPrice = 35.0f,
                isPurchased = true,
                assignedTo = "Marc",
                partyId = BIRTHDAY_PARTY_ID,
                partyColor = PINK_COLOR,
                isPriority = true
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Bougies anniversaire",
                quantity = 30,
                estimatedPrice = 5.0f,
                isPurchased = false,
                assignedTo = "Sophie",
                partyId = BIRTHDAY_PARTY_ID,
                partyColor = PINK_COLOR,
                isPriority = true
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Ballons colorés",
                quantity = 20,
                estimatedPrice = 10.0f,
                isPurchased = false,
                assignedTo = "Thomas",
                partyId = BIRTHDAY_PARTY_ID,
                partyColor = PINK_COLOR,
                isPriority = false
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Bouteille de champagne",
                quantity = 2,
                estimatedPrice = 45.0f,
                isPurchased = false,
                assignedTo = "Léa",
                partyId = BIRTHDAY_PARTY_ID,
                partyColor = PINK_COLOR,
                isPriority = true
            ),
            
            // ToBuys pour la soirée jeux
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Chips variées",
                quantity = 5,
                estimatedPrice = 15.0f,
                isPurchased = true,
                assignedTo = "Julien",
                partyId = GAME_NIGHT_ID,
                partyColor = BLUE_COLOR,
                isPriority = false
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Packs de bière",
                quantity = 3,
                estimatedPrice = 24.0f,
                isPurchased = true,
                assignedTo = "Emma",
                partyId = GAME_NIGHT_ID,
                partyColor = BLUE_COLOR,
                isPriority = true
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Pizzas assorties",
                quantity = 4,
                estimatedPrice = 40.0f,
                isPurchased = false,
                assignedTo = "Nicolas",
                partyId = GAME_NIGHT_ID,
                partyColor = BLUE_COLOR,
                isPriority = true
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Jeu de cartes",
                quantity = 1,
                estimatedPrice = 8.0f,
                isPurchased = false,
                assignedTo = "Clara",
                partyId = GAME_NIGHT_ID,
                partyColor = BLUE_COLOR,
                isPriority = false
            ),
            
            // ToBuys pour le barbecue d'été
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Sacs de charbon",
                quantity = 2,
                estimatedPrice = 12.0f,
                isPurchased = false,
                assignedTo = "Paul",
                partyId = SUMMER_BBQ_ID,
                partyColor = GREEN_COLOR,
                isPriority = true
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Viandes marinées",
                quantity = 3,
                estimatedPrice = 45.0f,
                isPurchased = false,
                assignedTo = "Marie",
                partyId = SUMMER_BBQ_ID,
                partyColor = GREEN_COLOR,
                isPriority = true
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Pains à hamburger",
                quantity = 12,
                estimatedPrice = 8.0f,
                isPurchased = false,
                assignedTo = "Antoine",
                partyId = SUMMER_BBQ_ID,
                partyColor = GREEN_COLOR,
                isPriority = false
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Assiettes jetables",
                quantity = 30,
                estimatedPrice = 5.0f,
                isPurchased = false,
                assignedTo = "Sarah",
                partyId = SUMMER_BBQ_ID,
                partyColor = GREEN_COLOR,
                isPriority = false
            ),
            
            // ToBuys pour le réveillon du nouvel an
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Bouteilles de champagne",
                quantity = 6,
                estimatedPrice = 120.0f,
                isPurchased = false,
                assignedTo = "Charlotte",
                partyId = NEW_YEAR_PARTY_ID,
                partyColor = PURPLE_COLOR,
                isPriority = true
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Cotillons et chapeaux",
                quantity = 20,
                estimatedPrice = 25.0f,
                isPurchased = true,
                assignedTo = "Hugo",
                partyId = NEW_YEAR_PARTY_ID,
                partyColor = PURPLE_COLOR,
                isPriority = false
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Petits fours assortis",
                quantity = 5,
                estimatedPrice = 60.0f,
                isPurchased = false,
                assignedTo = "Camille",
                partyId = NEW_YEAR_PARTY_ID,
                partyColor = PURPLE_COLOR,
                isPriority = true
            ),
            
            // ToBuys pour la pendaison de crémaillère
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Vins variés",
                quantity = 5,
                estimatedPrice = 75.0f,
                isPurchased = true,
                assignedTo = "Mathilde",
                partyId = HOUSEWARMING_ID,
                partyColor = ORANGE_COLOR,
                isPriority = true
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Bières artisanales",
                quantity = 12,
                estimatedPrice = 36.0f,
                isPurchased = true,
                assignedTo = "Lucas",
                partyId = HOUSEWARMING_ID,
                partyColor = ORANGE_COLOR,
                isPriority = false
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Ingrédients pour tapas",
                quantity = 1,
                estimatedPrice = 40.0f,
                isPurchased = true,
                assignedTo = "Alice",
                partyId = HOUSEWARMING_ID,
                partyColor = ORANGE_COLOR,
                isPriority = false
            ),
            ToBuy(
                id = UUID.randomUUID().toString(),
                title = "Bougies parfumées",
                quantity = 3,
                estimatedPrice = 18.0f,
                isPurchased = false,
                assignedTo = "Nathan",
                partyId = HOUSEWARMING_ID,
                partyColor = ORANGE_COLOR,
                isPriority = false
            )
        )
    }
} 