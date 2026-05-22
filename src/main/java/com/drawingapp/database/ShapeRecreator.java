package com.drawingapp.database;

import com.drawingapp.model.Shape;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShapeRecreator {
    private static ShapeRecreator instance;
    private Gson gson;

    private ShapeRecreator() {
        this.gson = new Gson();
    }

    public static ShapeRecreator getInstance() {
        if (instance == null) {
            instance = new ShapeRecreator();
        }
        return instance;
    }

    // Reconstruire les formes à partir du JSON
    public List<Shape> recreateShapes(String jsonData) {
        List<Shape> shapes = new ArrayList<>();

        if (jsonData == null || jsonData.isEmpty()) {
            return shapes;
        }

        try {
            Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> shapesData = gson.fromJson(jsonData, listType);

            for (Map<String, Object> shapeData : shapesData) {
                Shape shape = createShapeFromData(shapeData);
                if (shape != null) {
                    shapes.add(shape);
                }
            }

            System.out.println(" " + shapes.size() + " forme(s) reconstruite(s) depuis le JSON");
        } catch (Exception e) {
            System.err.println(" Erreur reconstruction formes: " + e.getMessage());
            e.printStackTrace();
        }

        return shapes;
    }

    private Shape createShapeFromData(Map<String, Object> data) {
        try {
            String className = (String) data.get("className");
            double startX = getDoubleValue(data, "startX");
            double startY = getDoubleValue(data, "startY");
            double endX = getDoubleValue(data, "endX");
            double endY = getDoubleValue(data, "endY");

            // Utiliser la réflexion pour créer la forme
            Class<?> clazz = Class.forName(className);
            return (Shape) clazz.getConstructor(double.class, double.class, double.class, double.class)
                    .newInstance(startX, startY, endX, endY);

        } catch (Exception e) {
            System.err.println(" Erreur création forme: " + e.getMessage());
            return null;
        }
    }

    private double getDoubleValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }
}