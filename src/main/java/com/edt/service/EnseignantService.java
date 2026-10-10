package com.edt.service;

import com.edt.dao.EnseignantDAO;
import com.edt.model.Enseignant;
import com.edt.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * Couche métier : valide les données puis délègue à la DAO.
 * Les contrôleurs ne parlent jamais directement à la DAO.
 */
public class EnseignantService {

    private final EnseignantDAO dao = new EnseignantDAO();

    public List<Enseignant> listerTout() throws SQLException {
        return dao.findAll();
    }

    public List<Enseignant> rechercher(String motCle) throws SQLException {
        if (motCle == null || motCle.trim().isEmpty()) {
            return listerTout();
        }
        return dao.search(motCle.trim());
    }

    public void ajouter(Enseignant e) throws SQLException {
        valider(e);
        dao.insert(e);
    }

    public void modifier(Enseignant e) throws SQLException {
        valider(e);
        dao.update(e);
    }

    public void supprimer(int id) throws SQLException {
        dao.delete(id);
    }

    public int compter() throws SQLException {
        return dao.count();
    }

    private void valider(Enseignant e) {
        ValidationUtil.requireNonEmpty(e.getNom(), "Nom");
        ValidationUtil.requireEmail(e.getEmail(), "Email");
        ValidationUtil.requirePhone(e.getTelephone(), "Téléphone");
        ValidationUtil.requireSelection(e.getMatiere(), "Matière enseignée");
    }
}
