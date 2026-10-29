package fr.efrei.collaborateurs;

import fr.efrei.collaborateurs.service.CollaborateurService;
import fr.efrei.collaborateurs.service.TicketService;
import fr.efrei.collaborateurs.ui.ConsoleApp;
import jakarta.persistence.EntityManagerFactory;

/** Point d'entrée : il assemble les morceaux, rien de plus. */
public class HelloEfrei {

    public static void main(String[] args) {
        EntityManagerFactory emf = JpaUtil.initEMFactory();
        CollaborateurService serviceCollaborateur = new CollaborateurService(emf);
        TicketService serviceTicket = new TicketService(emf);
        try {
            new ConsoleApp(serviceCollaborateur, serviceTicket).lancer();
        } finally {
            JpaUtil.fermerEMFactory();
        }
    }
}
