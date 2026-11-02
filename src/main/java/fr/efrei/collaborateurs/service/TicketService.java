package fr.efrei.collaborateurs.service;

import fr.efrei.collaborateurs.exception.*;
import fr.efrei.collaborateurs.modele.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
                verifierMoinsDe3EnCours(em, t.getAssigne());
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
            if (t.getStatut() == Statut.A_VALIDER && t.getAssigne() != null) {
                verifierMoinsDe3EnCours(em, t.getAssigne());
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

    public Ticket modifierTitre(long ticketId, String nouveauTitre) {
        Ticket ticket = Transactions.executer(emf, em -> {
            Ticket t = em.find(Ticket.class, ticketId);
            if (t == null) {
                throw new TicketIntrouvableException(ticketId);
            }
            t.setTitre(nouveauTitre);
            return t;
        });
        logger.info("Titre du ticket #{} modifié", ticketId);
        return ticket;
    }

    public Ticket modifierPriorite(long ticketId, Priorite nouvellePriorite) {
        Ticket ticket = Transactions.executer(emf, em -> {
            Ticket t = em.find(Ticket.class, ticketId);
            if (t == null) {
                throw new TicketIntrouvableException(ticketId);
            }
            t.setPriorite(nouvellePriorite);
            return t;
        });
        logger.info("Priorité du ticket #{} modifiée en {}", ticketId, nouvellePriorite);
        return ticket;
    }

    public List<Ticket> ticketsARepartir() {
        try (EntityManager em = emf.createEntityManager()) {
            List<Ticket> tickets = em.createQuery("select t from Ticket t where t.assigne is null", Ticket.class)
                    .getResultList();
            tickets.sort(Comparator.comparing(Ticket::getPriorite).reversed()
                    .thenComparing(Ticket::getId));
            return tickets;
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

    public Map<Statut, Long> nombreTicketsParStatut() {
        try (EntityManager em = emf.createEntityManager()) {
            Map<Statut, Long> resultat = new EnumMap<>(Statut.class);
            for (Statut statut : Statut.values()) {
                resultat.put(statut, 0L);
            }
            List<Object[]> lignes = em.createQuery(
                            "select t.statut, count(t) from Ticket t group by t.statut", Object[].class)
                    .getResultList();
            for (Object[] ligne : lignes) {
                resultat.put((Statut) ligne[0], (Long) ligne[1]);
            }
            return resultat;
        }
    }

    public Map<Programmeur, Long> chargeDesProgrammeurs() {
        try (EntityManager em = emf.createEntityManager()) {
            Map<Programmeur, Long> resultat = new LinkedHashMap<>();
            List<Programmeur> programmeurs = em.createQuery(
                            "select p from Programmeur p order by p.identifiant", Programmeur.class)
                    .getResultList();
            for (Programmeur p : programmeurs) {
                resultat.put(p, compterEnCours(em, p));
            }
            return resultat;
        }
    }

    public List<Ticket> reassignerTicketsAFaire(String identifiantSource, String identifiantCible) {
        if (identifiantSource.equals(identifiantCible)) {
            throw new AssignationRefuseeException("Le programmeur source et le programmeur cible sont identiques.");
        }
        List<Ticket> tickets = Transactions.executer(emf, em -> {
            Collaborateur source = em.find(Collaborateur.class, identifiantSource);
            if (source == null) {
                throw new CollaborateurIntrouvableException(identifiantSource);
            }
            Collaborateur cible = em.find(Collaborateur.class, identifiantCible);
            if (cible == null) {
                throw new CollaborateurIntrouvableException(identifiantCible);
            }
            if (!cible.peutEtreResponsableDeTickets()) {
                throw new AssignationRefuseeException(identifiantCible + " est " + cible.getMetier()
                        + " : seul un Programmeur peut reprendre des tickets.");
            }
            List<Ticket> aReassigner = em.createQuery(
                            "select t from Ticket t where t.assigne = :source and t.statut = :statut order by t.id",
                            Ticket.class)
                    .setParameter("source", source)
                    .setParameter("statut", Statut.A_FAIRE)
                    .getResultList();
            for (Ticket t : aReassigner) {
                t.assigner(cible);
            }
            return aReassigner;
        });
        logger.info("{} ticket(s) réassigné(s) de {} à {}", tickets.size(), identifiantSource, identifiantCible);
        return tickets;
    }

    private long compterEnCours(EntityManager em, Collaborateur programmeur) {
        return em.createQuery(
                        "select count(t) from Ticket t where t.assigne = :c and t.statut = :statut", Long.class)
                .setParameter("c", programmeur)
                .setParameter("statut", Statut.EN_COURS)
                .getSingleResult();
    }

    private void verifierMoinsDe3EnCours(EntityManager em, Collaborateur programmeur) {
        long enCours = compterEnCours(em, programmeur);
        if (enCours >= 3) {
            throw new TransitionRefuseeException(programmeur.getIdentifiant()
                    + " a déjà " + enCours + " tickets « En cours » (maximum 3).");
        }
    }
}
