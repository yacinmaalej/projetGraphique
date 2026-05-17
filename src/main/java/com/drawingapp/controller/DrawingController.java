package com.drawingapp.controller;

import com.drawingapp.model.Dessin;
import com.drawingapp.model.Palette;

/**
 * La logique métier est dans Palette et Dessin
 * Ce contrôleur peut servir de façade pour simplifier les interactions entre la vue et les modèles, mais il n'est pas strictement nécessaire dans ce cas.
 */
public class DrawingController {
    private Palette palette;
    private Dessin dessin;

    public DrawingController(Palette palette, Dessin dessin) {
        this.palette = palette;
        this.dessin = dessin;
    }

    // Méthodes de délégation si nécessaire
    public void undo() {
        palette.undoAction();
    }

    public void clearAll() {
        palette.clearAction();
    }
}