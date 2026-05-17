package com.drawingapp.database;

import com.drawingapp.model.Shape;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class DrawingRepository {
    private static DrawingRepository instance;
    private Gson gson;

    private DrawingRepository() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    public static DrawingRepository getInstance() {
        if (instance == null) {
            instance = new DrawingRepository();
        }
        return instance;
    }

    // Sauvegarder un dessin
    public boolean saveDrawing(String name, List<Shape> shapes) {
        DatabaseConnection db = DatabaseConnection.getInstance();
        if (!db.isConnected()) {
            System.err.println("❌ Base de données non connectée");
            return false;
        }

        // Sérialiser les formes en JSON avec TOUTES les informations nécessaires
        List<Map<String, Object>> shapesData = new ArrayList<>();
        for (Shape shape : shapes) {
            Map<String, Object> shapeMap = new HashMap<>();

            // ★★★ Si c'est un Adapter 3D, sauvegarder la forme ORIGINALE ★★★
            Shape shapeToSave = shape;
            if (shape instanceof com.drawingapp.adapter.Shape3DAdapter) {
                shapeToSave = ((com.drawingapp.adapter.Shape3DAdapter) shape).getOriginalShape();
            }

            shapeMap.put("className", shapeToSave.getClass().getName());
            shapeMap.put("type", shapeToSave.getType());
            shapeMap.put("startX", shapeToSave.getStartX());
            shapeMap.put("startY", shapeToSave.getStartY());
            shapeMap.put("endX", shapeToSave.getEndX());
            shapeMap.put("endY", shapeToSave.getEndY());
            shapesData.add(shapeMap);
        }

        String shapesJson = gson.toJson(shapesData);

        String sql = "INSERT INTO drawings (name, shapes_data, created_at) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE shapes_data = ?, updated_at = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            stmt.setString(1, name);
            stmt.setString(2, shapesJson);
            stmt.setTimestamp(3, now);
            stmt.setString(4, shapesJson);
            stmt.setTimestamp(5, now);

            int rows = stmt.executeUpdate();
            System.out.println("✅ Dessin sauvegardé: " + name + " (" + shapes.size() + " formes)");
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur sauvegarde dessin: " + e.getMessage());
            return false;
        }
    }

    // Charger tous les noms de dessins sauvegardés
    public Map<Integer, String> getAllDrawingNames() {
        Map<Integer, String> drawings = new LinkedHashMap<>(); // LinkedHashMap pour garder l'ordre
        DatabaseConnection db = DatabaseConnection.getInstance();
        if (!db.isConnected()) return drawings;

        String sql = "SELECT id, name, created_at FROM drawings ORDER BY created_at DESC";

        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String date = rs.getTimestamp("created_at").toLocalDateTime()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                drawings.put(id, name + " [" + date + "]");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur chargement noms dessins: " + e.getMessage());
        }

        return drawings;
    }

    // Charger les données JSON d'un dessin
    public String loadDrawingJson(int drawingId) {
        DatabaseConnection db = DatabaseConnection.getInstance();
        if (!db.isConnected()) return null;

        String sql = "SELECT shapes_data FROM drawings WHERE id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, drawingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("shapes_data");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur chargement dessin: " + e.getMessage());
        }

        return null;
    }

    // Supprimer un dessin
    public boolean deleteDrawing(int drawingId) {
        DatabaseConnection db = DatabaseConnection.getInstance();
        if (!db.isConnected()) return false;

        String sql = "DELETE FROM drawings WHERE id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, drawingId);
            int rows = stmt.executeUpdate();
            System.out.println("🗑 Dessin #" + drawingId + " supprimé");
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression dessin: " + e.getMessage());
            return false;
        }
    }
}