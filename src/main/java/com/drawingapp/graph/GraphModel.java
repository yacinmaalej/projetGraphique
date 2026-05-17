package com.drawingapp.graph;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

/**
 * Modèle du graphe : contient nœuds et arêtes, gère le rendu et le calcul
 * du plus court chemin.
 */
public class GraphModel {

    private final List<GraphNode> nodes = new ArrayList<>();
    private final List<GraphEdge> edges = new ArrayList<>();

    // Résultat du dernier calcul
    private PathResult lastResult = null;
    private String     statusMessage = "Cliquez sur le canvas pour ajouter des nœuds.";

    // Prochain identifiant de nœud (A, B, C, … Z, AA, AB, …)
    private int nodeCounter = 0;

    // ── Ajout / suppression ────────────────────────────────────────────────

    public GraphNode addNode(double x, double y) {
        String id   = generateId(nodeCounter++);
        GraphNode n = new GraphNode(id, x, y);
        nodes.add(n);
        return n;
    }

    public GraphEdge addEdge(GraphNode source, GraphNode target, double weight) {
        // Éviter les doublons (arête entre les mêmes nœuds dans le même sens)
        for (GraphEdge e : edges) {
            if (e.getSource() == source && e.getTarget() == target) return null;
            if (e.getSource() == target && e.getTarget() == source) return null;
        }
        GraphEdge e = new GraphEdge(source, target, weight, false);
        edges.add(e);
        return e;
    }

    public void removeNode(GraphNode n) {
        nodes.remove(n);
        edges.removeIf(e -> e.getSource() == n || e.getTarget() == n);
    }

    public void clear() {
        nodes.clear();
        edges.clear();
        nodeCounter  = 0;
        lastResult   = null;
        statusMessage = "Graphe effacé.";
    }

    // ── Dessin ────────────────────────────────────────────────────────────

    public void draw(GraphicsContext gc) {
        // Arêtes en premier
        for (GraphEdge e : edges) e.draw(gc);

        // Nœuds par-dessus
        for (GraphNode n : nodes) n.draw(gc);

        // Affichage du résultat en bas du canvas
        if (lastResult != null) {
            drawResultBanner(gc, lastResult.toString());
        } else {
            drawStatusHint(gc);
        }
    }

    private void drawResultBanner(GraphicsContext gc, String text) {
        gc.save();
        gc.setFill(Color.rgb(230, 255, 230, 0.90));
        gc.fillRoundRect(10, 10, 500, 30, 8, 8);
        gc.setStroke(Color.rgb(60, 180, 80));
        gc.setLineWidth(1.0);
        gc.strokeRoundRect(10, 10, 500, 30, 8, 8);
        gc.setFill(Color.rgb(30, 110, 50));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        gc.fillText(text, 18, 30);
        gc.restore();
    }

    private void drawStatusHint(GraphicsContext gc) {
        gc.save();
        gc.setFill(Color.rgb(100, 100, 120, 0.65));
        gc.setFont(Font.font("Arial", 11));
        gc.fillText(statusMessage, 10, 20);
        gc.restore();
    }

    // ── Calcul du plus court chemin ────────────────────────────────────────

    /**
     * Calcule et surligne le chemin. Réinitialise l'état visuel avant.
     */
    public PathResult computeShortestPath(String sourceId, String destId,
                                          ShortestPathAlgorithm algo) {
        clearPathHighlight();

        lastResult = algo.findPath(nodes, edges, sourceId, destId);

        if (lastResult.isFound()) {
            // Surligner les nœuds du chemin
            List<GraphNode> pathNodes = lastResult.getPath();
            for (int i = 0; i < pathNodes.size(); i++) {
                GraphNode n = pathNodes.get(i);
                n.setOnPath(true);
                if (i == 0)                          n.setSource(true);
                if (i == pathNodes.size() - 1)       n.setDest(true);
            }

            // Surligner les arêtes du chemin
            for (int i = 0; i < pathNodes.size() - 1; i++) {
                GraphNode a = pathNodes.get(i);
                GraphNode b = pathNodes.get(i + 1);
                for (GraphEdge e : edges) {
                    if ((e.getSource() == a && e.getTarget() == b)
                     || (e.getSource() == b && e.getTarget() == a)) {
                        e.setOnPath(true);
                    }
                }
            }
            statusMessage = lastResult.toString();
        } else {
            statusMessage = "Aucun chemin entre " + sourceId + " et " + destId;
        }
        return lastResult;
    }

    public void clearPathHighlight() {
        nodes.forEach(n -> { n.setOnPath(false); n.setSource(false);
                             n.setDest(false);   n.setSelected(false); });
        edges.forEach(e -> e.setOnPath(false));
        lastResult = null;
    }

    // ── Utilitaires ───────────────────────────────────────────────────────

    public GraphNode findNodeAt(double x, double y) {
        for (int i = nodes.size() - 1; i >= 0; i--) {
            if (nodes.get(i).contains(x, y)) return nodes.get(i);
        }
        return null;
    }

    public List<GraphNode> getNodes() { return nodes; }
    public List<GraphEdge> getEdges() { return edges; }

    public void setStatusMessage(String m) { this.statusMessage = m; }
    public String getStatusMessage()        { return statusMessage; }

    /** Génère A, B, … Z, AA, AB … */
    private static String generateId(int n) {
        StringBuilder sb = new StringBuilder();
        do {
            sb.insert(0, (char) ('A' + (n % 26)));
            n = n / 26 - 1;
        } while (n >= 0);
        return sb.toString();
    }
}
