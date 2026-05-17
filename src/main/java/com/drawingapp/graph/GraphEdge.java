package com.drawingapp.graph;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Arête (arc pondéré) entre deux nœuds.
 */
public class GraphEdge {
    private final GraphNode source;
    private final GraphNode target;
    private double weight;
    private boolean onPath   = false;   // fait partie du plus court chemin
    private boolean directed = false;   // arête directionnelle ?

    private static final Color C_NORMAL = Color.rgb(120, 120, 130);
    private static final Color C_PATH   = Color.rgb(50, 200, 80);

    public GraphEdge(GraphNode source, GraphNode target, double weight, boolean directed) {
        this.source   = source;
        this.target   = target;
        this.weight   = weight;
        this.directed = directed;
    }

    public void draw(GraphicsContext gc) {
        gc.save();

        double sx = source.getX(), sy = source.getY();
        double tx = target.getX(), ty = target.getY();

        // Raccourcir pour ne pas chevaucher les nœuds
        double angle = Math.atan2(ty - sy, tx - sx);
        double r     = source.getRadius();
        double startX = sx + Math.cos(angle) * (r + 1);
        double startY = sy + Math.sin(angle) * (r + 1);
        double endX   = tx - Math.cos(angle) * (r + 1);
        double endY   = ty - Math.sin(angle) * (r + 1);

        Color lineColor = onPath ? C_PATH : C_NORMAL;
        gc.setStroke(lineColor);
        gc.setLineWidth(onPath ? 3.0 : 1.8);
        gc.strokeLine(startX, startY, endX, endY);

        // Flèche (si graphe orienté ou chemin surligné)
        if (directed || onPath) {
            drawArrow(gc, startX, startY, endX, endY, lineColor);
        }

        // Label du poids au milieu de l'arête
        double midX = (startX + endX) / 2;
        double midY = (startY + endY) / 2;

        // Fond blanc pour la lisibilité
        gc.setFill(Color.rgb(255, 255, 255, 0.82));
        gc.fillRoundRect(midX - 12, midY - 10, 24, 16, 6, 6);

        gc.setFill(onPath ? Color.rgb(30, 130, 50) : Color.rgb(80, 80, 90));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.fillText(formatWeight(weight), midX, midY + 1);

        gc.restore();
    }

    private void drawArrow(GraphicsContext gc, double sx, double sy,
                           double ex, double ey, Color c) {
        double angle = Math.atan2(ey - sy, ex - sx);
        double size  = 10;
        double ax1   = ex - size * Math.cos(angle - 0.4);
        double ay1   = ey - size * Math.sin(angle - 0.4);
        double ax2   = ex - size * Math.cos(angle + 0.4);
        double ay2   = ey - size * Math.sin(angle + 0.4);
        gc.setFill(c);
        gc.fillPolygon(new double[]{ex, ax1, ax2},
                       new double[]{ey, ay1, ay2}, 3);
    }

    private String formatWeight(double w) {
        return w == (int) w ? String.valueOf((int) w) : String.format("%.1f", w);
    }

    // ── Getters / Setters ──────────────────────────────────────────────────
    public GraphNode getSource() { return source; }
    public GraphNode getTarget() { return target; }
    public double    getWeight() { return weight; }
    public void      setWeight(double w) { this.weight = w; }
    public void      setOnPath(boolean b){ this.onPath  = b; }
    public boolean   isOnPath()  { return onPath; }

    @Override public String toString() {
        return source.getId() + " →" + (int) weight + "→ " + target.getId();
    }
}
