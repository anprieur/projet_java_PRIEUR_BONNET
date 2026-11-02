package fr.efrei.collaborateurs.exception;

public class SuppressionTicketException extends RuntimeException {
    private final long identifiant;

    public SuppressionTicketException(long identifiant) {
        super("Vous ne pouvez pas supprimer le ticket : " + identifiant);
        this.identifiant = identifiant;
    }

    public long identifiant() {
        return identifiant;
    }
}
