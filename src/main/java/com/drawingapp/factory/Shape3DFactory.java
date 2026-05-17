package com.drawingapp.factory;

import com.drawingapp.model.Shape;
import com.drawingapp.model3d.*;

public class Shape3DFactory implements ShapeFactory {

    @Override
    public Shape createShape(String shapeType, double startX, double startY, double endX, double endY) {
        return switch (shapeType.toUpperCase()) {
            case "CUBE" -> new Cube3D(startX, startY, endX, endY);
            case "SPHERE" -> new Sphere3D(startX, startY, endX, endY);
            case "CYLINDER" -> new Cylinder3D(startX, startY, endX, endY);
            case "PYRAMID" -> new Pyramid3D(startX, startY, endX, endY);
            default -> throw new IllegalArgumentException("Forme 3D inconnue: " + shapeType);
        };
    }

    @Override
    public String getFactoryType() {
        return "3D";
    }

    @Override
    public String[] getAvailableShapes() {
        return new String[]{"CUBE", "SPHERE", "CYLINDER", "PYRAMID"};
    }
}