package com.edt.model;

import javafx.beans.property.*;

public class Classe {

    private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private final StringProperty nom = new SimpleStringProperty(this, "nom");
    private final StringProperty niveau = new SimpleStringProperty(this, "niveau");
    private final IntegerProperty effectif = new SimpleIntegerProperty(this, "effectif");

    public Classe() {
    }

    public Classe(int id, String nom, String niveau, int effectif) {
        setId(id);
        setNom(nom);
        setNiveau(niveau);
        setEffectif(effectif);
    }

    public int getId() { return id.get(); }
    public void setId(int v) { id.set(v); }
    public IntegerProperty idProperty() { return id; }

    public String getNom() { return nom.get(); }
    public void setNom(String v) { nom.set(v); }
    public StringProperty nomProperty() { return nom; }

    public String getNiveau() { return niveau.get(); }
    public void setNiveau(String v) { niveau.set(v); }
    public StringProperty niveauProperty() { return niveau; }

    public int getEffectif() { return effectif.get(); }
    public void setEffectif(int v) { effectif.set(v); }
    public IntegerProperty effectifProperty() { return effectif; }

    @Override
    public String toString() {
        return nom.get();
    }

    /** Égalité par identifiant : permet aux ComboBox de retrouver l'élément sélectionné. */
    @Override
    public boolean equals(Object autre) {
        if (this == autre) {
            return true;
        }
        if (!(autre instanceof Classe)) {
            return false;
        }
        return getId() != 0 && getId() == ((Classe) autre).getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }
}
