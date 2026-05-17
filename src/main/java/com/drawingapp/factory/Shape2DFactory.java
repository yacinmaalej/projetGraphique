package com.drawingapp.factory;

import com.drawingapp.model.Shape;
import com.drawingapp.model2d.*;

public class Shape2DFactory implements ShapeFactory {

    @Override
    public Shape createShape(String shapeType, double startX, double startY, double endX, double endY) {
        return switch (shapeType.toUpperCase()) {
            case "RECTANGLE" -> new Rectangle2D(startX, startY, endX, endY);
            case "CIRCLE" -> new Circle2D(startX, startY, endX, endY);
            case "LINE" -> new Line2D(startX, startY, endX, endY);
            case "TRIANGLE" -> new Triangle2D(startX, startY, endX, endY);
            default -> throw new IllegalArgumentException("Forme 2D inconnue: " + shapeType);
        };
    }

    @Override
    public String getFactoryType() {
        return "2D";
    }

    @Override
    public String[] getAvailableShapes() {
        return new String[]{"RECTANGLE", "CIRCLE", "LINE", "TRIANGLE"};
    }
}