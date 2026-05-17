package com.drawingapp.model2d;

import com.drawingapp.model.Shape2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

/**
 * Ligne 2D — AMÉLIORÉE
 * 
 * - Ligne plus épaisse et propre
 * - Petits cercles aux extrémités
 * - Label discret au milieu
 */
public class Line2D implements Shape2D {
    private double startX, startY, endX, endY;
    private Color strokeColor;
    private Color fillColor;
    private double strokeWidth;

    public Line2D(double startX, double startY, double endX, double endY) {
        this.startX      = startX;
        this.startY      = startY;
        this.endX        = endX;
        this.endY        = endY;
        this.strokeColor = Color.rgb(60, 100, 180);
        this.fillColor   = Color.TRANSPARENT;
        this.strokeWidth = 2.5;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.save();

        // Ombre légère de la ligne
        gc.setStroke(Color.rgb(0, 0, 0, 0.15));
        gc.setLineWidth(strokeWidth + 2);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.strokeLine(startX + 2, startY + 2, endX + 2, endY + 2);

        // Ligne principale
        gc.setStroke(strokeColor);
        gc.setLineWidth(strokeWidth);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.strokeLine(startX, startY, endX, endY);

        // Cercles aux extrémités
        gc.setFill(strokeColor);
        gc.fillOval(startX - 4, startY - 4, 8, 8);
        gc.fillOval(endX   - 4, endY   - 4, 8, 8);

        // Points blancs à l'intérieur
        gc.setFill(Color.WHITE);
        gc.fillOval(startX - 2, startY - 2, 4, 4);
        gc.fillOval(endX   - 2, endY   - 2, 4, 4);

        // Label "2D" au milieu
        double midX = (startX + endX) / 2;
        double midY = (startY + endY) / 2;
        gc.setFill(Color.rgb(60, 100, 180, 0.7));
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 10));
        gc.fillText("2D", midX - 8, midY - 6);

        gc.restore();
    }

    @Override public double getArea()      { return 0; }
    @Override public double getPerimeter() {
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
        double len = getPerimeter();
        if (len == 0) return false;
        double dist = Math.abs((endY - startY) * x - (endX - startX) * y
            + endX * startY - endY * startX) / len;
        return dist < 5.0;
    }

    @Override public String getType()  { return "2D"; }
    @Override public double getStartX(){ return startX; }
    @Override public double getStartY(){ return startY; }
    @Override public double getEndX()  { return endX;   }
    @Override public double getEndY()  { return endY;   }

    @Override
    public String toString() {
        return String.format("Line2D[(%.0f,%.0f)→(%.0f,%.0f) | Longueur:%.0f]",
            startX, startY, endX, endY, getPerimeter());
    }
}
