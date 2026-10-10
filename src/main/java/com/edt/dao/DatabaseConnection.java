package com.edt.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gère la connexion JDBC unique vers la base de données MySQL (XAMPP).
 * Lit les paramètres depuis src/main/resources/db.properties.
 */
public final class DatabaseConnection {

    private static final String CONFIG_FILE = "/db.properties";
    private static Connection connection;

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            Properties props = loadProperties();
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = DatabaseConnection.class.getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                throw new IllegalStateException("Fichier de configuration introuvable : " + CONFIG_FILE);
            }
            props.load(is);
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de lire la configuration de la base de données.", e);
        }
        return props;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
                // Rien à faire à la fermeture de l'application
            }
        }
    }
}
