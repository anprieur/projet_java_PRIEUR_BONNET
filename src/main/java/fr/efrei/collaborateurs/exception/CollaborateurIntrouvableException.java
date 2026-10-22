package fr.efrei.collaborateurs.exception;

/** Aucun collaborateur ne porte ce identifiant. */
public class CollaborateurIntrouvableException extends RuntimeException {

    private final String identifiant;

    public CollaborateurIntrouvableException(String identifiant) {
        super("Aucun collaborateur de identifiant " + identifiant);
        this.identifiant = identifiant;
    }

    public String identifiant() {
        return identifiant;
    }
}
