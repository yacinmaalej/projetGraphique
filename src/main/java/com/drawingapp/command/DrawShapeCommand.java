package com.drawingapp.command;

import com.drawingapp.logger.Logger;
import com.drawingapp.logger.LoggerManager;
import com.drawingapp.model.Shape;

import java.util.List;

public class DrawShapeCommand implements Command {
    private List<Shape> shapes;
    private Shape shape;
    private Logger logger;

    public DrawShapeCommand(List<Shape> shapes, Shape shape) {
        this.shapes = shapes;
        this.shape = shape;
        this.logger = LoggerManager.getInstance().getLogger();
    }

    @Override
    public void execute() {
        shapes.add(shape);
        logger.log("Command EXECUTE: Ajout de " + shape.toString());
    }

    @Override
    public void undo() {
        if (!shapes.isEmpty()) {
            // Enlever la dernière forme ajoutée
            shapes.remove(shapes.size() - 1);
            logger.log("Command UNDO: Retrait de " + shape.toString());
        }
    }

    @Override
    public void redo() {
        shapes.add(shape);
        logger.log("Command REDO: Ré-ajout de " + shape.toString());
    }

    @Override
    public String getDescription() {
        return "Dessiner " + shape.toString();
    }
}