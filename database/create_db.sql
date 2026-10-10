-- ============================================================
-- Base de données : gestion_edt
-- A importer via phpMyAdmin (XAMPP) ou : mysql -u root -p < create_db.sql
--
-- NOTE : la table "matiere" est créée AVANT "enseignant" car un enseignant
-- référence désormais la matière qu'il enseigne (clé étrangère matiere_id).
-- ============================================================

CREATE DATABASE IF NOT EXISTS gestion_edt CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestion_edt;

-- ---------------------------------------------------------
-- Table : matiere
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS matiere (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    nom VARCHAR(100) NOT NULL,
    volume_horaire INT NOT NULL DEFAULT 0
);

-- ---------------------------------------------------------
-- Table : enseignant
-- telephone : exactement 10 chiffres (contrôlé par l'application)
-- matiere_id : matière principale enseignée, utilisée pour le
--              remplissage automatique dans l'écran Séances
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS enseignant (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(60) NOT NULL,
    prenom VARCHAR(60) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    telephone VARCHAR(10) NOT NULL,
    specialite VARCHAR(80),
    matiere_id INT NULL,
    CONSTRAINT fk_enseignant_matiere FOREIGN KEY (matiere_id)
        REFERENCES matiere(id) ON DELETE SET NULL
);

-- ---------------------------------------------------------
-- Table : classe
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS classe (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(40) NOT NULL UNIQUE,
    niveau VARCHAR(40),
    effectif INT NOT NULL DEFAULT 0
);

-- ---------------------------------------------------------
-- Table : salle
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS salle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(40) NOT NULL UNIQUE,
    capacite INT NOT NULL DEFAULT 0,
    type VARCHAR(40)
);

-- ---------------------------------------------------------
-- Table : seance (le coeur de l'emploi du temps)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS seance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    matiere_id INT NOT NULL,
    enseignant_id INT NOT NULL,
    classe_id INT NOT NULL,
    salle_id INT NOT NULL,
    jour VARCHAR(10) NOT NULL,       -- LUNDI, MARDI, MERCREDI, JEUDI, VENDREDI, SAMEDI
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    CONSTRAINT fk_seance_matiere FOREIGN KEY (matiere_id) REFERENCES matiere(id) ON DELETE CASCADE,
    CONSTRAINT fk_seance_enseignant FOREIGN KEY (enseignant_id) REFERENCES enseignant(id) ON DELETE CASCADE,
    CONSTRAINT fk_seance_classe FOREIGN KEY (classe_id) REFERENCES classe(id) ON DELETE CASCADE,
    CONSTRAINT fk_seance_salle FOREIGN KEY (salle_id) REFERENCES salle(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Données de démonstration (facultatif)
-- ---------------------------------------------------------
INSERT INTO matiere (code, nom, volume_horaire) VALUES
('Java', 'Programmation Java', 40),
('Python', 'Algèbre', 30),
('PHP', 'PHP', 30),
('Web', 'Tech Web', 30);

-- Les numéros de téléphone comptent bien 10 chiffres.
INSERT INTO enseignant (nom, prenom, email, telephone, specialite, matiere_id) VALUES
('Rakoto', 'Jean', 'jean.rakoto@edt.mg', '0341234567', 'Informatique', 1),
('Rasoa', 'Marie', 'marie.rasoa@edt.mg', '0347654321', 'Mathématiques', 2),
('Ndrema', '', 'ndrema@gmail.com', '0347654321', 'Frontend', 4);

INSERT INTO classe (nom, niveau, effectif) VALUES
('L1-INFO', 'Licence 1', 35),
('L2-INFO', 'Licence 2', 32),
('L3-INFO', 'Licence 3', 32);

INSERT INTO salle (nom, capacite, type) VALUES
('Salle A1', 32, ''),
('Salle A2', 32, ''),
('Salle A3', 30, ''),
('Salle A4', 40, 'Pratique');

INSERT INTO seance (matiere_id, enseignant_id, classe_id, salle_id, jour, heure_debut, heure_fin) VALUES
(1, 1, 1, 2, 'LUNDI', '08:00:00', '10:00:00'),
(2, 2, 2, 1, 'MARDI', '10:00:00', '12:00:00');
