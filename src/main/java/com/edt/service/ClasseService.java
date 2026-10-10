package com.edt.service;

import com.edt.dao.ClasseDAO;
import com.edt.model.Classe;
import com.edt.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

public class ClasseService {

    private final ClasseDAO dao = new ClasseDAO();

    public List<Classe> listerTout() throws SQLException {
        return dao.findAll();
    }

    public List<Classe> rechercher(String motCle) throws SQLException {
        if (motCle == null || motCle.trim().isEmpty()) {
            return listerTout();
        }
        return dao.search(motCle.trim());
    }

    public void ajouter(Classe c) throws SQLException {
        valider(c);
        dao.insert(c);
    }

    public void modifier(Classe c) throws SQLException {
        valider(c);
        dao.update(c);
    }

    public void supprimer(int id) throws SQLException {
        dao.delete(id);
    }

    public int compter() throws SQLException {
        return dao.count();
    }

    private void valider(Classe c) {
        ValidationUtil.requireNonEmpty(c.getNom(), "Nom");
        if (c.getEffectif() < 0) {
            throw new IllegalArgumentException("L'effectif doit être positif.");
        }
    }
}
