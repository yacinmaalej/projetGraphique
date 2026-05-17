package com.drawingapp.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogRepository {
    private static LogRepository instance;

    private LogRepository() {}

    public static LogRepository getInstance() {
        if (instance == null) {
            instance = new LogRepository();
        }
        return instance;
    }

    public List<String> getRecentLogs(int limit) {
        List<String> logs = new ArrayList<>();
        DatabaseConnection db = DatabaseConnection.getInstance();
        if (!db.isConnected()) return logs;

        String sql = "SELECT timestamp, action FROM logs ORDER BY id DESC LIMIT ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String timestamp = rs.getTimestamp("timestamp").toString();
                String action = rs.getString("action");
                logs.add("[" + timestamp + "] " + action);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur chargement logs: " + e.getMessage());
        }

        return logs;
    }

    public void clearLogs() {
        DatabaseConnection db = DatabaseConnection.getInstance();
        if (!db.isConnected()) return;

        try {
            db.getConnection().createStatement().executeUpdate("DELETE FROM logs");
            System.out.println("🗑 Logs effacés de la base de données");
        } catch (SQLException e) {
            System.err.println("❌ Erreur effacement logs: " + e.getMessage());
        }
    }
}