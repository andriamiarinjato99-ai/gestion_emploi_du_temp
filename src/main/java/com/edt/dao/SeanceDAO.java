package com.edt.dao;

import com.edt.model.*;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SeanceDAO {

    private static final String SELECT_BASE =
            "SELECT s.id, s.jour, s.heure_debut, s.heure_fin, " +
            "       m.id AS matiere_id, m.code, m.nom AS matiere_nom, m.volume_horaire, " +
            "       e.id AS enseignant_id, e.nom AS ens_nom, e.prenom, e.email, e.telephone, e.specialite, " +
            "       c.id AS classe_id, c.nom AS classe_nom, c.niveau, c.effectif, " +
            "       sa.id AS salle_id, sa.nom AS salle_nom, sa.capacite, sa.type " +
            "FROM seance s " +
            "JOIN matiere m ON s.matiere_id = m.id " +
            "JOIN enseignant e ON s.enseignant_id = e.id " +
            "JOIN classe c ON s.classe_id = c.id " +
            "JOIN salle sa ON s.salle_id = sa.id ";

    public List<Seance> findAll() throws SQLException {
        String sql = SELECT_BASE + "ORDER BY FIELD(s.jour,'LUNDI','MARDI','MERCREDI','JEUDI','VENDREDI','SAMEDI'), s.heure_debut";
        List<Seance> liste = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(map(rs));
            }
        }
        return liste;
    }

    public List<Seance> search(String motCle) throws SQLException {
        String sql = SELECT_BASE +
                "WHERE m.nom LIKE ? OR e.nom LIKE ? OR e.prenom LIKE ? OR c.nom LIKE ? OR sa.nom LIKE ? OR s.jour LIKE ? " +
                "ORDER BY FIELD(s.jour,'LUNDI','MARDI','MERCREDI','JEUDI','VENDREDI','SAMEDI'), s.heure_debut";
        List<Seance> liste = new ArrayList<>();
        String like = "%" + motCle + "%";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            for (int i = 1; i <= 6; i++) {
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

    /**
     * Vérifie si un créneau chevauche un créneau existant pour le même enseignant,
     * la même classe ou la même salle, le même jour (hors la séance elle-même en cas de modification).
     */
    public boolean existeConflit(Seance seance) throws SQLException {
        String sql = SELECT_BASE +
                "WHERE s.jour = ? AND s.id <> ? " +
                "AND (s.enseignant_id = ? OR s.classe_id = ? OR s.salle_id = ?) " +
                "AND s.heure_debut < ? AND s.heure_fin > ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, seance.getJour().name());
            ps.setInt(2, seance.getId()); // 0 si nouvelle séance : ne correspond à aucun id existant
            ps.setInt(3, seance.getEnseignant().getId());
            ps.setInt(4, seance.getClasse().getId());
            ps.setInt(5, seance.getSalle().getId());
            ps.setTime(6, Time.valueOf(seance.getHeureFin()));
            ps.setTime(7, Time.valueOf(seance.getHeureDebut()));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void insert(Seance s) throws SQLException {
        String sql = "INSERT INTO seance (matiere_id, enseignant_id, classe_id, salle_id, jour, heure_debut, heure_fin) " +
                "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillStatement(ps, s);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    s.setId(keys.getInt(1));
                }
            }
        }
    }

    public void update(Seance s) throws SQLException {
        String sql = "UPDATE seance SET matiere_id=?, enseignant_id=?, classe_id=?, salle_id=?, jour=?, " +
                "heure_debut=?, heure_fin=? WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            fillStatement(ps, s);
            ps.setInt(8, s.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM seance WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM seance";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Matière la plus souvent assurée par un enseignant, d'après l'historique
     * des séances déjà saisies. Sert de solution de repli quand aucune matière
     * n'est encore rattachée à sa fiche.
     *
     * @return la matière trouvée, ou null si l'enseignant n'a encore aucune séance.
     */
    public Matiere findMatierePrincipale(int enseignantId) throws SQLException {
        String sql = "SELECT m.id, m.code, m.nom, m.volume_horaire, COUNT(*) AS total " +
                "FROM seance s JOIN matiere m ON s.matiere_id = m.id " +
                "WHERE s.enseignant_id = ? " +
                "GROUP BY m.id, m.code, m.nom, m.volume_horaire " +
                "ORDER BY total DESC, m.nom LIMIT 1";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, enseignantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Matiere(rs.getInt("id"), rs.getString("code"),
                            rs.getString("nom"), rs.getInt("volume_horaire"));
                }
            }
        }
        return null;
    }

    /** Nombre de séances par jour de la semaine, pour le dashboard. */
    public List<Object[]> countByJour() throws SQLException {
        String sql = "SELECT jour, COUNT(*) FROM seance GROUP BY jour " +
                "ORDER BY FIELD(jour,'LUNDI','MARDI','MERCREDI','JEUDI','VENDREDI','SAMEDI')";
        List<Object[]> resultat = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                resultat.add(new Object[]{rs.getString(1), rs.getInt(2)});
            }
        }
        return resultat;
    }

    private void fillStatement(PreparedStatement ps, Seance s) throws SQLException {
        ps.setInt(1, s.getMatiere().getId());
        ps.setInt(2, s.getEnseignant().getId());
        ps.setInt(3, s.getClasse().getId());
        ps.setInt(4, s.getSalle().getId());
        ps.setString(5, s.getJour().name());
        ps.setTime(6, Time.valueOf(s.getHeureDebut()));
        ps.setTime(7, Time.valueOf(s.getHeureFin()));
    }

    private Seance map(ResultSet rs) throws SQLException {
        Matiere matiere = new Matiere(rs.getInt("matiere_id"), rs.getString("code"),
                rs.getString("matiere_nom"), rs.getInt("volume_horaire"));
        Enseignant enseignant = new Enseignant(rs.getInt("enseignant_id"), rs.getString("ens_nom"),
                rs.getString("prenom"), rs.getString("email"), rs.getString("telephone"), rs.getString("specialite"));
        Classe classe = new Classe(rs.getInt("classe_id"), rs.getString("classe_nom"),
                rs.getString("niveau"), rs.getInt("effectif"));
        Salle salle = new Salle(rs.getInt("salle_id"), rs.getString("salle_nom"),
                rs.getInt("capacite"), rs.getString("type"));

        LocalTime debut = rs.getTime("heure_debut").toLocalTime();
        LocalTime fin = rs.getTime("heure_fin").toLocalTime();
        Jour jour = Jour.valueOf(rs.getString("jour"));

        return new Seance(rs.getInt("id"), matiere, enseignant, classe, salle, jour, debut, fin);
    }
}
