package com.edt.model;

import javafx.beans.property.*;

public class Salle {

    private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private final StringProperty nom = new SimpleStringProperty(this, "nom");
    private final IntegerProperty capacite = new SimpleIntegerProperty(this, "capacite");
    private final StringProperty type = new SimpleStringProperty(this, "type");

    public Salle() {
    }

    public Salle(int id, String nom, int capacite, String type) {
        setId(id);
        setNom(nom);
        setCapacite(capacite);
        setType(type);
    }

    public int getId() { return id.get(); }
    public void setId(int v) { id.set(v); }
    public IntegerProperty idProperty() { return id; }

    public String getNom() { return nom.get(); }
    public void setNom(String v) { nom.set(v); }
    public StringProperty nomProperty() { return nom; }

    public int getCapacite() { return capacite.get(); }
    public void setCapacite(int v) { capacite.set(v); }
    public IntegerProperty capaciteProperty() { return capacite; }

    public String getType() { return type.get(); }
    public void setType(String v) { type.set(v); }
    public StringProperty typeProperty() { return type; }

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
        if (!(autre instanceof Salle)) {
            return false;
        }
        return getId() != 0 && getId() == ((Salle) autre).getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }
}
