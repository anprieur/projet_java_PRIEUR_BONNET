-- Schéma de RÉFÉRENCE (correction) : équivalent à ce que Hibernate génère
-- avec hibernate.hbm2ddl.auto=update. Cible : MariaDB / MySQL (XAMPP).
CREATE DATABASE IF NOT EXISTS efrei_tp4 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE efrei_tp4;

CREATE TABLE IF NOT EXISTS adresse (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    rue         VARCHAR(100) NOT NULL,
    code_postal VARCHAR(10)  NOT NULL,
    ville       VARCHAR(60)  NOT NULL,
    pays        VARCHAR(60)  NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS collaborateur (
    identifiant     VARCHAR(10) NOT NULL,
    metier          VARCHAR(40) NOT NULL,   -- discriminant : 'Programmeur' ou 'Testeur'
    prenom          VARCHAR(50) NOT NULL,
    nom             VARCHAR(50) NOT NULL,
    salaire         DOUBLE      NOT NULL,
    langage_prefere VARCHAR(40) NULL,       -- NULL pour un Testeur
    adresse_id      BIGINT      NOT NULL,
    PRIMARY KEY (identifiant),
    CONSTRAINT fk_collaborateur_adresse FOREIGN KEY (adresse_id) REFERENCES adresse (id)
);
