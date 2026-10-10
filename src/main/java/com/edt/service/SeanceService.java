package com.edt.service;

import com.edt.dao.SeanceDAO;
import com.edt.model.Seance;
import com.edt.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

public class SeanceService {

    private final SeanceDAO dao = new SeanceDAO();

    public List<Seance> listerTout() throws SQLException {
        return dao.findAll();
    }

    public List<Seance> rechercher(String motCle) throws SQLException {
        if (motCle == null || motCle.trim().isEmpty()) {
            return listerTout();
        }
        return dao.search(motCle.trim());
    }

    public void ajouter(Seance s) throws SQLException {
        valider(s);
        if (dao.existeConflit(s)) {
            throw new IllegalArgumentException(
                    "Conflit détecté : l'enseignant, la classe ou la salle est déjà occupé(e) sur ce créneau.");
        }
        dao.insert(s);
    }

    public void modifier(Seance s) throws SQLException {
        valider(s);
        if (dao.existeConflit(s)) {
            throw new IllegalArgumentException(
                    "Conflit détecté : l'enseignant, la classe ou la salle est déjà occupé(e) sur ce créneau.");
        }
        dao.update(s);
    }

    public void supprimer(int id) throws SQLException {
        dao.delete(id);
    }

    public int compter() throws SQLException {
        return dao.count();
    }

    /**
     * Matière habituellement assurée par un enseignant (d'après ses séances passées).
     * Utilisée par l'écran Séance pour pré-remplir la matière.
     */
    public com.edt.model.Matiere matiereHabituelle(int enseignantId) throws SQLException {
        return dao.findMatierePrincipale(enseignantId);
    }

    public List<Object[]> statistiquesParJour() throws SQLException {
        return dao.countByJour();
    }

    private void valider(Seance s) {
        ValidationUtil.requireSelection(s.getMatiere(), "Matière");
        ValidationUtil.requireSelection(s.getEnseignant(), "Enseignant");
        ValidationUtil.requireSelection(s.getClasse(), "Classe");
        ValidationUtil.requireSelection(s.getSalle(), "Salle");
        ValidationUtil.requireSelection(s.getJour(), "Jour");
        if (s.getHeureDebut() == null) {
            throw new IllegalArgumentException("L'heure de début est obligatoire.");
        }
        if (s.getHeureFin() == null) {
            throw new IllegalArgumentException("L'heure de fin est obligatoire.");
        }
        ValidationUtil.requireEndAfterStart(s.getHeureDebut(), s.getHeureFin());
    }
}
