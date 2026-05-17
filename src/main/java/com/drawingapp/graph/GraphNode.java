package com.drawingapp.graph;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;

/**
 * Nœud d'un graphe.
 * Dessiné comme un cercle avec un label centré.
 */
public class GraphNode {
    private final String id;      // identifiant unique (ex: "A", "1"…)
    private double x, y;          // position centre
    private double radius = 22;

    // États visuels
    private boolean selected  = false;
    private boolean onPath    = false;   // fait partie du plus court chemin
    private boolean isSource  = false;
    private boolean isDest    = false;

    // Couleurs selon état
    private static final Color C_NORMAL   = Color.rgb(80, 130, 220);
    private static final Color C_SELECTED = Color.rgb(230, 140, 30);
    private static final Color C_PATH     = Color.rgb(60, 185, 90);
    private static final Color C_SOURCE   = Color.rgb(220, 60, 60);
    private static final Color C_DEST     = Color.rgb(180, 40, 180);

    public GraphNode(String id, double x, double y) {
        this.id = id;
        this.x  = x;
        this.y  = y;
    }

    public void draw(GraphicsContext gc) {
        gc.save();

        Color base = C_NORMAL;
        if (isSource)  base = C_SOURCE;
        else if (isDest) base = C_DEST;
        else if (onPath)  base = C_PATH;
        else if (selected) base = C_SELECTED;

        // Ombre
        gc.setFill(Color.rgb(0, 0, 0, 0.20));
        gc.fillOval(x - radius + 3, y - radius + 4, radius * 2, radius * 2);

        // Corps avec dégradé
        RadialGradient grad = new RadialGradient(
            0, 0, 0.38, 0.32, 0.75, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, base.brighter().brighter()),
            new Stop(0.5, base),
            new Stop(1.0, base.darker().darker())
        );
        gc.setFill(grad);
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        // Contour
        gc.setStroke(base.darker());
        gc.setLineWidth(onPath || isSource || isDest ? 2.5 : 1.5);
        gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);

        // Reflet
        gc.setFill(Color.rgb(255, 255, 255, 0.45));
        gc.fillOval(x - radius * 0.50, y - radius * 0.55,
                   radius * 0.38, radius * 0.28);

        // Label
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial",
            javafx.scene.text.FontWeight.BOLD, 14));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.fillText(id, x, y + 5);

        gc.restore();
    }

    /** Retourne true si le point (px, py) est dans le nœud */
    public boolean contains(double px, double py) {
        return Math.hypot(px - x, py - y) <= radius;
    }

    // ── Getters / Setters ──────────────────────────────────────────────────
    public String getId()  { return id; }
    public double getX()   { return x; }
    public double getY()   { return y; }
    public double getRadius() { return radius; }

    public void setPosition(double x, double y) { this.x = x; this.y = y; }
    public void setSelected(boolean b)  { this.selected  = b; }
    public void setOnPath(boolean b)    { this.onPath    = b; }
    public void setSource(boolean b)    { this.isSource  = b; }
    public void setDest(boolean b)      { this.isDest    = b; }

    public boolean isSelected()  { return selected; }
    public boolean isOnPath()    { return onPath; }
    public boolean isSource()    { return isSource; }
    public boolean isDest()      { return isDest; }

    @Override public String toString() { return "Node[" + id + "]"; }
}
