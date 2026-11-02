package fr.efrei.collaborateurs.ui;

import fr.efrei.collaborateurs.exception.AssignationRefuseeException;
import fr.efrei.collaborateurs.exception.CollaborateurDejaExistantException;
import fr.efrei.collaborateurs.exception.CollaborateurIntrouvableException;
import fr.efrei.collaborateurs.exception.SuppressionTicketException;
import fr.efrei.collaborateurs.exception.TermineStatutException;
import fr.efrei.collaborateurs.exception.TicketIntrouvableException;
import fr.efrei.collaborateurs.exception.TransitionRefuseeException;
import fr.efrei.collaborateurs.modele.*;
import fr.efrei.collaborateurs.service.CollaborateurService;
import fr.efrei.collaborateurs.service.TicketService;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Seule classe qui dialogue avec l'utilisateur : ses println sont des affichages
 * utilisateur. Les informations techniques passent par le logger.
 */
public class ConsoleApp {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleApp.class);

    private final CollaborateurService serviceCollaborateur;
    private final TicketService serviceTicket;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp(CollaborateurService serviceCollaborateur, TicketService serviceTicket) {
        this.serviceCollaborateur = serviceCollaborateur;
        this.serviceTicket = serviceTicket;
    }

    public void lancer() {
        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            String choix = scanner.nextLine().trim();
            try {
                switch (choix) {
                    case "1" -> ajouterProgrammeur();
                    case "2" -> ajouterTesteur();
                    case "3" -> listerSalaireSuperieur();
                    case "4" -> rechercherParNom();
                    case "5" -> augmenter();
                    case "6" -> listerParLangage();
                    case "7" -> creerTicket();
                    case "8" -> consulterTickets();
                    case "9" -> assignerTicket();
                    case "10" -> ticketsDUnCollaborateur();
                    case "11" -> demarrerTicket();
                    case "12" -> soumettreTicket();
                    case "13" -> validerTicket();
                    case "14" -> renvoyerTicket();
                    case "15" -> modifierTicket();
                    case "16" -> supprimerTicket();
                    case "17" -> ticketsARepartir();
                    case "18" -> tableauDeBord();
                    case "19" -> reassignerTicketsAFaire();
                    case "0" -> continuer = false;
                    default -> System.out.println("Choix inconnu.");
                }
            } catch (CollaborateurDejaExistantException e) {
                System.out.println("Cet identifiant est déjà pris (" + e.identifiant() + "). Choisissez-en un autre.");
            } catch (CollaborateurIntrouvableException e) {
                System.out.println("Aucun collaborateur n'a l'identifiant " + e.identifiant() + ".");
            } catch (TicketIntrouvableException e) {
                System.out.println("Aucun ticket n'a le numéro " + e.id() + ".");
            } catch (AssignationRefuseeException e) {
                System.out.println("Assignation refusée : " + e.getMessage());
            } catch (TransitionRefuseeException e) {
                System.out.println("Opération refusée : " + e.getMessage());
            } catch (TermineStatutException e) {
                System.out.println("Modification refusée : " + e.getMessage());
            } catch (SuppressionTicketException e) {
                System.out.println("Suppression refusée : le ticket #" + e.identifiant()
                        + " a déjà été commencé, seul un ticket « À faire » peut être supprimé.");
            } catch (SaisieInvalideException | IllegalArgumentException e) {
                System.out.println("Saisie refusée : " + e.getMessage());
            } catch (PersistenceException e) {
                // Problème technique : détail dans les logs, message sobre pour l'utilisateur
                logger.error("Erreur d'accès à la base de données", e);
                System.out.println("Un problème technique est survenu, l'opération n'a pas été enregistrée.");
            } catch (RuntimeException e) {
                logger.error("Erreur inattendue", e);
                System.out.println("Erreur inattendue : " + e.getMessage());
            }
        }
        System.out.println("Au revoir !");
    }

    private void afficherMenu() {
        System.out.println();
        System.out.println("1. Ajouter un programmeur");
        System.out.println("2. Ajouter un testeur");
        System.out.println("3. Salaire supérieur à ...");
        System.out.println("4. Rechercher par nom");
        System.out.println("5. Augmenter un salaire");
        System.out.println("6. Programmeurs par langage");
        System.out.println("7. Créer un ticket");
        System.out.println("8. Consulter les tickets");
        System.out.println("9. Assigner un ticket");
        System.out.println("10. Tickets d'un collaborateur");
        System.out.println("11. Démarrer un ticket");
        System.out.println("12. Soumettre un ticket à validation");
        System.out.println("13. Valider un ticket (testeur)");
        System.out.println("14. Renvoyer un ticket en cours (testeur)");
        System.out.println("15. Modifier le titre ou la priorité d'un ticket");
        System.out.println("16. Supprimer un ticket");
        System.out.println("17. Tickets à répartir");
        System.out.println("18. Tableau de bord");
        System.out.println("19. Réassigner les tickets « À faire » d'un programmeur");
        System.out.println("0. Quitter");
        System.out.print("Votre choix : ");
    }

    // ---- Collaborateur ----

    private void ajouterProgrammeur() {
        String identifiant = lireTexte("Identifiant");
        String prenom = lireTexte("Prénom");
        String nom = lireTexte("Nom");
        double salaire = lireDecimal("Salaire");
        String langage = lireTexte("Langage préféré");
        serviceCollaborateur.ajouterCollaborateur(new Programmeur(identifiant, prenom, nom, salaire, lireAdresse(), langage));
        System.out.println("Programmeur enregistré.");
    }

    private void ajouterTesteur() {
        String identifiant = lireTexte("Identifiant");
        String prenom = lireTexte("Prénom");
        String nom = lireTexte("Nom");
        double salaire = lireDecimal("Salaire");
        serviceCollaborateur.ajouterCollaborateur(new Testeur(identifiant, prenom, nom, salaire, lireAdresse()));
        System.out.println("Testeur enregistré.");
    }

    private void listerSalaireSuperieur() {
        afficher(serviceCollaborateur.salaireSuperieurA(lireDecimal("Seuil")));
    }

    private void rechercherParNom() {
        afficher(serviceCollaborateur.rechercherParNom(lireTexte("Fragment du nom")));
    }

    private void augmenter() {
        String identifiant = lireTexte("Identifiant");
        double pourcentage = lireDecimal("Pourcentage d'augmentation (10 = +10 %)");
        double nouveau = serviceCollaborateur.augmenterSalaire(identifiant, pourcentage);
        System.out.printf("Nouveau salaire : %.2f EUR%n", nouveau);
    }

    private void listerParLangage() {
        afficher(serviceCollaborateur.programmeursParLangage(lireTexte("Langage")));
    }

    // ---- Ticket ----

    private void creerTicket() {
        String titre = lireTexte("Titre");
        Priorite priorite = lirePriorite();
        System.out.println("Ticket créé : " + serviceTicket.creerTicket(titre, priorite));
    }

    private void consulterTickets() {
        Statut statut = lireStatutOuTous();
        afficher(statut == null ? serviceTicket.listerTousLesTicket() : serviceTicket.listerTicketParStatut(statut));
    }

    private void assignerTicket() {
        long numero = lireEntier("Numéro du ticket");
        String identifiant = lireTexte("Identifiant du programmeur");
        System.out.println("Ticket assigné : " + serviceTicket.assignerTicket(numero, identifiant));
    }

    private void ticketsDUnCollaborateur() {
        afficher(serviceTicket.ticketsDeCollaborateur(lireTexte("Identifiant du collaborateur")));
    }

    private void demarrerTicket() {
        long numero = lireEntier("Numéro du ticket");
        System.out.println("Ticket démarré : " + serviceTicket.demarrerTicket(numero));
    }

    private void soumettreTicket() {
        long numero = lireEntier("Numéro du ticket");
        System.out.println("Ticket soumis à validation : " + serviceTicket.aValiderTicket(numero));
    }

    private void validerTicket() {
        long numero = lireEntier("Numéro du ticket");
        String testeur = lireTexte("Identifiant du testeur");
        System.out.println("Ticket validé : " + serviceTicket.terminerTicket(numero, testeur));
    }

    private void renvoyerTicket() {
        long numero = lireEntier("Numéro du ticket");
        String testeur = lireTexte("Identifiant du testeur");
        System.out.println("Ticket renvoyé en cours : " + serviceTicket.renvoyerTicket(numero, testeur));
    }

    private void modifierTicket() {
        long numero = lireEntier("Numéro du ticket");
        String choix = lireTexte("Modifier (1) le titre ou (2) la priorité");
        switch (choix) {
            case "1" -> System.out.println("Ticket modifié : "
                    + serviceTicket.modifierTitre(numero, lireTexte("Nouveau titre")));
            case "2" -> System.out.println("Ticket modifié : "
                    + serviceTicket.modifierPriorite(numero, lirePriorite()));
            default -> throw new SaisieInvalideException("choix inconnu : " + choix);
        }
    }

    private void supprimerTicket() {
        long numero = lireEntier("Numéro du ticket");
        serviceTicket.supprimerTicket(numero);
        System.out.println("Ticket #" + numero + " supprimé.");
    }

    private void ticketsARepartir() {
        List<Ticket> tickets = serviceTicket.ticketsARepartir();
        if (tickets.isEmpty()) {
            System.out.println("Tous les tickets sont assignés : rien à répartir.");
            return;
        }
        System.out.println("Tickets à répartir (" + tickets.size() + ")");
        for (Ticket t : tickets) {
            System.out.printf("  #%-3d %-8s %s%n", t.getId(), t.getPriorite(), t.getTitre());
        }
    }

    private void tableauDeBord() {
        System.out.println("Tickets par statut");
        for (Map.Entry<Statut, Long> e : serviceTicket.nombreTicketsParStatut().entrySet()) {
            System.out.printf("  %-10s %d%n", e.getKey(), e.getValue());
        }
        System.out.println("Charge des programmeurs (tickets « En cours »)");
        Map<Programmeur, Long> charge = serviceTicket.chargeDesProgrammeurs();
        if (charge.isEmpty()) {
            System.out.println("  Aucun programmeur.");
        }
        for (Map.Entry<Programmeur, Long> e : charge.entrySet()) {
            Programmeur p = e.getKey();
            System.out.printf("  %-5s %-20s %d/3%n", p.getIdentifiant(), p.getPrenom() + " " + p.getNom(), e.getValue());
        }
    }

    private void reassignerTicketsAFaire() {
        String source = lireTexte("Identifiant du programmeur qui quitte le projet");
        String cible = lireTexte("Identifiant du programmeur qui reprend ses tickets");
        List<Ticket> tickets = serviceTicket.reassignerTicketsAFaire(source, cible);
        if (tickets.isEmpty()) {
            System.out.println(source + " n'a aucun ticket « À faire » à réassigner.");
        } else {
            System.out.println(tickets.size() + " ticket(s) réassigné(s) à " + cible + " :");
            afficher(tickets);
        }
    }

    // ---- Affichage ----

    private Adresse lireAdresse() {
        return new Adresse(lireTexte("Rue"), lireTexte("Code postal"), lireTexte("Ville"), lireTexte("Pays"));
    }

    private void afficher(List<?> liste) {
        if (liste.isEmpty()) {
            System.out.println("Aucun résultat.");
        }
        liste.forEach(System.out::println);
    }

    // ---- Lecture : une erreur de saisie n'est pas une règle métier ----

    private String lireTexte(String libelle) {
        System.out.print(libelle + " : ");
        String valeur = scanner.nextLine().trim();
        if (valeur.isEmpty()) {
            throw new SaisieInvalideException(libelle + " ne peut pas être vide");
        }
        return valeur;
    }

    private double lireDecimal(String libelle) {
        String texte = lireTexte(libelle);
        try {
            return Double.parseDouble(texte.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new SaisieInvalideException("\"" + texte + "\" n'est pas un nombre");
        }
    }

    private long lireEntier(String libelle) {
        String texte = lireTexte(libelle);
        try {
            return Long.parseLong(texte);
        } catch (NumberFormatException e) {
            throw new SaisieInvalideException("\"" + texte + "\" n'est pas un nombre entier");
        }
    }

    private Priorite lirePriorite() {
        String texte = lireTexte("Priorité (BASSE, NORMALE, HAUTE)").toUpperCase();
        try {
            return Priorite.valueOf(texte);
        } catch (IllegalArgumentException e) {
            throw new SaisieInvalideException("priorité inconnue : " + texte);
        }
    }

    private Statut lireStatutOuTous() {
        System.out.print("Statut (A_FAIRE, EN_COURS, A_VALIDER, TERMINE) ou vide pour tous : ");
        String texte = scanner.nextLine().trim().toUpperCase();
        if (texte.isEmpty() || texte.equals("TOUS")) {
            return null;
        }
        try {
            return Statut.valueOf(texte);
        } catch (IllegalArgumentException e) {
            throw new SaisieInvalideException("statut inconnu : " + texte);
        }
    }

    private static class SaisieInvalideException extends RuntimeException {
        SaisieInvalideException(String message) {
            super(message);
        }
    }
}
