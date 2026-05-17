package com.drawingapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/drawingapp";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Mot de passe XAMPP (vide par défaut)

    private DatabaseConnection() {
        try {
            // Charger le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            createDatabaseIfNotExists();
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion MySQL réussie !");
            createTables();
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL non trouvé: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Erreur connexion MySQL: " + e.getMessage());
            System.err.println("Vérifiez que XAMPP MySQL est démarré !");
        }
    }

    private void createDatabaseIfNotExists() {
        try (Connection tempConn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/", USER, PASSWORD)) {
            tempConn.createStatement().executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS drawingapp"
            );
            System.out.println("✅ Base de données 'drawingapp' vérifiée/créée");
        } catch (SQLException e) {
            System.err.println("❌ Erreur création base de données: " + e.getMessage());
        }
    }

    private void createTables() {
        String createLogsTable = """
            CREATE TABLE IF NOT EXISTS logs (
                id INT AUTO_INCREMENT PRIMARY KEY,
                timestamp DATETIME NOT NULL,
                action VARCHAR(500) NOT NULL,
                strategy VARCHAR(20) DEFAULT 'DATABASE'
            )
        """;

        String createDrawingsTable = """
            CREATE TABLE IF NOT EXISTS drawings (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(200) NOT NULL,
                shapes_data TEXT NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
        """;

        try {
            connection.createStatement().executeUpdate(createLogsTable);
            connection.createStatement().executeUpdate(createDrawingsTable);
            System.out.println("✅ Tables 'logs' et 'drawings' vérifiées/créées");
        } catch (SQLException e) {
            System.err.println("❌ Erreur création tables: " + e.getMessage());
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Connexion MySQL fermée");
            }
        } catch (SQLException e) {
            System.err.println("Erreur fermeture connexion: " + e.getMessage());
        }
    }
}