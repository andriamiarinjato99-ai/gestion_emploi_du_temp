# Gestion Emploi du Temps — Application JavaFX

Application desktop de gestion d'emploi du temps universitaire (enseignants, matières,
classes, salles, séances), développée en **JavaFX 23 + FXML + CSS**, architecture **MVC**,
persistance **MySQL (XAMPP)**, compatible **JDK 25**.

## 1. Prérequis

- **JDK 25** installé 
- **Maven 3.9+**.
- **XAMPP** avec le module **MySQL** démarré.

## 2. Créer la base de données

1. Démarrer XAMPP et lancer **Apache** + **MySQL**.
2. Ouvrir **phpMyAdmin** (`http://localhost/phpmyadmin`).
3. Importer le fichier `database/create_db.sql` (onglet *Importer*),
   ou en ligne de commande :
   ```bash
   mysql -u root -p < database/create_db.sql
   ```
   Cela crée la base `gestion_edt`, ses 5 tables, et quelques données de démonstration.

> **Si la base `gestion_edt` existe déjà** (créée avec une version précédente),
> n'importez pas `create_db.sql` : il ne modifierait pas les tables existantes.
> Importez plutôt `database/migration_matiere_enseignant.sql`, qui ajoute la
> colonne `enseignant.matiere_id`, la pré-remplit à partir des séances déjà
> saisies et nettoie les anciens numéros de téléphone.
> L'autre solution est de supprimer la base puis de réimporter `create_db.sql`.


## 3. Lancer l'application

```bash
mvn clean javafx:run
```

## 4. Construire un exécutable (jar autonome)

```bash
mvn clean package
java -jar target/gestion-edt.jar
```

## 5. Architecture du projet (MVC)

```
com.edt
 ├── app/          → point d'entrée (MainApp)
 ├── model/        → entités JavaFX (Enseignant, Matiere, Classe, Salle, Seance, Jour)
 ├── dao/           → accès aux données JDBC (CRUD + requêtes SQL)
 ├── service/      → règles métier et validations (couche entre DAO et contrôleurs)
 ├── controller/   → contrôleurs JavaFX liés aux vues FXML
 └── util/          → validation des champs, boîtes de dialogue

database/
 ├── create_db.sql                     → création complète de la base
 └── migration_matiere_enseignant.sql  → mise à jour d'une base existante

resources/com/edt/
 ├── view/         → fichiers FXML (une vue par module + fenêtre principale)
 └── css/          → feuille de style style.css
```

## 6. Fonctionnalités

- CRUD complet (ajout, modification, suppression, recherche en temps réel) sur
  Enseignants, Matières, Classes, Salles et Séances.
- Formulaires avec validation des champs obligatoires, des formats (email,
  téléphone, heures `HH:mm`, nombres) et messages d'erreur explicites.
- **Téléphone sur exactement 10 chiffres** : le champ n'accepte que des chiffres
  et bloque la saisie au 10ᵉ ; un compteur « n/10 chiffres » guide l'utilisateur
  et, à la validation, le message affiché est
  *« Le numéro de téléphone doit être 10 chiffres exactement. »*
- **Correction automatique des heures** dans l'écran Séances : saisir `8` donne
  `08:00`, `8:30` donne `08:30` (formats acceptés : `8`, `08`, `830`, `0830`,
  `8h30`, `8.30`, `8:30`…). La correction s'applique à la sortie du champ, à la
  touche Entrée et au moment d'enregistrer.
- **Remplissage automatique de la matière** : sélectionner un enseignant dans
  l'écran Séances renseigne aussitôt la matière qu'il enseigne. La matière vient
  de sa fiche (champ « Matière enseignée »), ou à défaut de la matière qu'il
  assure le plus souvent dans les séances déjà saisies. Elle reste modifiable.
- **Détection automatique de conflits d'emploi du temps** : impossible de
  programmer deux séances qui se chevauchent pour le même enseignant, la même
  classe ou la même salle, le même jour.
- **Tableau de bord** avec compteurs et graphique (nombre de séances par jour
  de la semaine).
- Interface moderne, construite en JavaFX/FXML/CSS avec architecture MVC
  propre (Model / Service / Controller / DAO).
