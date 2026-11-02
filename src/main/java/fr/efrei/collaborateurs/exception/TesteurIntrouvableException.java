package fr.efrei.collaborateurs.exception;

public class TesteurIntrouvableException extends RuntimeException {
    private final String identifiant;

    public TesteurIntrouvableException(String identifiant) {
        super("Aucun testeur d'identifiant " + identifiant);
        this.identifiant = identifiant;
    }

    public String identifiant() {
        return identifiant;
    }
}
