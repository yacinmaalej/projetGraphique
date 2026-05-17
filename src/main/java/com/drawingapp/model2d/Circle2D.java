package com.drawingapp.model2d;

import com.drawingapp.model.Shape2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;

/**
 * Cercle 2D — AMÉLIORÉ
 * 
 * - Dégradé radial (simulant un léger effet de lumière)
 * - Reflet blanc en haut à gauche
 * - Label discret
 */
public class Circle2D implements Shape2D {
    private double startX, startY, endX, endY;
    private Color strokeColor;
    private Color fillColor;
    private double strokeWidth;

    public Circle2D(double startX, double startY, double endX, double endY) {
        this.startX      = startX;
        this.startY      = startY;
        this.endX        = endX;
        this.endY        = endY;
        this.strokeColor = Color.rgb(60, 100, 180);
        this.fillColor   = Color.rgb(220, 235, 255, 0.85);
        this.strokeWidth = 1.5;
    }

    @Override
    public void draw(GraphicsContext gc) {
        double radius = Math.sqrt(
            Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2)
        );
        if (radius < 2) return;

        double x = startX - radius;
        double y = startY - radius;
        double d = radius * 2;

        gc.save();

        // Dégradé radial centré légèrement en haut-gauche
        double focusX = startX - radius * 0.25;
        double focusY = startY - radius * 0.25;

        RadialGradient grad = new RadialGradient(
            0, 0,
            (focusX - x) / d,   // focus X relatif (0..1)
            (focusY - y) / d,   // focus Y relatif
            0.75,               // rayon relatif
            true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.rgb(245, 250, 255, 0.98)),
            new Stop(0.6, Color.rgb(190, 215, 250, 0.88)),
            new Stop(1.0, Color.rgb(120, 170, 230, 0.80))
        );

        gc.setFill(fillColor.equals(Color.rgb(220, 235, 255, 0.85)) ? grad : fillColor);
        gc.fillOval(x, y, d, d);

        // Contour
        gc.setStroke(strokeColor);
        gc.setLineWidth(strokeWidth);
        gc.strokeOval(x, y, d, d);

        // Reflet blanc en haut à gauche
        gc.setFill(Color.rgb(255, 255, 255, 0.55));
        gc.fillOval(
            startX - radius * 0.55,
            startY - radius * 0.55,
            radius * 0.35,
            radius * 0.25
        );

        // Label "2D"
        gc.setFill(Color.rgb(60, 100, 180, 0.7));
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 10));
        gc.fillText("2D", startX - 8, startY + radius - 5);

        gc.restore();
    }

    @Override public double getArea()      { double r = radius(); return Math.PI * r * r; }
    @Override public double getPerimeter() { return 2 * Math.PI * radius(); }
    private double radius() {
        return Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
    }

    @Override
    public void setStyle(Color strokeColor, Color fillColor, double strokeWidth) {
        this.strokeColor = strokeColor;
        this.fillColor   = fillColor;
        this.strokeWidth = strokeWidth;
    }

    @Override
    public boolean contains(double x, double y) {
        return Math.sqrt(Math.pow(x - startX, 2) + Math.pow(y - startY, 2)) <= radius();
    }

    @Override public String getType()  { return "2D"; }
    @Override public double getStartX(){ return startX; }
    @Override public double getStartY(){ return startY; }
    @Override public double getEndX()  { return endX;   }
    @Override public double getEndY()  { return endY;   }

    @Override
    public String toString() {
        return String.format("Circle2D[centre:(%.0f,%.0f) | Aire:%.0f]",
            startX, startY, getArea());
    }
}
