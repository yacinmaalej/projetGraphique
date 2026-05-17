package com.drawingapp.model3d;

import com.drawingapp.model.Shape3D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;

/**
 * Sphère 3D — AMÉLIORÉE
 * 
 * - Dégradé radial réaliste (lumière en haut-gauche)
 * - Reflet blanc proéminent
 * - Ombre portée au bas (ellipse sombre)
 */
public class Sphere3D implements Shape3D {
    private double startX, startY, endX, endY;
    private Color strokeColor;
    private Color fillColor;
    private double strokeWidth;

    public Sphere3D(double startX, double startY, double endX, double endY) {
        this.startX      = startX;
        this.startY      = startY;
        this.endX        = endX;
        this.endY        = endY;
        this.strokeColor = Color.rgb(160, 40, 40);
        this.fillColor   = Color.rgb(220, 80, 80);
        this.strokeWidth = 1.0;
    }

    @Override
    public void draw(GraphicsContext gc) {
        double radius = Math.sqrt(
            Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2)
        );
        if (radius < 5) return;

        double cx = startX;
        double cy = startY;
        double d  = radius * 2;

        gc.save();

        // ── Ombre portée au sol ──
        gc.setFill(Color.rgb(0, 0, 0, 0.18));
        gc.fillOval(cx - radius * 0.80, cy + radius * 0.80,
                   radius * 1.60, radius * 0.35);

        // ── Corps de la sphère (dégradé radial réaliste) ──
        RadialGradient sphereGrad = new RadialGradient(
            0, 0,
            0.35, 0.30,  // centre lumière relatif (haut-gauche)
            0.80,         // rayon relatif
            true, CycleMethod.NO_CYCLE,
            new Stop(0.00, Color.rgb(255, 210, 210, 1.00)),  // blanc-rosé (spéculaire)
            new Stop(0.30, Color.rgb(240, 100,  90, 0.95)),  // rouge vif
            new Stop(0.70, Color.rgb(180,  40,  40, 0.95)),  // rouge foncé
            new Stop(1.00, Color.rgb( 80,  10,  10, 0.90))   // très foncé (bord)
        );

        gc.setFill(sphereGrad);
        gc.fillOval(cx - radius, cy - radius, d, d);

        // Contour très léger
        gc.setStroke(Color.rgb(100, 20, 20, 0.5));
        gc.setLineWidth(1.0);
        gc.strokeOval(cx - radius, cy - radius, d, d);

        // ── Reflet principal (ellipse blanche haut-gauche) ──
        gc.setFill(Color.rgb(255, 255, 255, 0.60));
        gc.fillOval(cx - radius * 0.60,
                   cy - radius * 0.60,
                   radius * 0.40,
                   radius * 0.28);

        // ── Reflet secondaire (plus petit) ──
        gc.setFill(Color.rgb(255, 255, 255, 0.30));
        gc.fillOval(cx - radius * 0.30,
                   cy - radius * 0.35,
                   radius * 0.15,
                   radius * 0.10);

        // Label "3D"
        gc.setFill(Color.rgb(100, 20, 20, 0.75));
        gc.setFont(javafx.scene.text.Font.font("Arial",
            javafx.scene.text.FontWeight.BOLD, 11));
        gc.fillText("3D", cx - 8, cy + radius - 5);

        gc.restore();
    }

    @Override public double getVolume()      { double r = radius(); return (4.0/3.0)*Math.PI*r*r*r; }
    @Override public double getSurfaceArea() { double r = radius(); return 4*Math.PI*r*r; }
    private double radius() {
        return Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
    }
    @Override public String get3DRepresentation() {
        return "Sphère [Volume: " + String.format("%.0f", getVolume()) + "]";
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

    @Override public String getType()  { return "3D"; }
    @Override public double getStartX(){ return startX; }
    @Override public double getStartY(){ return startY; }
    @Override public double getEndX()  { return endX;   }
    @Override public double getEndY()  { return endY;   }

    @Override
    public String toString() {
        return String.format("Sphere3D[Volume:%.0f]", getVolume());
    }
}
