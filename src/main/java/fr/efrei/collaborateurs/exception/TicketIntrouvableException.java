package fr.efrei.collaborateurs.exception;

/** Aucun ticket ne porte ce numéro. */
public class TicketIntrouvableException extends RuntimeException {

    private final long id;

    public TicketIntrouvableException(long id) {
        super("Aucun ticket n'a le numéro " + id);
        this.id = id;
    }

    public long id() {
        return id;
    }
}
