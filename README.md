# Application de Dessin JavaFX avec Design Patterns

## Description

Cette application JavaFX permet de sélectionner et dessiner des formes géométriques (rectangle, cercle, ligne, ellipse, triangle) sur une zone de dessin. L'architecture s'appuie sur plusieurs design patterns pour assurer une meilleure modularité, extensibilité et maintenance du code.

## Fonctionnalités

### Fonctionnalités principales
- ✅ Sélection de formes géométriques depuis une palette
- ✅ Dessin de formes dans la zone de dessin
- ✅ Sauvegarde et chargement de dessins en base de données
- ✅ Journalisation des actions utilisateur avec 3 stratégies :
  - Console
  - Fichier texte
  - Base de données
- ✅ Système d'annulation/rétablissement (Undo/Redo)
- ✅ Suppression de formes
- ✅ Personnalisation des propriétés (couleur, épaisseur, remplissage)

### Fonctionnalités avancées
- Interface utilisateur intuitive avec palette d'outils
- Gestion des événements de souris
- Affichage des coordonnées en temps réel
- Barre de statut informative

## Design Patterns Implémentés

### 1. Factory Pattern (`ShapeFactory`)
- **Objectif** : Créer des instances de formes géométriques
- **Localisation** : `com.drawing.factory.ShapeFactory`
- **Avantages** : Centralise la création d'objets, facilite l'ajout de nouvelles formes

### 2. Strategy Pattern (`LoggingStrategy`)
- **Objectif** : Changer dynamiquement la stratégie de journalisation
- **Localisation** : `com.drawing.strategy.*`
- **Implémentations** :
  - `ConsoleLoggingStrategy`
  - `FileLoggingStrategy`
  - `DatabaseLoggingStrategy`

### 3. Observer Pattern (`DrawingObserver`)
- **Objectif** : Notifier les changements dans le dessin
- **Localisation** : `com.drawing.observer.DrawingObserver`
- **Usage** : Synchronisation entre modèle et vue

### 4. Command Pattern (`Command`)
- **Objectif** : Encapsuler les actions pour supporter undo/redo
- **Localisation** : `com.drawing.command.*`
- **Implémentations** :
  - `AddShapeCommand`
  - `RemoveShapeCommand`
  - `CommandManager`

### 5. Singleton Pattern (`Logger`)
- **Objectif** : Instance unique du gestionnaire de logs
- **Localisation** : `com.drawing.utils.Logger`

### 6. Template Method Pattern (`Shape`)
- **Objectif** : Définir la structure commune de rendu des formes
- **Localisation** : `com.drawing.model.shapes.Shape`

### 7. MVC Pattern
- **Model** : `com.drawing.model.*`
- **View** : `com.drawing.view.*`
- **Controller** : `com.drawing.controller.*`

## Architecture du Projet

```
src/
├── main/
│   ├── java/
│   │   └── com/drawing/
│   │       ├── DrawingApplication.java          # Classe principale
│   │       ├── model/                           # Modèles de données
│   │       │   ├── shapes/                      # Classes des formes
│   │       │   │   ├── Shape.java              # Classe abstraite
│   │       │   │   ├── Rectangle.java
│   │       │   │   ├── Circle.java
│   │       │   │   ├── Line.java
│   │       │   │   ├── Ellipse.java
│   │       │   │   └── Triangle.java
│   │       │   └── drawing/
│   │       │       └── Drawing.java            # Modèle du dessin
│   │       ├── view/                           # Interface utilisateur
│   │       │   └── MainViewController.java
│   │       ├── controller/                     # Contrôleurs
│   │       │   └── DrawingController.java
│   │       ├── factory/                        # Factory Pattern
│   │       │   └── ShapeFactory.java
│   │       ├── strategy/                       # Strategy Pattern
│   │       │   ├── LoggingStrategy.java
│   │       │   ├── ConsoleLoggingStrategy.java
│   │       │   ├── FileLoggingStrategy.java
│   │       │   └── DatabaseLoggingStrategy.java
│   │       ├── observer/                       # Observer Pattern
│   │       │   └── DrawingObserver.java
│   │       ├── command/                        # Command Pattern
│   │       │   ├── Command.java
│   │       │   ├── AddShapeCommand.java
│   │       │   ├── RemoveShapeCommand.java
│   │       │   └── CommandManager.java
│   │       ├── database/                       # Gestion BDD
│   │       │   └── DatabaseManager.java
│   │       └── utils/                          # Utilitaires
│   │           └── Logger.java
│   └── resources/
│       └── fxml/
│           └── main.fxml                       # Interface FXML
└── test/
    └── java/                                   # Tests unitaires
        └── com/drawing/
            ├── factory/
            │   └── ShapeFactoryTest.java
            ├── model/drawing/
            │   └── DrawingTest.java
            └── command/
                └── CommandManagerTest.java
```

