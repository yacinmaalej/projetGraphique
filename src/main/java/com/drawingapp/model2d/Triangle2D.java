package com.drawingapp.model2d;

import com.drawingapp.model.Shape2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;

/**
 * Triangle 2D — AMÉLIORÉ
 * 
 * - Dégradé du haut (clair) vers le bas (légèrement plus foncé)
 * - Contour propre
 * - Label discret en bas
 */
public class Triangle2D implements Shape2D {
    private double startX, startY, endX, endY;
    private Color strokeColor;
    private Color fillColor;
    private double strokeWidth;

    public Triangle2D(double startX, double startY, double endX, double endY) {
        this.startX      = startX;
        this.startY      = startY;
        this.endX        = endX;
        this.endY        = endY;
        this.strokeColor = Color.rgb(60, 100, 180);
        this.fillColor   = Color.rgb(220, 235, 255, 0.85);
        this.strokeWidth = 1.5;
    }

    /** Points du triangle : sommet en haut-centre, base en bas */
    private double[][] points() {
        double topX   = (startX + endX) / 2;
        double topY   = Math.min(startY, endY);
        double baseY  = Math.max(startY, endY);
        double left   = Math.min(startX, endX);
        double right  = Math.max(startX, endX);
        return new double[][]{
            {topX, left, right},
            {topY, baseY, baseY}
        };
    }

    @Override
    public void draw(GraphicsContext gc) {
        double[][] p = points();
        double[] xs = p[0];
        double[] ys = p[1];

        double topY  = ys[0];
        double baseY = ys[1];

        if (Math.abs(baseY - topY) < 2) return;

        gc.save();

        // Dégradé du sommet vers la base
        LinearGradient grad = new LinearGradient(
            xs[0], topY, xs[0], baseY, false, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.rgb(240, 248, 255, 0.95)),
            new Stop(1.0, Color.rgb(180, 210, 248, 0.85))
        );

        gc.setFill(fillColor.equals(Color.rgb(220, 235, 255, 0.85)) ? grad : fillColor);
        gc.fillPolygon(xs, ys, 3);

        gc.setStroke(strokeColor);
        gc.setLineWidth(strokeWidth);
        gc.strokePolygon(xs, ys, 3);

        // Label "2D"
        gc.setFill(Color.rgb(60, 100, 180, 0.7));
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 10));
        gc.fillText("2D", xs[0] - 8, baseY - 5);

        gc.restore();
    }

    @Override
    public double getArea() {
        double base   = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);
        return 0.5 * base * height;
    }

    @Override
    public double getPerimeter() {
        double base   = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);
        double side   = Math.sqrt(Math.pow(base / 2, 2) + Math.pow(height, 2));
        return base + 2 * side;
    }

    @Override
    public void setStyle(Color strokeColor, Color fillColor, double strokeWidth) {
        this.strokeColor = strokeColor;
        this.fillColor   = fillColor;
        this.strokeWidth = strokeWidth;
    }

    @Override
    public boolean contains(double x, double y) {
        double[][] p = points();
        double x1 = p[0][0], y1 = p[1][0];
        double x2 = p[0][1], y2 = p[1][1];
        double x3 = p[0][2], y3 = p[1][2];
        double d1 = sign(x, y, x1, y1, x2, y2);
        double d2 = sign(x, y, x2, y2, x3, y3);
        double d3 = sign(x, y, x3, y3, x1, y1);
        boolean hasNeg = d1 < 0 || d2 < 0 || d3 < 0;
        boolean hasPos = d1 > 0 || d2 > 0 || d3 > 0;
        return !(hasNeg && hasPos);
    }

    private double sign(double px, double py, double ax, double ay, double bx, double by) {
        return (px - bx) * (ay - by) - (ax - bx) * (py - by);
    }

    @Override public String getType()  { return "2D"; }
    @Override public double getStartX(){ return startX; }
    @Override public double getStartY(){ return startY; }
    @Override public double getEndX()  { return endX;   }
    @Override public double getEndY()  { return endY;   }

    @Override
    public String toString() {
        return String.format("Triangle2D[Aire:%.0f | Périmètre:%.0f]",
            getArea(), getPerimeter());
    }
}
