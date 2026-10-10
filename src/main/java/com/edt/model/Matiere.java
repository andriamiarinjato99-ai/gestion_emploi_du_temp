package com.edt.model;

import javafx.beans.property.*;

public class Matiere {

    private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private final StringProperty code = new SimpleStringProperty(this, "code");
    private final StringProperty nom = new SimpleStringProperty(this, "nom");
    private final IntegerProperty volumeHoraire = new SimpleIntegerProperty(this, "volumeHoraire");

    public Matiere() {
    }

    public Matiere(int id, String code, String nom, int volumeHoraire) {
        setId(id);
        setCode(code);
        setNom(nom);
        setVolumeHoraire(volumeHoraire);
    }

    public int getId() { return id.get(); }
    public void setId(int v) { id.set(v); }
    public IntegerProperty idProperty() { return id; }

    public String getCode() { return code.get(); }
    public void setCode(String v) { code.set(v); }
    public StringProperty codeProperty() { return code; }

    public String getNom() { return nom.get(); }
    public void setNom(String v) { nom.set(v); }
    public StringProperty nomProperty() { return nom; }

    public int getVolumeHoraire() { return volumeHoraire.get(); }
    public void setVolumeHoraire(int v) { volumeHoraire.set(v); }
    public IntegerProperty volumeHoraireProperty() { return volumeHoraire; }

    @Override
    public String toString() {
        return code.get() + " - " + nom.get();
    }

    /** Égalité par identifiant : permet aux ComboBox de retrouver l'élément sélectionné. */
    @Override
    public boolean equals(Object autre) {
        if (this == autre) {
            return true;
        }
        if (!(autre instanceof Matiere)) {
            return false;
        }
        return getId() != 0 && getId() == ((Matiere) autre).getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }
}
