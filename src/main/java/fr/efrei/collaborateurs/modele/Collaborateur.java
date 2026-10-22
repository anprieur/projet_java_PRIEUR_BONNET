package fr.efrei.collaborateurs.modele;

import jakarta.persistence.*;

/**
 * Une seule table "collaborateur" pour toute la hiérarchie (SINGLE_TABLE).
 * La colonne "metier" indique de quelle sous-classe est chaque ligne.
 */
@Entity
@Table(name = "collaborateur")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "metier", length = 40)
public abstract class Collaborateur {

    // L'identifiant métier du TP3 ("C001") sert de clé primaire :
    // il est stable, unique, et fourni par l'application (pas de @GeneratedValue)
    @Id
    @Column(length = 10)
    private String identifiant;

    @Column(nullable = false, length = 50)
    private String prenom;

    @Column(nullable = false, length = 50)
    private String nom;

    @Column(nullable = false)
    private double salaire;

    // Plusieurs collaborateurs peuvent partager la même adresse
    // => la clé étrangère est du côté "plusieurs" : collaborateur.adresse_id
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "adresse_id", nullable = false)
    private Adresse adresse;

    protected Collaborateur() {
        // exigé par JPA
    }

    protected Collaborateur(String identifiant, String prenom, String nom, double salaire, Adresse adresse) {
        if (identifiant == null || identifiant.isBlank()) {
            throw new IllegalArgumentException("Le identifiant est obligatoire");
        }
        if (prenom == null || prenom.isBlank() || nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le prénom et le nom sont obligatoires");
        }
        if (salaire < 0) {
            throw new IllegalArgumentException("Le salaire ne peut pas être négatif : " + salaire);
        }
        if (adresse == null) {
            throw new IllegalArgumentException("Un collaborateur a toujours une adresse");
        }
        this.identifiant = identifiant.trim();
        this.prenom = prenom.trim();
        this.nom = nom.trim();
        this.salaire = salaire;
        this.adresse = adresse;
    }

    public void augmenterSalaire(double pourcentage) {
        if (pourcentage <= 0) {
            throw new IllegalArgumentException("Le pourcentage d'augmentation doit être positif : " + pourcentage);
        }
        this.salaire = salaire * (1 + pourcentage / 100);
    }

    public abstract String getMetier();

    public String getIdentifiant() { return identifiant; }
    public String getPrenom() { return prenom; }
    public String getNom() { return nom; }
    public double getSalaire() { return salaire; }
    public Adresse getAdresse() { return adresse; }

    @Override
    public String toString() {
        return String.format("%s %s %s (%s) - %.2f EUR - %s", identifiant, prenom, nom, getMetier(), salaire, adresse);
    }
}
