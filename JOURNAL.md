# JOURNAL — EFREIForge (Jalon 1)

Antoine Prieur & Ivane Bonnet

## Ce qu'on a fait

On est partis du TP4.

On a ajouté :
- l'entité `Ticket` ;
- `TicketService` pour créer, lister et assigner les tickets ;
- les options 7 à 10 du menu ;
- deux exceptions : ticket introuvable et assignation refusée ;
- le script `sql/efreiforge.sql`.

Un nouveau ticket est « À faire », sans responsable. Seul un programmeur peut être assigné. Si l'assignation est refusée, rien n'est modifié.

## Tests

- Créer deux tickets puis les lister : OK.
- Assigner un ticket à `C001` puis voir ses tickets : OK.
- Quitter puis relancer : les tickets sont toujours là. OK.
- Assigner à `C003`, un testeur : refusé, OK.

## Pas encore fait

Démarrer, soumettre et valider un ticket. Modifier ou supprimer un ticket.

## Lancer le projet

1. Lancer MariaDB dans XAMPP.
2. Importer `sql/efreiforge.sql` dans phpMyAdmin.
3. Lancer `mvn compile exec:java`, ou `HelloEfrei` dans IntelliJ.

## Qui a fait quoi

on a tout fait à deux donc 50/50
