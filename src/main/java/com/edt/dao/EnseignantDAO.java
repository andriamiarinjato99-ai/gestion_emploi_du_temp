package com.edt.dao;

import com.edt.model.Enseignant;
import com.edt.model.Matiere;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnseignantDAO {

    /**
     * LEFT JOIN : un enseignant peut ne pas encore avoir de matière rattachée.
     * Les colonnes de la matière sont préfixées "mat_" pour éviter toute collision.
     */
    private static final String SELECT_BASE =
            "SELECT e.id, e.nom, e.prenom, e.email, e.telephone, e.specialite, e.matiere_id, " +
            "       m.code AS mat_code, m.nom AS mat_nom, m.volume_horaire AS mat_volume " +
            "FROM enseignant e " +
            "LEFT JOIN matiere m ON e.matiere_id = m.id ";

    public List<Enseignant> findAll() throws SQLException {
        String sql = SELECT_BASE + "ORDER BY e.nom, e.prenom";
        List<Enseignant> liste = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(map(rs));
            }
        }
        return liste;
    }

    public List<Enseignant> search(String motCle) throws SQLException {
        String sql = SELECT_BASE +
                "WHERE e.nom LIKE ? OR e.prenom LIKE ? OR e.email LIKE ? " +
                "OR e.specialite LIKE ? OR m.nom LIKE ? ORDER BY e.nom, e.prenom";
        List<Enseignant> liste = new ArrayList<>();
        String like = "%" + motCle + "%";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) {
                ps.setString(i, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(map(rs));
                }
            }
        }
        return liste;
    }

    /** Retrouve un enseignant (avec sa matière) à partir de son identifiant. */
    public Enseignant findById(int id) throws SQLException {
        String sql = SELECT_BASE + "WHERE e.id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public void insert(Enseignant e) throws SQLException {
        String sql = "INSERT INTO enseignant (nom, prenom, email, telephone, specialite, matiere_id) " +
                "VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            remplir(ps, e);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getInt(1));
                }
            }
        }
    }

    public void update(Enseignant e) throws SQLException {
        String sql = "UPDATE enseignant SET nom=?, prenom=?, email=?, telephone=?, specialite=?, " +
                "matiere_id=? WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            remplir(ps, e);
            ps.setInt(7, e.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM enseignant WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM enseignant";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void remplir(PreparedStatement ps, Enseignant e) throws SQLException {
        ps.setString(1, e.getNom());
        ps.setString(2, e.getPrenom());
        ps.setString(3, e.getEmail());
        ps.setString(4, e.getTelephone());
        ps.setString(5, e.getSpecialite());
        if (e.getMatiere() == null) {
            ps.setNull(6, Types.INTEGER);
        } else {
            ps.setInt(6, e.getMatiere().getId());
        }
    }

    private Enseignant map(ResultSet rs) throws SQLException {
        int matiereId = rs.getInt("matiere_id");
        Matiere matiere = null;
        if (!rs.wasNull() && matiereId > 0) {
            matiere = new Matiere(matiereId, rs.getString("mat_code"),
                    rs.getString("mat_nom"), rs.getInt("mat_volume"));
        }
        return new Enseignant(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                rs.getString("telephone"),
                rs.getString("specialite"),
                matiere
        );
    }
}
