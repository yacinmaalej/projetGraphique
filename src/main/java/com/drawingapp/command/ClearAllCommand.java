package com.drawingapp.command;

import com.drawingapp.logger.Logger;
import com.drawingapp.logger.LoggerManager;
import com.drawingapp.model.Shape;

import java.util.ArrayList;
import java.util.List;

public class ClearAllCommand implements Command {
    private List<Shape> shapes;
    private List<Shape> savedShapes; // Pour pouvoir undo le clear
    private Logger logger;

    public ClearAllCommand(List<Shape> shapes) {
        this.shapes = shapes;
        this.savedShapes = new ArrayList<>();
        this.logger = LoggerManager.getInstance().getLogger();
    }

    @Override
    public void execute() {
        // Sauvegarder les formes avant de les effacer
        savedShapes.addAll(shapes);
        shapes.clear();
        logger.log("Command: ClearAll exécuté - " + savedShapes.size() + " forme(s) effacée(s)");
    }

    @Override
    public void undo() {
        // Restaurer les formes
        shapes.addAll(savedShapes);
        logger.log("Command: Undo ClearAll - " + savedShapes.size() + " forme(s) restaurée(s)");
    }

    @Override
    public void redo() {
        savedShapes.clear();
        savedShapes.addAll(shapes);
        shapes.clear();
        logger.log("Command: Redo ClearAll - Formes effacées à nouveau");
    }

    @Override
    public String getDescription() {
        return "Effacer tout (" + savedShapes.size() + " forme(s))";
    }
}