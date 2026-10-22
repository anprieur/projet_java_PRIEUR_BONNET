package fr.efrei.collaborateurs.exception;

/** Règle métier violée : l'identifiant est déjà utilisé. */
public class CollaborateurDejaExistantException extends RuntimeException {

    private final String identifiant;

    public CollaborateurDejaExistantException(String identifiant) {
        super("Un collaborateur de identifiant " + identifiant + " existe déjà");
        this.identifiant = identifiant;
    }

    public String identifiant() {
        return identifiant;
    }
}
