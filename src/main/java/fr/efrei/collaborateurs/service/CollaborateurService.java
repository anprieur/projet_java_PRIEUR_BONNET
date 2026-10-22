package fr.efrei.collaborateurs.service;

import fr.efrei.collaborateurs.exception.CollaborateurDejaExistantException;
import fr.efrei.collaborateurs.exception.CollaborateurIntrouvableException;
import fr.efrei.collaborateurs.modele.Collaborateur;
import fr.efrei.collaborateurs.modele.Programmeur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Les opérations métier + persistance. Il n'y a ni Scanner ni println ici :
 * ce service signale les problèmes par des exceptions, l'interface les affiche.
 * Chaque méthode = une unité de travail = un EntityManager.
 */
public class CollaborateurService {

    private static final Logger logger = LoggerFactory.getLogger(CollaborateurService.class);

    private final EntityManagerFactory eMFactory;

    public CollaborateurService(EntityManagerFactory emf) {
        this.eMFactory = emf;
    }

    // ---------- Écritures (dans une transaction) ----------

    public void ajouter(Collaborateur collaborateur) {
        dansUneTransaction(em -> {
            // find cherche par clé primaire : null = identifiant libre
            if (em.find(Collaborateur.class, collaborateur.getIdentifiant()) != null) {
                logger.warn("Doublon refusé pour l'identifiant {}", collaborateur.getIdentifiant());
                throw new CollaborateurDejaExistantException(collaborateur.getIdentifiant());
            }
            em.persist(collaborateur);
            return null;
        });
        logger.info("Collaborateur {} enregistré", collaborateur.getIdentifiant());
    }

    /** Aucun appel à persist ni à UPDATE : l'entité est gérée, JPA détecte le changement. */
    public double augmenterSalaire(String identifiant, double pourcentage) {
        return dansUneTransaction(em -> {
            Collaborateur c = em.find(Collaborateur.class, identifiant);
            if (c == null) {
                throw new CollaborateurIntrouvableException(identifiant);
            }
            double avant = c.getSalaire();
            c.augmenterSalaire(pourcentage);
            logger.debug("Salaire de {} : {} -> {}", identifiant, avant, c.getSalaire());
            return c.getSalaire();
        });
    }

    // ---------- Lectures ----------

    /** find() renvoie null si l'identifiant n'existe pas : on le traduit en Optional. */
    public Optional<Collaborateur> trouverParIdentifiant(String identifiant) {
        try (EntityManager em = eMFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(Collaborateur.class, identifiant));
        }
    }

    public List<Collaborateur> salaireSuperieurA(double seuil) {
        try (EntityManager em = eMFactory.createEntityManager()) {
            return em.createQuery(
                            "select c from Collaborateur c where c.salaire > :seuil order by c.salaire desc",
                            Collaborateur.class)
                    .setParameter("seuil", seuil)
                    .getResultList();
        }
    }

    public List<Collaborateur> rechercherParNom(String fragment) {
        try (EntityManager em = eMFactory.createEntityManager()) {
            return em.createQuery(
                            "select c from Collaborateur c where lower(c.nom) like :motif order by c.nom",
                            Collaborateur.class)
                    .setParameter("motif", "%" + fragment.toLowerCase() + "%")
                    .getResultList();
        }
    }

    /** Requête polymorphe : seuls les Programmeur sont lus, le filtre est fait par la base. */
    public List<Programmeur> programmeursParLangage(String langage) {
        try (EntityManager em = eMFactory.createEntityManager()) {
            return em.createQuery(
                            "select p from Programmeur p where lower(p.langagePrefere) = :langage order by p.nom",
                            Programmeur.class)
                    .setParameter("langage", langage.toLowerCase())
                    .getResultList();
        }
    }

    // ---------- Outils internes ----------

    /** begin / commit / rollback écrits une seule fois (Challenge A). */
    private <T> T dansUneTransaction(Function<EntityManager, T> travail) {
        try (EntityManager em = eMFactory.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                T resultat = travail.apply(em);
                transaction.commit();
                return resultat;
            } catch (RuntimeException e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                    logger.debug("Transaction annulée (rollback) : {}", e.toString());
                }
                throw e; // on ne l'avale pas : l'appelant décide quoi dire à l'utilisateur
            }
        }
    }
}
