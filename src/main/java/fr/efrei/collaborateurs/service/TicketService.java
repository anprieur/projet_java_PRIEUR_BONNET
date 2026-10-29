package fr.efrei.collaborateurs.service;

import fr.efrei.collaborateurs.exception.CollaborateurIntrouvableException;
import fr.efrei.collaborateurs.exception.TicketIntrouvableException;
import fr.efrei.collaborateurs.modele.Collaborateur;
import fr.efrei.collaborateurs.modele.Priorite;
import fr.efrei.collaborateurs.modele.Statut;
import fr.efrei.collaborateurs.modele.Ticket;
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
