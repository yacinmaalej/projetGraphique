package com.drawingapp.factory;

import com.drawingapp.model.Shape;

public interface ShapeFactory {
    Shape createShape(String shapeType, double startX, double startY, double endX, double endY);
    String getFactoryType(); // "2D" ou "3D"
    String[] getAvailableShapes();
}