package com.edt.service;

import com.edt.dao.MatiereDAO;
import com.edt.model.Matiere;
import com.edt.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

public class MatiereService {

    private final MatiereDAO dao = new MatiereDAO();

    public List<Matiere> listerTout() throws SQLException {
        return dao.findAll();
    }

    public List<Matiere> rechercher(String motCle) throws SQLException {
        if (motCle == null || motCle.trim().isEmpty()) {
            return listerTout();
        }
        return dao.search(motCle.trim());
    }

    public void ajouter(Matiere m) throws SQLException {
        valider(m);
        dao.insert(m);
    }

    public void modifier(Matiere m) throws SQLException {
        valider(m);
        dao.update(m);
    }

    public void supprimer(int id) throws SQLException {
        dao.delete(id);
    }

    public int compter() throws SQLException {
        return dao.count();
    }

    private void valider(Matiere m) {
        ValidationUtil.requireNonEmpty(m.getCode(), "Code");
        ValidationUtil.requireNonEmpty(m.getNom(), "Nom");
        if (m.getVolumeHoraire() < 0) {
            throw new IllegalArgumentException("Le volume horaire doit être positif.");
        }
    }
}
