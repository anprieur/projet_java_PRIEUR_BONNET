package fr.efrei.collaborateurs.modele;

import jakarta.persistence.*;

/**
 * depuis le TP2, Adresse était déjà une classe. Pour devenir une Entity elle gagne
 * un identifiant technique, un constructeur sans argument et des annotations.
 */
@Entity
@Table(name = "adresse")
public class Adresse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String rue;

    @Column(name = "code_postal", nullable = false, length = 10)
    private String codePostal;

    @Column(nullable = false, length = 60)
    private String ville;

    @Column(nullable = false, length = 60)
    private String pays;

    protected Adresse() {
        // exigé par JPA
    }

    public Adresse(String rue, String codePostal, String ville, String pays) {
        this.rue = exiger(rue, "rue");
        this.codePostal = exiger(codePostal, "code postal");
        this.ville = exiger(ville, "ville");
        this.pays = exiger(pays, "pays");
    }

    private static String exiger(String valeur, String nom) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("L'adresse exige une valeur pour : " + nom);
        }
        return valeur.trim();
    }

    public Long getId() { return id; }
    public String getRue() { return rue; }
    public String getCodePostal() { return codePostal; }
    public String getVille() { return ville; }
    public String getPays() { return pays; }

    @Override
    public String toString() {
        return rue + ", " + codePostal + " " + ville + " (" + pays + ")";
    }
}
