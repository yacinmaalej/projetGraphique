package com.drawingapp.model3d;

import com.drawingapp.model.Shape3D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;

/**
 * Cube 3D — AMÉLIORÉ
 * 
 * Rendu isométrique propre avec trois faces (avant, dessus, droite)
 * chacune avec une teinte différente pour simuler l'éclairage.
 */
public class Cube3D implements Shape3D {
    private double startX, startY, endX, endY;
    private Color strokeColor;
    private Color fillColor;
    private double strokeWidth;

    // Couleurs de base du cube (bleu-indigo)
    private static final Color BASE = Color.rgb(80, 120, 220);

    public Cube3D(double startX, double startY, double endX, double endY) {
        this.startX      = startX;
        this.startY      = startY;
        this.endX        = endX;
        this.endY        = endY;
        this.strokeColor = Color.rgb(40, 70, 160);
        this.fillColor   = BASE;
        this.strokeWidth = 1.5;
    }

    @Override
    public void draw(GraphicsContext gc) {
        double size = Math.abs(endX - startX);
        if (size < 10) return;

        double x   = Math.min(startX, endX);
        double y   = Math.min(startY, endY);
        double off = size * 0.35; // décalage perspective

        gc.save();

        // ── Face DESSUS (la plus claire, lumière vient d'en haut) ──
        double[] topXs = { x, x + size, x + size + off, x + off };
        double[] topYs = { y, y,         y - off,          y - off };
        LinearGradient topGrad = new LinearGradient(
            x, y - off, x, y, false, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.rgb(200, 220, 255, 0.98)),
            new Stop(1.0, Color.rgb(160, 190, 245, 0.90))
        );
        gc.setFill(topGrad);
        gc.fillPolygon(topXs, topYs, 4);
        gc.setStroke(strokeColor);
        gc.setLineWidth(strokeWidth);
        gc.strokePolygon(topXs, topYs, 4);

        // ── Face AVANT (luminosité moyenne) ──
        LinearGradient frontGrad = new LinearGradient(
            x, y, x + size, y + size, false, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.rgb(140, 175, 240, 0.95)),
            new Stop(1.0, Color.rgb(80, 120, 200, 0.90))
        );
        gc.setFill(frontGrad);
        gc.fillRect(x, y, size, size);
        gc.setStroke(strokeColor);
        gc.strokeRect(x, y, size, size);

        // ── Face DROITE (la plus sombre, ombre) ──
        double[] rightXs = { x + size, x + size + off, x + size + off, x + size };
        double[] rightYs = { y,         y - off,          y + size - off, y + size };
        LinearGradient rightGrad = new LinearGradient(
            x + size, y, x + size + off, y, false, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.rgb(80, 110, 195, 0.90)),
            new Stop(1.0, Color.rgb(45,  75, 155, 0.90))
        );
        gc.setFill(rightGrad);
        gc.fillPolygon(rightXs, rightYs, 4);
        gc.setStroke(strokeColor);
        gc.strokePolygon(rightXs, rightYs, 4);

        // Label "3D" discret
        gc.setFill(Color.rgb(30, 60, 150, 0.75));
        gc.setFont(javafx.scene.text.Font.font("Arial",
            javafx.scene.text.FontWeight.BOLD, 11));
        gc.fillText("3D", x + size / 2 - 8, y + size / 2 + 4);

        gc.restore();
    }

    @Override public double getVolume()      { double s = Math.abs(endX - startX); return s * s * s; }
    @Override public double getSurfaceArea() { double s = Math.abs(endX - startX); return 6 * s * s; }
    @Override public String get3DRepresentation() {
        return "Cube [Volume: " + String.format("%.0f", getVolume()) + "]";
    }

    @Override
    public void setStyle(Color strokeColor, Color fillColor, double strokeWidth) {
        this.strokeColor = strokeColor;
        this.fillColor   = fillColor;
        this.strokeWidth = strokeWidth;
    }

    @Override
    public boolean contains(double x, double y) {
        double size = Math.abs(endX - startX);
        double left = Math.min(startX, endX);
        double top  = Math.min(startY, endY);
        return x >= left && x <= left + size && y >= top && y <= top + size;
    }

    @Override public String getType()  { return "3D"; }
    @Override public double getStartX(){ return startX; }
    @Override public double getStartY(){ return startY; }
    @Override public double getEndX()  { return endX;   }
    @Override public double getEndY()  { return endY;   }

    @Override
    public String toString() {
        return String.format("Cube3D[Volume:%.0f]", getVolume());
    }
}
