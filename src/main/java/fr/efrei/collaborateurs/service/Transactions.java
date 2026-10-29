package fr.efrei.collaborateurs.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

final class Transactions {

    private static final Logger logger = LoggerFactory.getLogger(Transactions.class);

    private Transactions() {
    }

    static <T> T executer(EntityManagerFactory emf, Function<EntityManager, T> travail) {
        try (EntityManager em = emf.createEntityManager()) {
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
                throw e;
            }
        }
    }
}
