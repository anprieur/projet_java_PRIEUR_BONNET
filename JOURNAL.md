# JOURNAL — EFREIForge (Jalon 1)

Antoine Prieur & Ivane Bonnet

## 1. Démarche

- **Point de départ** : application du TP4 (hiérarchie `Collaborateur` / `Programmeur` / `Testeur`,
  entité `Adresse`, `CollaborateurService`, `ConsoleApp`, exceptions métier, JPA/Hibernate).
- **Réutilisé tel quel** : entités collaborateurs (SINGLE_TABLE), `Adresse`, `JpaUtil`, exceptions,
  menu console 1 à 6.
- **Ajouté pour le jalon 1** : `Ticket` (mapping fourni, complété), `TicketService`
  (créer, lister, lister par statut, assigner, tickets d'un collaborateur), entrées 7 à 10 du menu,
  `TicketIntrouvableException`, `AssignationRefuseeException`, script `sql/efreiforge.sql`.
- **Petits changements sur le TP4** :
    - la gestion `begin/commit/rollback` a été déplacée dans `Transactions`, partagée par les deux services ;
    - `Collaborateur` gagne `peutEtreResponsableDeTickets()` (vrai pour Programmeur, faux pour Testeur) :
      la règle dépend du métier, elle est donc portée par la hiérarchie et non par un `instanceof` ;
    - la méthode `demarerTicket()` du squelette (qui levait `Exception`) a été retirée : elle relève du jalon final.
- **Règles déjà appliquées au jalon 1** : titre obligatoire, ticket créé « À faire » sans responsable,
  assignation réservée aux programmeurs, assignation possible seulement si « À faire »,
  collaborateur ou ticket inexistant géré, refus sans modification des données (rollback).
- **Organisation du binôme** : [à compléter].

## 2. Autocontrôle du jalon 1

> À remplir après avoir réellement exécuté chaque scénario (OK / KO / partiel).

| N° | Autocontrôle du jalon 1 | Résultat attendu | Constat |
|----|-------------------------|------------------|---------|
| J1 | Créer deux tickets de priorités différentes, puis consulter les tickets. | Ils apparaissent « À faire », sans responsable. | [à constater] |
| J2 | Assigner un ticket à un programmeur, puis afficher les tickets de ce programmeur. | Le ticket apparaît dans sa liste. | [à constater] |
| J3 | Arrêter puis relancer l'application, consulter les tickets. | Tickets, statuts et assignations retrouvés à l'identique. | [à constater] |

## 3. Limites connues à ce stade

- Fonctionnalités non réalisées (jalon final) : démarrer / soumettre / valider / renvoyer un ticket,
  limite de 3 tickets « En cours », modification du titre ou de la priorité, suppression,
  liste des tickets à répartir.
- Aucun test automatisé ; la vérification se fait à la main via la console.

## 4. README — Lancer et essayer l'application

**Prérequis** : JDK 17+, Maven, XAMPP (MariaDB démarré), utilisateur `root` sans mot de passe
(sinon adapter `src/main/resources/META-INF/persistence.xml`).

1. Dans phpMyAdmin, onglet « Importer », choisir `sql/efreiforge.sql` (crée la base `efreiforge`,
   les tables et le jeu de données : 6 collaborateurs, 8 tickets).
2. Dans le dossier du projet : `mvn compile exec:java`
   (ou lancer `HelloEfrei` depuis IntelliJ).
3. Essais : option 8 (liste des tickets) ; option 7 (créer un ticket) ;
   option 9 (assigner le ticket 1 à `C001`, puis à `C003` qui est testeur : refusé) ;
   option 10 (tickets de `C001`) ; quitter (0), relancer, option 8.

## 5. Contribution de chacun

[à compléter]
