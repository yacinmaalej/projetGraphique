package com.drawingapp.model3d;

import com.drawingapp.model.Shape3D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;

/**
 * Pyramide 3D — AMÉLIORÉE
 * 
 * - Trois faces visibles avec des teintes différentes (éclairage simulé)
 * - Base en perspective
 * - Ombre portée au sol
 */
public class Pyramid3D implements Shape3D {
    private double startX, startY, endX, endY;
    private Color strokeColor;
    private Color fillColor;
    private double strokeWidth;

    public Pyramid3D(double startX, double startY, double endX, double endY) {
        this.startX      = startX;
        this.startY      = startY;
        this.endX        = endX;
        this.endY        = endY;
        this.strokeColor = Color.rgb(160, 130, 20);
        this.fillColor   = Color.rgb(230, 195, 60);
        this.strokeWidth = 1.5;
    }

    @Override
    public void draw(GraphicsContext gc) {
        double baseSize = Math.abs(endX - startX);
        double height   = Math.abs(endY - startY);
        double x        = Math.min(startX, endX);
        double y        = Math.min(startY, endY);

        if (baseSize < 10 || height < 10) return;

        double off   = baseSize * 0.28;  // décalage perspective
        double apexX = x + baseSize / 2;
        double apexY = y;
        double baseY = y + height;

        // Sommets des 4 angles de la base (losange en perspective)
        double baseFrontLeftX  = x;
        double baseFrontLeftY  = baseY;
        double baseFrontRightX = x + baseSize;
        double baseFrontRightY = baseY;
        double baseBackRightX  = x + baseSize + off;
        double baseBackRightY  = baseY - off;
        double baseBackLeftX   = x + off;
        double baseBackLeftY   = baseY - off;

        gc.save();

        // ── Ombre portée ──
        gc.setFill(Color.rgb(0, 0, 0, 0.15));
        double[] shadowXs = { apexX + off * 0.6, x + off * 0.3,
                              x + baseSize + off * 0.9, x + baseSize + off * 1.1 };
        double[] shadowYs = { baseY + 8, baseY + 12, baseY + 12, baseY + 5 };
        gc.fillPolygon(shadowXs, shadowYs, 4);

        // ── Base ──
        double[] baseXs = { baseFrontLeftX, baseFrontRightX, baseBackRightX, baseBackLeftX };
        double[] baseYs = { baseFrontLeftY, baseFrontRightY, baseBackRightY, baseBackLeftY };
        gc.setFill(Color.rgb(160, 130, 25, 0.80));
        gc.fillPolygon(baseXs, baseYs, 4);
        gc.setStroke(strokeColor);
        gc.setLineWidth(strokeWidth);
        gc.strokePolygon(baseXs, baseYs, 4);

        // ── Face GAUCHE (ombre) ──
        double[] leftXs = { apexX, baseFrontLeftX, baseBackLeftX };
        double[] leftYs = { apexY, baseFrontLeftY, baseBackLeftY };
        LinearGradient leftGrad = new LinearGradient(
            apexX, apexY, baseFrontLeftX, baseFrontLeftY, false, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.rgb(255, 230, 100, 0.95)),
            new Stop(1.0, Color.rgb(150, 110,  15, 0.90))
        );
        gc.setFill(leftGrad);
        gc.fillPolygon(leftXs, leftYs, 3);
        gc.setStroke(strokeColor);
        gc.strokePolygon(leftXs, leftYs, 3);

        // ── Face AVANT (lumière directe) ──
        double[] frontXs = { apexX, baseFrontLeftX, baseFrontRightX };
        double[] frontYs = { apexY, baseFrontLeftY, baseFrontRightY };
        LinearGradient frontGrad = new LinearGradient(
            apexX, apexY, (baseFrontLeftX + baseFrontRightX) / 2, baseFrontLeftY,
            false, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.rgb(255, 250, 200, 0.98)),
            new Stop(0.5, Color.rgb(240, 200,  60, 0.95)),
            new Stop(1.0, Color.rgb(200, 160,  25, 0.90))
        );
        gc.setFill(frontGrad);
        gc.fillPolygon(frontXs, frontYs, 3);
        gc.setStroke(strokeColor);
        gc.strokePolygon(frontXs, frontYs, 3);

        // ── Face DROITE (semi-ombre) ──
        double[] rightXs = { apexX, baseFrontRightX, baseBackRightX };
        double[] rightYs = { apexY, baseFrontRightY, baseBackRightY };
        LinearGradient rightGrad = new LinearGradient(
            apexX, apexY, baseBackRightX, baseBackRightY, false, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.rgb(240, 215,  90, 0.95)),
            new Stop(1.0, Color.rgb(130,  95,  15, 0.90))
        );
        gc.setFill(rightGrad);
        gc.fillPolygon(rightXs, rightYs, 3);
        gc.setStroke(strokeColor);
        gc.strokePolygon(rightXs, rightYs, 3);

        // Arête arrière gauche (ligne de l'apex vers l'arrière)
        gc.setStroke(Color.rgb(140, 110, 15, 0.6));
        gc.setLineWidth(1.0);
        gc.strokeLine(apexX, apexY, baseBackLeftX, baseBackLeftY);

        // Label "3D"
        gc.setFill(Color.rgb(130, 95, 15, 0.75));
        gc.setFont(javafx.scene.text.Font.font("Arial",
            javafx.scene.text.FontWeight.BOLD, 11));
        gc.fillText("3D", apexX - 8, apexY + height * 0.55);

        gc.restore();
    }

    @Override
    public double getVolume() {
        double b = Math.abs(endX - startX);
        double h = Math.abs(endY - startY);
        return (1.0 / 3.0) * b * b * h;
    }

    @Override
    public double getSurfaceArea() {
        double b  = Math.abs(endX - startX);
        double h  = Math.abs(endY - startY);
        double sl = Math.sqrt(Math.pow(b / 2, 2) + h * h);
        return b * b + 2 * b * sl;
    }

    @Override public String get3DRepresentation() {
        return "Pyramide [Volume: " + String.format("%.0f", getVolume()) + "]";
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
        return String.format("Pyramid3D[Volume:%.0f]", getVolume());
    }
}