## Technologies Utilisées

- **Java 17** : Langage de programmation
- **JavaFX 21.0.2** : Framework d'interface utilisateur
- **H2 Database 2.2.224** : Base de données embarquée
- **Jackson 2.16.1** : Sérialisation JSON
- **JUnit 5.10.1** : Tests unitaires
- **SLF4J 2.0.11** : API de logging
- **Logback 1.4.14** : Implémentation de logging
- **JaCoCo 0.8.11** : Couverture de code
- **Maven** : Gestion des dépendances

## Installation et Exécution

### Prérequis
- Java 17 ou supérieur
- Maven 3.6 ou supérieur

### Compilation
```bash
mvn clean compile
```

### Exécution
```bash
mvn javafx:run
```

### Tests et Couverture
```bash
# Exécuter les tests
mvn test

# Générer le rapport de couverture
mvn verify
```
Le rapport de couverture sera disponible dans `target/site/jacoco/index.html`

### Package
```bash
mvn clean package
```

## Utilisation

### Interface Utilisateur

1. **Palette d'outils** (à gauche) :
   - Sélection de formes (Rectangle, Cercle, Ligne, Ellipse, Triangle)
   - Propriétés (Couleur, Épaisseur, Remplissage)
   - Actions (Supprimer)

2. **Zone de dessin** (centre) :
   - Cliquez pour dessiner une forme
   - Les formes apparaissent à la position du clic

3. **Barre de menu** :
   - **Fichier** : Nouveau, Sauvegarder, Charger, Quitter
   - **Édition** : Annuler, Rétablir, Effacer tout
   - **Outils** : Changer la stratégie de journalisation

4. **Barre de statut** (bas) :
   - Messages d'information
   - Coordonnées de la souris

### Raccourcis et Actions

- **Dessiner** : Cliquer sur la zone de dessin
- **Supprimer** : Cliquer sur "Supprimer" puis sur une forme
- **Annuler** : Menu Édition > Annuler
- **Rétablir** : Menu Édition > Rétablir

### Journalisation

L'application utilise SLF4J avec Logback pour la journalisation. Trois modes sont disponibles :

1. **Console** : Affichage dans la console avec formatage coloré
2. **Fichier** : Écriture dans `logs/drawing-app.log` avec rotation quotidienne
3. **Base de données** : Stockage dans une table `logs`

Configuration du logging dans `src/main/resources/logback.xml` :
- Niveau de log par défaut : INFO
- Niveau de log pour l'application : DEBUG
- Rotation des fichiers de log : 30 jours
- Format : `[timestamp] [thread] [level] [logger] - message`

## Base de Données

L'application utilise H2 Database avec deux bases :

1. **drawings.mv.db** : Stockage des dessins
   - Table `drawings` : Métadonnées des dessins
   - Table `shapes` : Formes sérialisées en JSON

2. **logs.mv.db** : Journalisation en base
   - Table `logs` : Historique des actions

## Tests

Le projet inclut des tests unitaires pour :

- `ShapeFactoryTest` : Tests du Factory Pattern
- `DrawingTest` : Tests du modèle de dessin et Observer Pattern
- `CommandManagerTest` : Tests du Command Pattern

Exécution des tests :
```bash
mvn test
```

## Extensibilité

### Ajouter une nouvelle forme

1. Créer une classe héritant de `Shape`
2. Implémenter les méthodes abstraites
3. Ajouter le type dans `ShapeFactory.ShapeType`
4. Mettre à jour `ShapeFactory.createShape()`
5. Ajouter un bouton dans l'interface FXML

### Ajouter une nouvelle stratégie de logging

1. Implémenter `LoggingStrategy`
2. Ajouter une méthode dans `Logger`
3. Ajouter un élément de menu dans l'interface

## Auteur

Projet réalisé dans le cadre d'un exercice sur les Design Patterns en Java.
# Drawing-App
