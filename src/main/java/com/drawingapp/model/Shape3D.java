package com.drawingapp.model;

import javafx.scene.canvas.GraphicsContext;

public interface Shape3D extends Shape {
    double getVolume();
    double getSurfaceArea();
    String get3DRepresentation();
}