-- =========================================================
-- Programmation en Java — TP4 / Préparation de la base
-- Cible : MariaDB ou MySQL (outil préconisé : XAMPP + phpMyAdmin)
--
-- Ce script :
--   1. crée la base « efrei_tp4 » si elle n'existe pas ;
--   2. crée et remplit la table de DÉMONSTRATION « collaborateur_demo »,
--      utilisée à la mission 10 (génération d'une entité par IntelliJ).
--
-- Il ne crée PAS les tables de votre application (« collaborateur »,
-- « adresse ») : c'est Hibernate qui s'en charge (hbm2ddl.auto=update).
-- Il ne crée volontairement ni relation ni clé étrangère.
--
-- Utilisation avec XAMPP : phpMyAdmin → onglet « Importer » → choisir ce fichier.
-- Le script peut être rejoué sans risque : seule « collaborateur_demo » est recréée.
-- =========================================================

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS efrei_tp4
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE efrei_tp4;

DROP TABLE IF EXISTS collaborateur_demo;

CREATE TABLE collaborateur_demo (
    id_collaborateur  VARCHAR(10) NOT NULL,   -- identifiant métier (C001...)
    prenom            VARCHAR(50) NOT NULL,
    nom               VARCHAR(50) NOT NULL,
    metier            VARCHAR(40) NOT NULL,   -- 'Programmeur' ou 'Testeur'
    salaire           DOUBLE      NOT NULL,
    langage_prefere   VARCHAR(40) NULL,       -- NULL pour un Testeur

    PRIMARY KEY (id_collaborateur)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO collaborateur_demo
    (id_collaborateur, prenom, nom, metier, salaire, langage_prefere)
VALUES
    ('C001', 'Alice', 'Martin',  'Programmeur', 42000, 'Java'),
    ('C002', 'Alex',  'Dupont',  'Programmeur', 44500, 'Python'),
    ('C003', 'Chloé', 'Bernard', 'Testeur',     39000, NULL),
    ('C004', 'Nora',  'Diallo',  'Programmeur', 47000, 'Java'),
    ('C005', 'Hugo',  'Petit',   'Testeur',     41000, NULL),
    ('C006', 'Lina',  'Moreau',  'Programmeur', 45500, 'Kotlin');

-- ---------------------------------------------------------
-- Vérifications (à exécuter dans l'onglet « SQL » de phpMyAdmin)
-- ---------------------------------------------------------

-- Les 6 lignes attendues
SELECT * FROM collaborateur_demo ORDER BY id_collaborateur;

-- Équivalent SQL de la requête JPQL de la mission 8 : attendu Nora et Lina
SELECT * FROM collaborateur_demo WHERE salaire > 45000;

-- Recherche sur le nom, sans tenir compte de la casse : attendu Alice Martin
SELECT * FROM collaborateur_demo WHERE LOWER(nom) LIKE '%mar%';
