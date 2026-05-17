package com.drawingapp.adapter;

import com.drawingapp.decorator.ShadowDecorator;
import com.drawingapp.model.Shape;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Shape3DAdapter implements Shape {
    private Shape originalShape;
    private Shape enhancedShape;

    public Shape3DAdapter(Shape shape3D) {
        this.originalShape = shape3D;
        this.enhancedShape = new ShadowDecorator(shape3D, 7, 7,
                Color.rgb(0, 0, 0, 0.28));
    }

    @Override
    public void draw(GraphicsContext gc) {
        enhancedShape.draw(gc);
    }

    @Override
    public void setStyle(Color strokeColor, Color fillColor, double strokeWidth) {
        originalShape.setStyle(strokeColor, fillColor, strokeWidth);
    }

    @Override
    public boolean contains(double x, double y) {
        return originalShape.contains(x, y);
    }

    @Override public String getType()  { return originalShape.getType(); }
    @Override public double getStartX(){ return originalShape.getStartX(); }
    @Override public double getStartY(){ return originalShape.getStartY(); }
    @Override public double getEndX()  { return originalShape.getEndX(); }
    @Override public double getEndY()  { return originalShape.getEndY(); }

    public Shape getOriginalShape() {
        return originalShape;
    }
}