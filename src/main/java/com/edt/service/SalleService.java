package com.edt.service;

import com.edt.dao.SalleDAO;
import com.edt.model.Salle;
import com.edt.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

public class SalleService {

    private final SalleDAO dao = new SalleDAO();

    public List<Salle> listerTout() throws SQLException {
        return dao.findAll();
    }

    public List<Salle> rechercher(String motCle) throws SQLException {
        if (motCle == null || motCle.trim().isEmpty()) {
            return listerTout();
        }
        return dao.search(motCle.trim());
    }

    public void ajouter(Salle s) throws SQLException {
        valider(s);
        dao.insert(s);
    }

    public void modifier(Salle s) throws SQLException {
        valider(s);
        dao.update(s);
    }

    public void supprimer(int id) throws SQLException {
        dao.delete(id);
    }

    public int compter() throws SQLException {
        return dao.count();
    }

    private void valider(Salle s) {
        ValidationUtil.requireNonEmpty(s.getNom(), "Nom");
        if (s.getCapacite() < 0) {
            throw new IllegalArgumentException("La capacité doit être positive.");
        }
    }
}
