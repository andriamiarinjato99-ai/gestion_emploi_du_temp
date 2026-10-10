package com.edt.dao;

import com.edt.model.Classe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClasseDAO {

    public List<Classe> findAll() throws SQLException {
        String sql = "SELECT * FROM classe ORDER BY nom";
        List<Classe> liste = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(map(rs));
            }
        }
        return liste;
    }

    public List<Classe> search(String motCle) throws SQLException {
        String sql = "SELECT * FROM classe WHERE nom LIKE ? OR niveau LIKE ? ORDER BY nom";
        List<Classe> liste = new ArrayList<>();
        String like = "%" + motCle + "%";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(map(rs));
                }
            }
        }
        return liste;
    }

    public void insert(Classe c) throws SQLException {
        String sql = "INSERT INTO classe (nom, niveau, effectif) VALUES (?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNom());
            ps.setString(2, c.getNiveau());
            ps.setInt(3, c.getEffectif());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    c.setId(keys.getInt(1));
                }
            }
        }
    }

    public void update(Classe c) throws SQLException {
        String sql = "UPDATE classe SET nom=?, niveau=?, effectif=? WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getNom());
            ps.setString(2, c.getNiveau());
            ps.setInt(3, c.getEffectif());
            ps.setInt(4, c.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM classe WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM classe";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Classe map(ResultSet rs) throws SQLException {
        return new Classe(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("niveau"),
                rs.getInt("effectif")
        );
    }
}
