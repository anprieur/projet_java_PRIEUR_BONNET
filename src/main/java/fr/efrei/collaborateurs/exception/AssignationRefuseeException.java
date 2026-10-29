package fr.efrei.collaborateurs.exception;

/** Règle métier violée lors d'une assignation (mauvais métier, ticket déjà commencé...). */
public class AssignationRefuseeException extends RuntimeException {

    public AssignationRefuseeException(String message) {
        super(message);
    }
}
