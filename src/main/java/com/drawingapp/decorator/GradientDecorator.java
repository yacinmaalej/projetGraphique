package com.drawingapp.decorator;

import com.drawingapp.model.Shape;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;

public class GradientDecorator extends ShapeDecorator {
    private Color color1;
    private Color color2;

    public GradientDecorator(Shape decoratedShape, Color color1, Color color2) {
        super(decoratedShape);
        this.color1 = color1;
        this.color2 = color2;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.save();

        double x = Math.min(decoratedShape.getStartX(), decoratedShape.getEndX());
        double y = Math.min(decoratedShape.getStartY(), decoratedShape.getEndY());
        double w = Math.abs(decoratedShape.getEndX() - decoratedShape.getStartX());
        double h = Math.abs(decoratedShape.getEndY() - decoratedShape.getStartY());

        // Créer un dégradé
        LinearGradient gradient = new LinearGradient(
                x, y, x + w, y + h, false, CycleMethod.NO_CYCLE,
                new Stop(0, color1),
                new Stop(1, color2)
        );

        gc.setFill(gradient);
        gc.fillRect(x, y, w, h);

        gc.restore();

        // Dessiner la forme par-dessus
        decoratedShape.draw(gc);
    }
}