package com.drawingapp.model3d;

import com.drawingapp.model.Shape3D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;

/**
 * Cylindre 3D — AMÉLIORÉ
 * 
 * - Corps avec dégradé latéral (effet cylindrique réaliste)
 * - Disque du haut avec dégradé radial (lumière)
 * - Ellipse du bas sombre
 * - Ombre portée au sol
 */
public class Cylinder3D implements Shape3D {
    private double startX, startY, endX, endY;
    private Color strokeColor;
    private Color fillColor;
    private double strokeWidth;

    public Cylinder3D(double startX, double startY, double endX, double endY) {
        this.startX      = startX;
        this.startY      = startY;
        this.endX        = endX;
        this.endY        = endY;
        this.strokeColor = Color.rgb(30, 120, 60);
        this.fillColor   = Color.rgb(60, 180, 90);
        this.strokeWidth = 1.5;
    }

    @Override
    public void draw(GraphicsContext gc) {
        double w = Math.abs(endX - startX);
        double h = Math.abs(endY - startY);
        double x = Math.min(startX, endX);
        double y = Math.min(startY, endY);

        if (w < 10 || h < 10) return;

        double ey = h * 0.22;  // hauteur des ellipses
        double bodyTop    = y + ey / 2;
        double bodyBottom = y + h - ey / 2;
        double bodyH      = bodyBottom - bodyTop;

        gc.save();

        // ── Ombre portée au sol ──
        gc.setFill(Color.rgb(0, 0, 0, 0.15));
        gc.fillOval(x + w * 0.05, y + h + 4, w * 0.90, ey * 0.6);

        // ── Corps du cylindre (dégradé gauche→droite) ──
        LinearGradient bodyGrad = new LinearGradient(
            x, 0, x + w, 0, false, CycleMethod.NO_CYCLE,
            new Stop(0.00, Color.rgb( 30, 120,  50, 0.95)),  // sombre (bord gauche)
            new Stop(0.25, Color.rgb( 80, 185, 100, 0.95)),  // vert vif
            new Stop(0.50, Color.rgb(160, 240, 170, 0.92)),  // reflet (centre)
            new Stop(0.75, Color.rgb( 70, 175,  90, 0.95)),  // vert vif
            new Stop(1.00, Color.rgb( 20,  90,  40, 0.95))   // sombre (bord droit)
        );
        gc.setFill(bodyGrad);
        gc.fillRect(x, bodyTop, w, bodyH);

        // Masquer les dépassements du corps en bas
        gc.setFill(Color.rgb(25, 100, 45, 0.95));
        gc.fillOval(x, bodyBottom - ey / 2, w, ey);

        // ── Ellipse du bas ──
        LinearGradient bottomGrad = new LinearGradient(
            x, bodyBottom, x, bodyBottom + ey, false, CycleMethod.NO_CYCLE,
            new Stop(0, Color.rgb(40, 140, 65, 0.90)),
            new Stop(1, Color.rgb(20,  80, 40, 0.95))
        );
        gc.setFill(bottomGrad);
        gc.fillOval(x, bodyBottom - ey / 2, w, ey);
        gc.setStroke(strokeColor);
        gc.setLineWidth(strokeWidth);
        gc.strokeOval(x, bodyBottom - ey / 2, w, ey);

        // Contours du corps
        gc.setStroke(strokeColor);
        gc.strokeLine(x,     bodyTop, x,     bodyBottom);
        gc.strokeLine(x + w, bodyTop, x + w, bodyBottom);

        // ── Ellipse du haut (la plus lumineuse) ──
        LinearGradient topGrad = new LinearGradient(
            x, bodyTop - ey / 2, x, bodyTop + ey / 2, false, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.rgb(200, 255, 210, 0.98)),  // très clair (lumière)
            new Stop(1.0, Color.rgb( 80, 190, 105, 0.95))
        );
        gc.setFill(topGrad);
        gc.fillOval(x, bodyTop - ey / 2, w, ey);
        gc.setStroke(strokeColor);
        gc.strokeOval(x, bodyTop - ey / 2, w, ey);

        // Label "3D"
        gc.setFill(Color.rgb(20, 90, 40, 0.75));
        gc.setFont(javafx.scene.text.Font.font("Arial",
            javafx.scene.text.FontWeight.BOLD, 11));
        gc.fillText("3D", x + w / 2 - 8, bodyTop + bodyH / 2 + 4);

        gc.restore();
    }

    @Override
    public double getVolume() {
        double r = Math.abs(endX - startX) / 2;
        double h = Math.abs(endY - startY);
        return Math.PI * r * r * h;
    }

    @Override
    public double getSurfaceArea() {
        double r = Math.abs(endX - startX) / 2;
        double h = Math.abs(endY - startY);
        return 2 * Math.PI * r * (r + h);
    }

    @Override public String get3DRepresentation() {
        return "Cylindre [Volume: " + String.format("%.0f", getVolume()) + "]";
    }

    @Override
    public void setStyle(Color strokeColor, Color fillColor, double strokeWidth) {
        this.strokeColor = strokeColor;
        this.fillColor   = fillColor;
        this.strokeWidth = strokeWidth;
    }

    @Override
    public boolean contains(double x, double y) {
        return x >= Math.min(startX, endX) && x <= Math.max(startX, endX)
            && y >= Math.min(startY, endY) && y <= Math.max(startY, endY);
    }

    @Override public String getType()  { return "3D"; }
    @Override public double getStartX(){ return startX; }
    @Override public double getStartY(){ return startY; }
    @Override public double getEndX()  { return endX;   }
    @Override public double getEndY()  { return endY;   }

    @Override
    public String toString() {
        return String.format("Cylinder3D[Volume:%.0f]", getVolume());
    }
}
