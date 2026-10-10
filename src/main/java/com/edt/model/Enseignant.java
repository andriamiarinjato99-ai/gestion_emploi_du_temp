package com.edt.model;

import javafx.beans.property.*;

public class Enseignant {

    private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private final StringProperty nom = new SimpleStringProperty(this, "nom");
    private final StringProperty prenom = new SimpleStringProperty(this, "prenom");
    private final StringProperty email = new SimpleStringProperty(this, "email");
    private final StringProperty telephone = new SimpleStringProperty(this, "telephone");
    private final StringProperty specialite = new SimpleStringProperty(this, "specialite");

    /** Matière principale enseignée (peut être nulle). Sert au remplissage automatique. */
    private final ObjectProperty<Matiere> matiere = new SimpleObjectProperty<>(this, "matiere");

    public Enseignant() {
    }

    public Enseignant(int id, String nom, String prenom, String email, String telephone, String specialite) {
        this(id, nom, prenom, email, telephone, specialite, null);
    }

    public Enseignant(int id, String nom, String prenom, String email, String telephone,
                      String specialite, Matiere matiere) {
        setId(id);
        setNom(nom);
        setPrenom(prenom);
        setEmail(email);
        setTelephone(telephone);
        setSpecialite(specialite);
        setMatiere(matiere);
    }

    public int getId() { return id.get(); }
    public void setId(int v) { id.set(v); }
    public IntegerProperty idProperty() { return id; }

    public String getNom() { return nom.get(); }
    public void setNom(String v) { nom.set(v); }
    public StringProperty nomProperty() { return nom; }

    public String getPrenom() { return prenom.get(); }
    public void setPrenom(String v) { prenom.set(v); }
    public StringProperty prenomProperty() { return prenom; }

    public String getEmail() { return email.get(); }
    public void setEmail(String v) { email.set(v); }
    public StringProperty emailProperty() { return email; }

    public String getTelephone() { return telephone.get(); }
    public void setTelephone(String v) { telephone.set(v); }
    public StringProperty telephoneProperty() { return telephone; }

    public String getSpecialite() { return specialite.get(); }
    public void setSpecialite(String v) { specialite.set(v); }
    public StringProperty specialiteProperty() { return specialite; }

    public Matiere getMatiere() { return matiere.get(); }
    public void setMatiere(Matiere v) { matiere.set(v); }
    public ObjectProperty<Matiere> matiereProperty() { return matiere; }

    /** Identifiant de la matière enseignée, ou 0 si aucune matière n'est rattachée. */
    public int getMatiereId() {
        return getMatiere() == null ? 0 : getMatiere().getId();
    }

    /** Libellé de la matière enseignée, pour l'affichage dans le tableau. */
    public String getMatiereNom() {
        return getMatiere() == null ? "" : getMatiere().getNom();
    }

    @Override
    public String toString() {
        return prenom.get() + " " + nom.get();
    }

    /**
     * Deux enseignants sont identiques s'ils ont le même id.
     * Indispensable pour que les ComboBox re-sélectionnent la bonne ligne
     * lorsqu'un objet rechargé depuis la base est réinjecté dans le formulaire.
     */
    @Override
    public boolean equals(Object autre) {
        if (this == autre) {
            return true;
        }
        if (!(autre instanceof Enseignant)) {
            return false;
        }
        return getId() != 0 && getId() == ((Enseignant) autre).getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }
}
