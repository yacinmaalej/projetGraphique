package com.drawingapp.decorator;

import com.drawingapp.model.Shape;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Décorateur d'ombre — CORRIGÉ
 * 
 * Correction : l'ombre est maintenant dessinée en utilisant le MÊME type de forme
 * que la forme originale (rectangle→rectangle, ovale→ovale…) au lieu d'un fillOval
 * générique. On utilise la réflexion sur le type de la forme décorée via getType().
 */
public class ShadowDecorator extends ShapeDecorator {
    private double offsetX;
    private double offsetY;
    private Color shadowColor;
    private double blur; // simulation de flou par plusieurs couches semi-transparentes

    public ShadowDecorator(Shape decoratedShape) {
        super(decoratedShape);
        this.offsetX = 8;
        this.offsetY = 8;
        this.shadowColor = Color.rgb(0, 0, 0, 0.25);
        this.blur = 3;
    }

    public ShadowDecorator(Shape decoratedShape, double offsetX, double offsetY, Color shadowColor) {
        super(decoratedShape);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.shadowColor = shadowColor;
        this.blur = 3;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.save();

        double startX = decoratedShape.getStartX();
        double startY = decoratedShape.getStartY();
        double endX   = decoratedShape.getEndX();
        double endY   = decoratedShape.getEndY();

        double x = Math.min(startX, endX);
        double y = Math.min(startY, endY);
        double w = Math.abs(endX - startX);
        double h = Math.abs(endY - startY);

        // Simuler un flou doux avec plusieurs passes de plus en plus opaques
        int passes = 4;
        for (int i = passes; i >= 1; i--) {
            double expansion = i * (blur / passes);
            double alpha = (shadowColor.getOpacity() / passes) * (passes - i + 1);
            gc.setFill(Color.rgb(
                (int)(shadowColor.getRed()   * 255),
                (int)(shadowColor.getGreen() * 255),
                (int)(shadowColor.getBlue()  * 255),
                alpha
            ));

            drawShadowShape(gc,
                x + offsetX - expansion,
                y + offsetY - expansion,
                w + expansion * 2,
                h + expansion * 2
            );
        }

        gc.restore();

        // Dessiner la forme originale par-dessus
        decoratedShape.draw(gc);
    }

    /**
     * Dessine la forme d'ombre en adaptant le type de forme à la forme décorée.
     * Rectangle → fillRect, Cercle/Sphère → fillOval, etc.
     */
    private void drawShadowShape(GraphicsContext gc, double x, double y, double w, double h) {
        String type = decoratedShape.getClass().getSimpleName().toUpperCase();

        if (type.contains("CIRCLE") || type.contains("SPHERE")) {
            // Cercle : on centre l'ombre sur le point de départ (centre du cercle)
            double cx = decoratedShape.getStartX() + offsetX;
            double cy = decoratedShape.getStartY() + offsetY;
            double radius = Math.sqrt(
                Math.pow(decoratedShape.getEndX() - decoratedShape.getStartX(), 2) +
                Math.pow(decoratedShape.getEndY() - decoratedShape.getStartY(), 2)
            );
            double exp = (w - (radius * 2)) / 2;
            gc.fillOval(cx - radius - exp, cy - radius - exp,
                       (radius + exp) * 2, (radius + exp) * 2);

        } else if (type.contains("TRIANGLE") || type.contains("PYRAMID")) {
            // Triangle
            double baseSize = w;
            double apexX = x + baseSize / 2;
            gc.fillPolygon(
                new double[]{apexX, x, x + baseSize},
                new double[]{y, y + h, y + h},
                3
            );

        } else if (type.contains("LINE")) {
            // Ligne : ombre légèrement décalée et épaisse
            gc.setLineWidth(4);
            gc.setStroke(gc.getFill());
            gc.strokeLine(
                decoratedShape.getStartX() + offsetX,
                decoratedShape.getStartY() + offsetY,
                decoratedShape.getEndX() + offsetX,
                decoratedShape.getEndY() + offsetY
            );

        } else {
            // Rectangle par défaut (Rectangle2D, Cube3D, Cylinder3D…)
            gc.fillRoundRect(x, y, w, h, 6, 6);
        }
    }
}
