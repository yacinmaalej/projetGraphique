package com.drawingapp.decorator;

import com.drawingapp.model.Shape;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class ShapeDecorator implements Shape {
    protected Shape decoratedShape;

    public ShapeDecorator(Shape decoratedShape) {
        this.decoratedShape = decoratedShape;
    }

    @Override
    public void draw(GraphicsContext gc) {
        decoratedShape.draw(gc);
    }

    @Override
    public void setStyle(Color strokeColor, Color fillColor, double strokeWidth) {
        decoratedShape.setStyle(strokeColor, fillColor, strokeWidth);
    }

    @Override
    public boolean contains(double x, double y) {
        return decoratedShape.contains(x, y);
    }

    @Override
    public String getType() {
        return decoratedShape.getType();
    }

    @Override
    public double getStartX() { return decoratedShape.getStartX(); }
    @Override
    public double getStartY() { return decoratedShape.getStartY(); }
    @Override
    public double getEndX() { return decoratedShape.getEndX(); }
    @Override
    public double getEndY() { return decoratedShape.getEndY(); }
}