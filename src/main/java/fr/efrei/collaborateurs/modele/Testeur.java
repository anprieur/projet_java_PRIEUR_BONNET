package fr.efrei.collaborateurs.modele;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Testeur")
public class Testeur extends Collaborateur {

    protected Testeur() {
    }

    public Testeur(String identifiant, String prenom, String nom, double salaire, Adresse adresse) {
        super(identifiant, prenom, nom, salaire, adresse);
    }

    @Override
    public String getMetier() { return "Testeur"; }
}
