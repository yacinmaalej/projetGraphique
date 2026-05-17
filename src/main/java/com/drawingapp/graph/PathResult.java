package com.drawingapp.graph;

import java.util.Collections;
import java.util.List;

/**
 * Résultat d'un calcul de plus court chemin.
 */
public class PathResult {
    private final List<GraphNode> path;
    private final double totalCost;
    private final boolean found;

    public PathResult(List<GraphNode> path, double totalCost) {
        this.path      = path;
        this.totalCost = totalCost;
        this.found     = !path.isEmpty();
    }

    private PathResult() {
        this.path      = Collections.emptyList();
        this.totalCost = Double.MAX_VALUE;
        this.found     = false;
    }

    public static PathResult noPath() { return new PathResult(); }

    public List<GraphNode> getPath()     { return path; }
    public double          getTotalCost(){ return totalCost; }
    public boolean         isFound()    { return found; }

    @Override
    public String toString() {
        if (!found) return "Aucun chemin trouvé";
        StringBuilder sb = new StringBuilder("Chemin : ");
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).getId());
            if (i < path.size() - 1) sb.append(" → ");
        }
        sb.append("  |  Coût total : ").append(String.format("%.1f", totalCost));
        return sb.toString();
    }
}
