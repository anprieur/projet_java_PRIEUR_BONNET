package fr.efrei.collaborateurs;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/** Une seule EntityManagerFactory pour toute l'application (elle est coûteuse à créer). */
public final class JpaUtil {

    private static final String UNITE = "collaborateurs-pu";
    private static EntityManagerFactory emFactory;

    private JpaUtil() {
    }

    public static synchronized EntityManagerFactory initEMFactory() {
        if (emFactory == null) {
            emFactory = Persistence.createEntityManagerFactory(UNITE);
        }
        return emFactory;
    }

    public static synchronized void fermerEMFactory() {
        if (emFactory != null) {
            emFactory.close();
            emFactory = null;
        }
    }
}
