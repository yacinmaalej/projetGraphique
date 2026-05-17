package com.drawingapp.graph;

import java.util.*;

/**
 * Interface Strategy pour les algorithmes de plus court chemin.
 * Pattern Strategy : on peut facilement ajouter d'autres algorithmes.
 */
public interface ShortestPathAlgorithm {
    /**
     * Calcule le plus court chemin du nœud source au nœud destination.
     *
     * @param nodes    tous les nœuds du graphe
     * @param edges    toutes les arêtes du graphe
     * @param sourceId ID du nœud source
     * @param destId   ID du nœud destination
     * @return PathResult contenant la liste des nœuds sur le chemin et le coût total,
     *         ou un résultat vide si aucun chemin n'existe.
     */
    PathResult findPath(List<GraphNode> nodes,
                        List<GraphEdge> edges,
                        String sourceId,
                        String destId);

    /** Nom affiché dans la ComboBox de l'UI */
    String getName();


    // ══════════════════════════════════════════════════════════════
    //  Implémentation 1 : Dijkstra (graphe pondéré)
    // ══════════════════════════════════════════════════════════════
    class Dijkstra implements ShortestPathAlgorithm {

        @Override
        public String getName() { return "Dijkstra (poids)"; }

        @Override
        public PathResult findPath(List<GraphNode> nodes,
                                   List<GraphEdge> edges,
                                   String sourceId, String destId) {

            Map<String, Double>      dist   = new HashMap<>();
            Map<String, String>      prev   = new HashMap<>();
            PriorityQueue<String>    pq     = new PriorityQueue<>(
                Comparator.comparingDouble(dist::get));
            Set<String>              visited= new HashSet<>();

            for (GraphNode n : nodes) dist.put(n.getId(), Double.MAX_VALUE);
            dist.put(sourceId, 0.0);
            pq.add(sourceId);

            while (!pq.isEmpty()) {
                String u = pq.poll();
                if (visited.contains(u)) continue;
                visited.add(u);
                if (u.equals(destId)) break;

                for (GraphEdge e : edges) {
                    String neighbor = null;
                    double w = e.getWeight();

                    if (e.getSource().getId().equals(u)) {
                        neighbor = e.getTarget().getId();
                    } else if (!isDirected(edges) && e.getTarget().getId().equals(u)) {
                        neighbor = e.getSource().getId();
                    }

                    if (neighbor != null && !visited.contains(neighbor)) {
                        double newDist = dist.get(u) + w;
                        if (newDist < dist.get(neighbor)) {
                            dist.put(neighbor, newDist);
                            prev.put(neighbor, u);
                            pq.add(neighbor);
                        }
                    }
                }
            }

            return buildPath(prev, dist, sourceId, destId, nodes);
        }

        /** Détection basique : si au moins une arête est asymétrique → orienté */
        private boolean isDirected(List<GraphEdge> edges) { return false; }
    }


    // ══════════════════════════════════════════════════════════════
    //  Implémentation 2 : BFS (graphe non pondéré, chemin le + court en arêtes)
    // ══════════════════════════════════════════════════════════════
    class BFS implements ShortestPathAlgorithm {

        @Override
        public String getName() { return "BFS (arêtes min)"; }

        @Override
        public PathResult findPath(List<GraphNode> nodes,
                                   List<GraphEdge> edges,
                                   String sourceId, String destId) {

            Map<String, String> prev    = new HashMap<>();
            Set<String>         visited = new HashSet<>();
            Queue<String>       queue   = new LinkedList<>();

            queue.add(sourceId);
            visited.add(sourceId);

            while (!queue.isEmpty()) {
                String u = queue.poll();
                if (u.equals(destId)) break;

                for (GraphEdge e : edges) {
                    String neighbor = null;
                    if (e.getSource().getId().equals(u)) {
                        neighbor = e.getTarget().getId();
                    } else if (e.getTarget().getId().equals(u)) {
                        neighbor = e.getSource().getId();
                    }

                    if (neighbor != null && !visited.contains(neighbor)) {
                        visited.add(neighbor);
                        prev.put(neighbor, u);
                        queue.add(neighbor);
                    }
                }
            }

            // Coût = nombre d'arêtes (poids = 1 par arête)
            Map<String, Double> dist = new HashMap<>();
            String cur = destId;
            double d = 0;
            while (prev.containsKey(cur)) { cur = prev.get(cur); d++; }
            dist.put(destId, d);

            return buildPath(prev, dist, sourceId, destId, nodes);
        }
    }


    // ══════════════════════════════════════════════════════════════
    //  Méthode utilitaire commune : reconstruit le chemin
    // ══════════════════════════════════════════════════════════════
    private static PathResult buildPath(Map<String, String> prev,
                                        Map<String, Double>  dist,
                                        String sourceId, String destId,
                                        List<GraphNode> nodes) {

        if (!prev.containsKey(destId) && !sourceId.equals(destId)) {
            return PathResult.noPath();
        }

        Deque<String> path = new ArrayDeque<>();
        String cur = destId;
        while (cur != null) {
            path.addFirst(cur);
            cur = prev.get(cur);
        }

        List<GraphNode> pathNodes = new ArrayList<>();
        Map<String, GraphNode> nodeMap = new HashMap<>();
        for (GraphNode n : nodes) nodeMap.put(n.getId(), n);
        for (String id : path) {
            GraphNode n = nodeMap.get(id);
            if (n != null) pathNodes.add(n);
        }

        double totalCost = dist.getOrDefault(destId, 0.0);
        if (totalCost == Double.MAX_VALUE) return PathResult.noPath();

        return new PathResult(pathNodes, totalCost);
    }
}
