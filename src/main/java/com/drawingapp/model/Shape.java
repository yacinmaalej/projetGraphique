package com.drawingapp.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public interface Shape {
    void draw(GraphicsContext gc);
    void setStyle(Color strokeColor, Color fillColor, double strokeWidth);
    boolean contains(double x, double y);
    String getType(); // "2D" ou "3D"

    default double getStartX() { return 0; }
    default double getStartY() { return 0; }
    default double getEndX() { return 0; }
    default double getEndY() { return 0; }
}