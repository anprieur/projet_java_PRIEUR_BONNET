package fr.efrei.collaborateurs.service;

import fr.efrei.collaborateurs.exception.*;
import fr.efrei.collaborateurs.modele.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    private final EntityManagerFactory emf;

    public TicketService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Ticket creerTicket(String titre, Priorite priorite) {
        Ticket ticket = new Ticket(titre, priorite);
        Transactions.executer(emf, em -> {
            em.persist(ticket);
            return null;
        });
        logger.info("Ticket #{} créé", ticket.getId());
        return ticket;
    }

    public List<Ticket> listerTousLesTicket() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("select t from Ticket t order by t.id", Ticket.class).getResultList();
        }
    }

    public List<Ticket> listerTicketParStatut(Statut statut) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("select t from Ticket t where t.statut = :statut order by t.id", Ticket.class)
                    .setParameter("statut", statut)
                    .getResultList();
        }
    }

    public Ticket demarrerTicket(long ticketId) {
        Ticket ticket = Transactions.executer(emf, em -> {
            Ticket t = em.find(Ticket.class, ticketId);
            if (t == null) {
                throw new TicketIntrouvableException(ticketId);
            }

            if (t.getStatut() == Statut.A_FAIRE && t.getAssigne() != null) {

                long nombreTicketsEnCours = ticketsDeCollaborateur(t.getAssigne().getIdentifiant())
                        .stream()
                        .filter(tk -> tk.getStatut() == Statut.EN_COURS)
                        .count();

                if (nombreTicketsEnCours >= 3) {
                    throw new AssignationRefuseeException(
                            "Le collaborateur ne peut pas avoir plus de 3 tickets en cours"
                    );
                }
            }
            t.demarrerTicket();
            return t;
        });

        logger.info("Ticket #{} démarré", ticketId);
        return ticket;
    }

    public Ticket aValiderTicket(long ticketId) {
        Ticket ticket = Transactions.executer(emf, em -> {
            Ticket t = em.find(Ticket.class, ticketId);
            if (t == null) {
                throw new TicketIntrouvableException(ticketId);
            }
            t.aValiderTicket();
            return t;
        });

        logger.info("Ticket #{} soumis à la validation", ticketId);
        return ticket;
    }

    public Ticket renvoyerTicket(long ticketId, String testeurId) {
        Ticket ticket = Transactions.executer(emf, em -> {
            Ticket t = em.find(Ticket.class, ticketId);
            if (t == null) {
                throw new TicketIntrouvableException(ticketId);
            }
            Collaborateur c = em.find(Collaborateur.class, testeurId);
            if (c == null) {
                throw new CollaborateurIntrouvableException(testeurId);
            }
            if (!(c instanceof Testeur testeur))
            {
                throw new TransitionRefuseeException(testeurId + " est " + c.getMetier()
                        + " : seul un Testeur peut valider ou renvoyer un ticket.");
            }
            t.renvoyerTicket(testeur);
            return t;
        });

        logger.info("Ticket #{} renvoyer au statut 'EN COURS'", ticketId);
        return ticket;
    }

    public Ticket terminerTicket(long ticketId, String testeurId) {
        Ticket ticket = Transactions.executer(emf, em -> {
            Ticket t = em.find(Ticket.class, ticketId);
            if (t == null) {
                throw new TicketIntrouvableException(ticketId);
            }
            Collaborateur c = em.find(Collaborateur.class, testeurId);
            if (c == null) {
                throw new CollaborateurIntrouvableException(testeurId);
            }
            if (!(c instanceof Testeur testeur))
            {
                throw new TransitionRefuseeException(testeurId + " est " + c.getMetier()
                        + " : seul un Testeur peut valider ou renvoyer un ticket.");
            }
            t.terminerTicket(testeur);
            return t;
        });

        logger.info("Ticket #{} terminer", ticketId);
        return ticket;
    }

    public Ticket supprimerTicket(long ticketId) {
        Ticket ticket = Transactions.executer(emf, em ->{
            Ticket t = em.find(Ticket.class, ticketId);
            if (t == null) {
                throw new TicketIntrouvableException(ticketId);
            } else if (!t.verifierSupprimable()) {
                throw new SuppressionTicketException(ticketId);
            }
            em.remove(t);
            return t;
        });

        logger.info("Ticket #{} a été supprimé", ticketId);
        return ticket;
    }

    public Ticket assignerTicket(long ticketId, String identifiant) {
        Ticket ticket = Transactions.executer(emf, em -> {
            Ticket t = em.find(Ticket.class, ticketId);
            if (t == null) {
                throw new TicketIntrouvableException(ticketId);
            }
            Collaborateur c = em.find(Collaborateur.class, identifiant);
            if (c == null) {
                throw new CollaborateurIntrouvableException(identifiant);
            }
            t.assigner(c);
            return t;
        });
        logger.info("Ticket #{} assigné à {}", ticketId, identifiant);
        return ticket;
    }

    public List<Ticket> ticketsDeCollaborateur(String identifiant) {
        try (EntityManager em = emf.createEntityManager()) {
            if (em.find(Collaborateur.class, identifiant) == null) {
                throw new CollaborateurIntrouvableException(identifiant);
            }
            return em.createQuery(
                            "select t from Ticket t where t.assigne.identifiant = :id order by t.id", Ticket.class)
                    .setParameter("id", identifiant)
                    .getResultList();
        }
    }
}
