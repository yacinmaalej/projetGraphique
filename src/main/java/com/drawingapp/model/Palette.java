package com.drawingapp.model;

import com.drawingapp.factory.ShapeFactory;
import com.drawingapp.logger.LoggerManager;
import com.drawingapp.observer.Observable;
import com.drawingapp.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class Palette implements Observable {
    private List<Observer> observers;
    private ShapeFactory currentFactory;
    private String currentShapeType;
    private String currentDimension;

    public Palette() {
        this.observers = new ArrayList<>();
        this.currentShapeType = "AUCUN";
        this.currentDimension = "2D";
        LoggerManager.log("Palette initialisée - Abstract Factory Pattern actif");
    }

    @Override
    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            LoggerManager.log("Observer ajouté à la palette: " + observer.getClass().getSimpleName());
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
        LoggerManager.log("Observer retiré de la palette: " + observer.getClass().getSimpleName());
    }

    @Override
    public void notifyObservers(String eventType, Object data) {
        LoggerManager.log("Palette notifie - Événement: " + eventType +
                ", Dimension: " + currentDimension +
                ", Forme: " + currentShapeType);
        for (Observer observer : observers) {
            observer.update(eventType, data);
        }
    }

    public void selectShape(ShapeFactory factory, String shapeType) {
        this.currentFactory = factory;
        this.currentShapeType = shapeType;
        this.currentDimension = factory.getFactoryType();
        LoggerManager.log("Forme sélectionnée: " + factory.getFactoryType() + " - " + shapeType);
        String fullShapeInfo = factory.getFactoryType() + ":" + shapeType;
        notifyObservers("SHAPE_SELECTED", fullShapeInfo);
    }

    public ShapeFactory getCurrentFactory() { return currentFactory; }
    public String getCurrentShapeType() { return currentShapeType; }
    public String getCurrentDimension() { return currentDimension; }

    public void undoAction() {
        LoggerManager.log("Bouton Undo cliqué");
        notifyObservers("UNDO_REQUESTED", null);
    }

    public void redoAction() {
        LoggerManager.log("Bouton Redo cliqué");
        notifyObservers("REDO_REQUESTED", null);
    }

    public void clearAction() {
        LoggerManager.log("Bouton Clear cliqué");
        notifyObservers("CLEAR_REQUESTED", null);
    }

    public void changeLoggingStrategy(Object strategy) {
        LoggerManager.log("Changement stratégie de log: " + strategy);
        notifyObservers("LOGGING_STRATEGY_CHANGED", strategy);
    }
}