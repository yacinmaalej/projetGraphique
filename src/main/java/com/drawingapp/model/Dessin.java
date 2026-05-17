package com.drawingapp.model;

import com.drawingapp.adapter.Shape3DAdapter;
import com.drawingapp.command.*;
import com.drawingapp.factory.Shape2DFactory;
import com.drawingapp.factory.Shape3DFactory;
import com.drawingapp.factory.ShapeFactory;
import com.drawingapp.logger.ConsoleLogger;
import com.drawingapp.logger.Logger;
import com.drawingapp.logger.LoggerManager;
import com.drawingapp.observer.Observer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class Dessin implements Observer {
    private Canvas canvas;
    private GraphicsContext gc;
    private List<Shape> shapes;
    private Logger logger;

    private CommandHistory commandHistory;

    private ShapeFactory currentFactory;
    private String currentShapeType;

    private double startX, startY;
    private boolean isDrawing = false;

    public Dessin(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.shapes = new ArrayList<>();
        this.logger =  LoggerManager.getInstance().getLogger();
        this.currentShapeType = "AUCUN";

        // Initialiser l'historique de commandes
        this.commandHistory = new CommandHistory();

        setupEventHandlers();
        logger.log("Dessin initialisé avec Command Pattern - Prêt pour Undo/Redo");
    }

    private void setupEventHandlers() {
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(this::handleMouseReleased);
    }

    // IMPLÉMENTATION DU PATTERN OBSERVER
    @Override
    public void update(String eventType, Object data) {
        logger.log("Dessin reçoit - Événement: " + eventType + ", Data: " + data);

        switch (eventType) {
            case "SHAPE_SELECTED":
                if (data instanceof String) {
                    String[] parts = ((String) data).split(":");
                    if (parts.length == 2) {
                        String dimension = parts[0];
                        String shapeType = parts[1];
                        handleShapeSelected(dimension, shapeType);
                    }
                }
                break;

            case "UNDO_REQUESTED":
                undo();
                break;

            case "REDO_REQUESTED":
                redo();
                break;

            case "CLEAR_REQUESTED":
                clearAll();
                break;

            case "DIMENSION_CHANGED":
                logger.log("Dimension changée: " + data);
                break;

            case "LOGGING_STRATEGY_CHANGED":
                handleLoggingStrategyChanged(data);
                break;

            default:
                logger.log("Événement inconnu: " + eventType);
                break;
        }
    }

    private void handleShapeSelected(String dimension, String shapeType) {
        if (dimension.equals("2D")) {
            this.currentFactory = new Shape2DFactory();
            this.currentShapeType = shapeType;
        } else if (dimension.equals("3D")) {
            this.currentFactory = new Shape3DFactory();
            this.currentShapeType = shapeType;
        }
        logger.log("Dessin configuré: " + dimension + " - " + shapeType);
    }

    // MÉTHODES UNDO/REDO AVEC COMMAND PATTERN
    public void undo() {
        if (commandHistory.canUndo()) {
            commandHistory.undo();
            redrawCanvas();
            logger.log("Dessin: UNDO exécuté - Stack undo: " +
                    commandHistory.getUndoStackSize() + ", redo: " +
                    commandHistory.getRedoStackSize());
        } else {
            logger.log("Dessin: Rien à undo");
        }
    }

    public void redo() {
        if (commandHistory.canRedo()) {
            commandHistory.redo();
            redrawCanvas();
            logger.log("Dessin: REDO exécuté - Stack undo: " +
                    commandHistory.getUndoStackSize() + ", redo: " +
                    commandHistory.getRedoStackSize());
        } else {
            logger.log("Dessin: Rien à redo");
        }
    }

    public void clearAll() {
        if (!shapes.isEmpty()) {
            Command clearCommand = new ClearAllCommand(shapes);
            commandHistory.executeCommand(clearCommand);
            redrawCanvas();
            logger.log("Dessin: ClearAll exécuté via Command");
        } else {
            logger.log("Dessin: Rien à effacer");
        }
    }

    public boolean canUndo() {
        return commandHistory.canUndo();
    }

    public boolean canRedo() {
        return commandHistory.canRedo();
    }

    private void handleLoggingStrategyChanged(Object data) {
        logger.log("Dessin: Changement de stratégie de log reçu: " + data);
    }

    // GESTION DES ÉVÉNEMENTS SOURIS
    private void handleMousePressed(MouseEvent event) {
        if (currentFactory != null && currentShapeType != null) {
            startX = event.getX();
            startY = event.getY();
            isDrawing = true;
            logger.log(String.format("Début dessin %s:%s à (%.2f, %.2f)",
                    currentFactory.getFactoryType(), currentShapeType, startX, startY));
        } else {
            logger.log("ATTENTION: Aucune forme sélectionnée pour le dessin");
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (isDrawing && currentFactory != null && currentShapeType != null) {
            redrawCanvas();
            drawCurrentShape(event.getX(), event.getY());
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        if (isDrawing && currentFactory != null && currentShapeType != null) {
            // Créer la forme
            Shape shape = currentFactory.createShape(currentShapeType,
                    startX, startY, event.getX(), event.getY());

            // ★★★ Appliquer l'Adapter pour les formes 3D ★★★
            if (shape.getType().equals("3D")) {
                shape = new Shape3DAdapter(shape);
            }

            // ★★★ POINT CRITIQUE : Utiliser le Command Pattern ★★★
            Command drawCommand = new DrawShapeCommand(shapes, shape);
            commandHistory.executeCommand(drawCommand);

            logger.log(String.format("Forme créée via Command: %s", shape.toString()));
            logger.log("Stack undo: " + commandHistory.getUndoStackSize() +
                    ", Stack redo: " + commandHistory.getRedoStackSize());

            isDrawing = false;
            redrawCanvas();
        }
    }

    private void drawCurrentShape(double currentX, double currentY) {
        if (currentFactory != null && currentShapeType != null) {
            Shape tempShape = currentFactory.createShape(currentShapeType,
                    startX, startY, currentX, currentY);
            tempShape.draw(gc);
        }
    }

    private void redrawCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (Shape shape : shapes) {
            shape.draw(gc);
        }
    }

    // GETTERS
    public List<Shape> getShapes() {
        return shapes;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public String getCurrentShapeType() {
        return currentShapeType;
    }

    public CommandHistory getCommandHistory() {
        return commandHistory;
    }

    public void setShapes(List<Shape> shapes) {
        this.shapes.clear();
        this.shapes.addAll(shapes);
        redrawCanvas();
    }

    public void loadShapesFromDatabase(List<Shape> loadedShapes) {
        shapes.clear();
        commandHistory.clear(); // Vider l'historique undo/redo

        for (Shape shape : loadedShapes) {
            // ★★★ Ré-appliquer l'Adapter aux formes 3D ★★★
            if (shape.getType().equals("3D")) {
                shape = new com.drawingapp.adapter.Shape3DAdapter(shape);
            }
            shapes.add(shape);
        }

        redrawCanvas();
        logger.log("Formes chargées depuis la BD: " + shapes.size() + " forme(s)");
    }
}