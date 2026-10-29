SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS efreiforge CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE efreiforge;

DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS collaborateur;
DROP TABLE IF EXISTS adresse;

CREATE TABLE adresse (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    rue         VARCHAR(100) NOT NULL,
    code_postal VARCHAR(10)  NOT NULL,
    ville       VARCHAR(60)  NOT NULL,
    pays        VARCHAR(60)  NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE collaborateur (
    identifiant     VARCHAR(10) NOT NULL,
    metier          VARCHAR(40) NOT NULL,   -- discriminant : 'Programmeur' ou 'Testeur'
    prenom          VARCHAR(50) NOT NULL,
    nom             VARCHAR(50) NOT NULL,
    salaire         DOUBLE      NOT NULL,
    langage_prefere VARCHAR(40) NULL,       -- NULL pour un Testeur
    adresse_id      BIGINT      NOT NULL,
    PRIMARY KEY (identifiant),
    CONSTRAINT fk_collaborateur_adresse FOREIGN KEY (adresse_id) REFERENCES adresse (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ticket (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    titre       VARCHAR(100) NOT NULL,
    priorite    VARCHAR(20)  NOT NULL,      -- BASSE, NORMALE, HAUTE
    statut      VARCHAR(20)  NOT NULL,      -- A_FAIRE, EN_COURS, A_VALIDER, TERMINE
    assigne_id  VARCHAR(10)  NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_ticket_collaborateur FOREIGN KEY (assigne_id) REFERENCES collaborateur (identifiant)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------- Jeu de données ----------------

INSERT INTO adresse (id, rue, code_postal, ville, pays) VALUES
    (1, '12 rue de la Paix',        '75002', 'Paris',     'France'),
    (2, '5 avenue Victor Hugo',     '69003', 'Lyon',      'France'),
    (3, '8 boulevard Carnot',       '59000', 'Lille',     'France'),
    (4, '21 rue Sainte-Catherine',  '33000', 'Bordeaux',  'France'),
    (5, '3 place du Capitole',      '31000', 'Toulouse',  'France'),
    (6, '17 quai des Chartrons',    '33300', 'Bordeaux',  'France');

INSERT INTO collaborateur (identifiant, metier, prenom, nom, salaire, langage_prefere, adresse_id) VALUES
    ('C001', 'Programmeur', 'Alice', 'Martin',  42000, 'Java',   1),
    ('C002', 'Programmeur', 'Alex',  'Dupont',  44500, 'Python', 2),
    ('C003', 'Testeur',     'Chloé', 'Bernard', 39000, NULL,     3),
    ('C004', 'Programmeur', 'Nora',  'Diallo',  47000, 'Java',   4),
    ('C005', 'Testeur',     'Hugo',  'Petit',   41000, NULL,     5),
    ('C006', 'Programmeur', 'Lina',  'Moreau',  45500, 'Kotlin', 6);

-- 8 tickets : les 4 statuts, 2 non assignés (#1 et #2), au plus 3 « En cours » par programmeur
INSERT INTO ticket (id, titre, priorite, statut, assigne_id) VALUES
    (1, 'Corriger l''export PDF',            'HAUTE',   'A_FAIRE',   NULL),
    (2, 'Ajouter la recherche par nom',      'NORMALE', 'A_FAIRE',   NULL),
    (3, 'Mettre à jour la documentation',    'BASSE',   'A_FAIRE',   'C004'),
    (4, 'Créer l''écran de connexion',       'HAUTE',   'EN_COURS',  'C001'),
    (5, 'Optimiser les requêtes de listing', 'NORMALE', 'EN_COURS',  'C002'),
    (6, 'Écrire les tests d''intégration',   'NORMALE', 'A_VALIDER', 'C001'),
    (7, 'Migrer vers Java 21',               'BASSE',   'TERMINE',   'C004'),
    (8, 'Corriger la pagination',            'HAUTE',   'EN_COURS',  'C006');

-- Vérifications
SELECT statut, COUNT(*) AS nb FROM ticket GROUP BY statut;
SELECT * FROM ticket WHERE assigne_id IS NULL;
