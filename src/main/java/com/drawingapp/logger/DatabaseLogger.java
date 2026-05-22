package com.drawingapp.logger;

import com.drawingapp.database.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatabaseLogger implements Logger {
    private static volatile DatabaseLogger instance;
    private LoggingStrategy loggingStrategy;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DatabaseLogger() {
        this.loggingStrategy = LoggingStrategy.DATABASE;
        System.out.println(" DatabaseLogger initialisé");
    }

    public static DatabaseLogger getInstance() {
        if (instance == null) {
            synchronized (DatabaseLogger.class) {
                if (instance == null) {
                    instance = new DatabaseLogger();
                }
            }
        }
        return instance;
    }

    @Override
    public void log(String action) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(formatter);

        System.out.println("[DB-LOG] [" + timestamp + "] " + action); // ← Toujours afficher dans la console pour debug

        switch (loggingStrategy) {
            case DATABASE:
                saveToDatabase(now, action); // ← Passer LocalDateTime, pas String
                break;
            case CONSOLE:
                System.out.println("[DB-LOG] [" + timestamp + "] " + action);
                break;
            case FILE:
                FileLogger.getInstance().log("[DB] " + action);
                break;
        }
    }

    private void saveToDatabase(LocalDateTime dateTime, String action) {
        DatabaseConnection db = DatabaseConnection.getInstance();
        if (!db.isConnected()) {
            System.err.println("⚠ Base de données non connectée, log en console: " + action);
            return;
        }

        String sql = "INSERT INTO logs (timestamp, action, strategy) VALUES (?, ?, 'DATABASE')";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(dateTime)); // ← Convertir en Timestamp SQL
            stmt.setString(2, action);
            int rows = stmt.executeUpdate();
            System.out.println(" Log inséré en DB: " + action + " (rows: " + rows + ")");
        } catch (SQLException e) {
            System.err.println(" Erreur sauvegarde log en DB: " + e.getMessage());
            e.printStackTrace(); // ← Afficher la stack trace complète
        }
    }

    @Override
    public void setLoggingStrategy(LoggingStrategy strategy) {
        this.loggingStrategy = strategy;
        log("Stratégie de logging changée vers: " + strategy);
    }
}