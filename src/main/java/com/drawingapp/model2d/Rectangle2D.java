package com.drawingapp.model2d;

import com.drawingapp.model.Shape2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;

/**
 * Rectangle 2D — AMÉLIORÉ
 * 
 * Améliorations :
 * - Fond avec dégradé léger (bleu doux)
 * - Coin arrondis
 * - Contour plus propre (sans surcharge de couleur sur le label)
 * - Label "2D" discret en coin inférieur droit
 */
public class Rectangle2D implements Shape2D {
    private double startX, startY, endX, endY;
    private Color strokeColor;
    private Color fillColor;
    private double strokeWidth;

    private static final Color DEFAULT_FILL   = Color.rgb(220, 235, 255, 0.85); // bleu très clair
    private static final Color DEFAULT_STROKE = Color.rgb(60, 100, 180);

    public Rectangle2D(double startX, double startY, double endX, double endY) {
        this.startX      = startX;
        this.startY      = startY;
        this.endX        = endX;
        this.endY        = endY;
        this.strokeColor = DEFAULT_STROKE;
        this.fillColor   = DEFAULT_FILL;
        this.strokeWidth = 1.5;
    }

    @Override
    public void draw(GraphicsContext gc) {
        double x = Math.min(startX, endX);
        double y = Math.min(startY, endY);
        double w = Math.abs(endX - startX);
        double h = Math.abs(endY - startY);

        if (w < 2 || h < 2) return;

        gc.save();

        // Dégradé léger du haut vers le bas
        LinearGradient grad = new LinearGradient(
            x, y, x, y + h, false, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.rgb(230, 242, 255, 0.95)),
            new Stop(1.0, Color.rgb(190, 215, 250, 0.85))
        );
        gc.setFill(fillColor.equals(DEFAULT_FILL) ? grad : fillColor);
        gc.fillRoundRect(x, y, w, h, 8, 8);

        // Contour
        gc.setStroke(strokeColor);
        gc.setLineWidth(strokeWidth);
        gc.strokeRoundRect(x, y, w, h, 8, 8);

        // Reflet en haut (ligne blanche subtile)
        gc.setStroke(Color.rgb(255, 255, 255, 0.6));
        gc.setLineWidth(1.0);
        gc.strokeLine(x + 8, y + 2, x + w - 8, y + 2);

        // Label "2D" discret
        gc.setFill(Color.rgb(60, 100, 180, 0.7));
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 10));
        gc.fillText("2D", x + w - 20, y + h - 5);

        gc.restore();
    }

    @Override public double getArea()      { return Math.abs((endX - startX) * (endY - startY)); }
    @Override public double getPerimeter() { return 2 * (Math.abs(endX - startX) + Math.abs(endY - startY)); }

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

    @Override public String getType()  { return "2D"; }
    @Override public double getStartX(){ return startX; }
    @Override public double getStartY(){ return startY; }
    @Override public double getEndX()  { return endX;   }
    @Override public double getEndY()  { return endY;   }

    @Override
    public String toString() {
        return String.format("Rectangle2D[%.0f,%.0f → %.0f,%.0f | Aire:%.0f]",
            startX, startY, endX, endY, getArea());
    }
}
