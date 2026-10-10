package com.edt.dao;

import com.edt.model.Matiere;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatiereDAO {

    public List<Matiere> findAll() throws SQLException {
        String sql = "SELECT * FROM matiere ORDER BY nom";
        List<Matiere> liste = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(map(rs));
            }
        }
        return liste;
    }

    public List<Matiere> search(String motCle) throws SQLException {
        String sql = "SELECT * FROM matiere WHERE code LIKE ? OR nom LIKE ? ORDER BY nom";
        List<Matiere> liste = new ArrayList<>();
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

    public void insert(Matiere m) throws SQLException {
        String sql = "INSERT INTO matiere (code, nom, volume_horaire) VALUES (?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getCode());
            ps.setString(2, m.getNom());
            ps.setInt(3, m.getVolumeHoraire());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    m.setId(keys.getInt(1));
                }
            }
        }
    }

    public void update(Matiere m) throws SQLException {
        String sql = "UPDATE matiere SET code=?, nom=?, volume_horaire=? WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, m.getCode());
            ps.setString(2, m.getNom());
            ps.setInt(3, m.getVolumeHoraire());
            ps.setInt(4, m.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM matiere WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM matiere";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Matiere map(ResultSet rs) throws SQLException {
        return new Matiere(
                rs.getInt("id"),
                rs.getString("code"),
                rs.getString("nom"),
                rs.getInt("volume_horaire")
        );
    }
}
