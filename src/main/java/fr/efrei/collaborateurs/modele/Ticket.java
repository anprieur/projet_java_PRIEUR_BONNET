package fr.efrei.collaborateurs.modele;

import fr.efrei.collaborateurs.exception.AssignationRefuseeException;
import fr.efrei.collaborateurs.exception.TermineStatutException;
import fr.efrei.collaborateurs.exception.TransitionRefuseeException;
import jakarta.persistence.*;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priorite priorite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statut statut;

    @ManyToOne
    @JoinColumn(name = "assigne_id")
    private Collaborateur assigne;

    protected Ticket() {
        // exigé par JPA, comme pour vos autres entités
    }

    public Ticket(String titre, Priorite priorite) {
        this.titre = titre;
        this.priorite = priorite;
        this.statut=Statut.A_FAIRE;
    }

    public void demarrerTicket() {
        if (assigne == null || assigne.peutEtreResponsableDeTickets()) {
            throw new TransitionRefuseeException("Le ticket doit être assigné à un programmeur pour qu'il soit démarré.");
        } else if (!this.statut.equals(Statut.A_FAIRE)) {
            throw new TransitionRefuseeException("Le ticket doit être au statut 'A FAIRE' pour qu'il soit démarré.");
        } else {
            this.statut = Statut.EN_COURS;
        }
    }

    public void aValiderTicket() {
        if (this.statut != Statut.EN_COURS) {
            throw new TransitionRefuseeException("Le ticket doit être 'EN_COURS' pour qu'il puisse être soumis à une validation.");
        }
        this.statut = Statut.A_VALIDER;
    }

    public void renvoyerTicket(Testeur testeur) {
        if (!testeur.getMetier().equals("Testeur")) {
            throw new TransitionRefuseeException("Le ticket ne peut être renvoyé que par un testeur.");
        } else if (!this.statut.equals(Statut.A_VALIDER)) {
            throw new TransitionRefuseeException("Le ticket doit être au statut 'A VALIDER' pour qu'il soit renvoyer au statut 'EN COURS'.");
        } else {
            this.statut = Statut.EN_COURS;
        }
    }

    public void terminerTicket(Testeur testeur) {
        if (!testeur.getMetier().equals("Testeur")) {
            throw new TransitionRefuseeException("Le ticket ne peut être renvoyé que par un testeur.");
        } else if (!this.statut.equals(Statut.A_VALIDER)) {
            throw new TransitionRefuseeException("Le ticket doit être au statut 'A VALIDER' pour qu'il soit mis au statut 'TERMINE'.");
        } else {
            this.statut = Statut.TERMINE;
        }
    }

    public void assigner(Collaborateur collaborateur) {
        if (collaborateur == null) {
            throw new IllegalArgumentException("Le collaborateur est obligatoire");
        }
        if (statut != Statut.A_FAIRE) {
            throw new AssignationRefuseeException(
                    "Le ticket #" + id + " n'est plus « À faire » : son responsable ne peut plus changer.");
        }
        if (!collaborateur.peutEtreResponsableDeTickets()) {
            throw new AssignationRefuseeException(
                    collaborateur.getIdentifiant() + " est " + collaborateur.getMetier()
                            + " : seul un Programmeur peut être responsable d'un ticket.");
        }
        this.assigne = collaborateur;
    }

    public boolean verifierSupprimable() {
        boolean reponse;
        if (this.statut != Statut.A_FAIRE) reponse = false;
        else reponse = true;
        return reponse;
    }

    public String getTitre() {
        return titre;
    }

    public Priorite getPriorite() {
        return priorite;
    }

    public Statut getStatut() {
        return statut;
    }

    public Collaborateur getAssigne() {
        return assigne;
    }

    public Long getId() {
        return id;
    }

    public void setTitre(String titre) {
        if (titre == null || titre.isBlank()) {
            throw new IllegalArgumentException("Le titre d'un ticket est obligatoire");
        } else if (this.statut == Statut.TERMINE) {
            throw new TermineStatutException("Le ticket est terminé il ne peut pas être modifié.");
        }
        this.titre=titre.trim();
    }

    public void setPriorite(Priorite priorite) {
        if (priorite == null) {
            throw new IllegalArgumentException("La priorité d'un ticket est obligatoire");
        } else if (this.statut == Statut.TERMINE) {
            throw new TermineStatutException("Le ticket est terminé il ne peut pas être modifié.");
        }
        this.priorite= priorite;
    }

    @Override
    public String toString() {
        String responsable = assigne == null ? "aucun responsable" : assigne.getIdentifiant();
        return String.format("#%d %s [%s] - %s - %s", id, titre, priorite, statut, responsable);
    }

}
