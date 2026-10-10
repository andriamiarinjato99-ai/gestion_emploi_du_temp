package com.edt.model;

import javafx.beans.property.*;

import java.time.LocalTime;

public class Seance {

    private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private final ObjectProperty<Matiere> matiere = new SimpleObjectProperty<>(this, "matiere");
    private final ObjectProperty<Enseignant> enseignant = new SimpleObjectProperty<>(this, "enseignant");
    private final ObjectProperty<Classe> classe = new SimpleObjectProperty<>(this, "classe");
    private final ObjectProperty<Salle> salle = new SimpleObjectProperty<>(this, "salle");
    private final ObjectProperty<Jour> jour = new SimpleObjectProperty<>(this, "jour");
    private final ObjectProperty<LocalTime> heureDebut = new SimpleObjectProperty<>(this, "heureDebut");
    private final ObjectProperty<LocalTime> heureFin = new SimpleObjectProperty<>(this, "heureFin");

    public Seance() {
    }

    public Seance(int id, Matiere matiere, Enseignant enseignant, Classe classe, Salle salle,
                  Jour jour, LocalTime heureDebut, LocalTime heureFin) {
        setId(id);
        setMatiere(matiere);
        setEnseignant(enseignant);
        setClasse(classe);
        setSalle(salle);
        setJour(jour);
        setHeureDebut(heureDebut);
        setHeureFin(heureFin);
    }

    public int getId() { return id.get(); }
    public void setId(int v) { id.set(v); }
    public IntegerProperty idProperty() { return id; }

    public Matiere getMatiere() { return matiere.get(); }
    public void setMatiere(Matiere v) { matiere.set(v); }
    public ObjectProperty<Matiere> matiereProperty() { return matiere; }

    public Enseignant getEnseignant() { return enseignant.get(); }
    public void setEnseignant(Enseignant v) { enseignant.set(v); }
    public ObjectProperty<Enseignant> enseignantProperty() { return enseignant; }

    public Classe getClasse() { return classe.get(); }
    public void setClasse(Classe v) { classe.set(v); }
    public ObjectProperty<Classe> classeProperty() { return classe; }

    public Salle getSalle() { return salle.get(); }
    public void setSalle(Salle v) { salle.set(v); }
    public ObjectProperty<Salle> salleProperty() { return salle; }

    public Jour getJour() { return jour.get(); }
    public void setJour(Jour v) { jour.set(v); }
    public ObjectProperty<Jour> jourProperty() { return jour; }

    public LocalTime getHeureDebut() { return heureDebut.get(); }
    public void setHeureDebut(LocalTime v) { heureDebut.set(v); }
    public ObjectProperty<LocalTime> heureDebutProperty() { return heureDebut; }

    public LocalTime getHeureFin() { return heureFin.get(); }
    public void setHeureFin(LocalTime v) { heureFin.set(v); }
    public ObjectProperty<LocalTime> heureFinProperty() { return heureFin; }
}
