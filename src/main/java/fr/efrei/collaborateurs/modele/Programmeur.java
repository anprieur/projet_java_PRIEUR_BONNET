package fr.efrei.collaborateurs.modele;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Programmeur")
public class Programmeur extends Collaborateur {

    // Nullable en base : les Testeurs n'ont pas de langage (SINGLE_TABLE)
    @Column(name = "langage_prefere", length = 40)
    private String langagePrefere;

    protected Programmeur() {
    }

    public Programmeur(String identifiant, String prenom, String nom, double salaire,
                       Adresse adresse, String langagePrefere) {
        super(identifiant, prenom, nom, salaire, adresse);
        this.langagePrefere = langagePrefere;
    }

    public String getLangageExpertise() { return langagePrefere; }

    @Override
    public String getMetier() { return "Programmeur"; }
}
