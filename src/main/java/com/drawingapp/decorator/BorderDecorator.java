package com.drawingapp.decorator;

import com.drawingapp.model.Shape;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

public class BorderDecorator extends ShapeDecorator {
    private Color borderColor;
    private double borderWidth;
    private double[] dashPattern;

    public BorderDecorator(Shape decoratedShape, Color borderColor, double borderWidth) {
        super(decoratedShape);
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        this.dashPattern = null;
    }

    public BorderDecorator(Shape decoratedShape, Color borderColor, double borderWidth, double[] dashPattern) {
        super(decoratedShape);
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        this.dashPattern = dashPattern;
    }

    @Override
    public void draw(GraphicsContext gc) {
        // Dessiner la forme originale
        decoratedShape.draw(gc);

        // Dessiner la bordure par-dessus
        gc.save();
        gc.setStroke(borderColor);
        gc.setLineWidth(borderWidth);
        gc.setLineCap(StrokeLineCap.ROUND);

        if (dashPattern != null) {
            gc.setLineDashes(dashPattern);
        }

        // Rectangle englobant pour la bordure
        double x = Math.min(decoratedShape.getStartX(), decoratedShape.getEndX());
        double y = Math.min(decoratedShape.getStartY(), decoratedShape.getEndY());
        double w = Math.abs(decoratedShape.getEndX() - decoratedShape.getStartX());
        double h = Math.abs(decoratedShape.getEndY() - decoratedShape.getStartY());

        gc.strokeRect(x - 5, y - 5, w + 10, h + 10);

        gc.restore();
    }
}