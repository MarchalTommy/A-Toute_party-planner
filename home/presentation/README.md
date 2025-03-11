# Module Home - Présentation

Ce module contient l'écran d'accueil de l'application A-Toute.

## Architecture

L'écran d'accueil est structuré selon le pattern MVVM :
- **Model** : Des classes de données pour représenter les Party et Todo
- **ViewModel** : HomeViewModel qui gère l'état de l'UI et les opérations
- **View** : Composables Jetpack Compose pour afficher l'interface utilisateur

## Composants principaux

- **HomeScreen** : Écran principal avec la barre d'application et le contenu
- **PartyCard** : Tuile affichant les détails d'une Party avec countdown
- **TodoItem** : Élément affichant une tâche à faire avec l'accent de couleur de sa Party associée

## Fonctionnalités

L'écran d'accueil affiche :
- Une liste des prochaines Party avec un compte à rebours en jours
- Une liste de tâches (Todo) provenant de différentes Party
- Les Todo sont différenciables par leur accent de couleur
- État vide pour chaque section quand aucune donnée n'est disponible

## Navigation

L'écran expose des callbacks pour :
- Cliquer sur une Party (navigation vers les détails)
- Cliquer sur un Todo (navigation vers les détails)
- Ajouter une nouvelle Party