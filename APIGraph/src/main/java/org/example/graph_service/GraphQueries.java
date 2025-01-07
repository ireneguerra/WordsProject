package org.example.graph_service;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class GraphQueries {
    private final Graph<String, DefaultWeightedEdge> graph;

    public GraphQueries(Graph<String, DefaultWeightedEdge> graph) {
        this.graph = graph;
    }

    private String validateNodeExists(String node) {
        if (!graph.containsVertex(node)) {
            return "La palabra '" + node + "' no existe en el grafo.";
        }
        return null;
    }

    public List<String> shortestPath(String source, String target) {
        String sourceValidation = validateNodeExists(source);
        if (sourceValidation != null) {
            return Collections.singletonList(sourceValidation);
        }

        String targetValidation = validateNodeExists(target);
        if (targetValidation != null) {
            return Collections.singletonList(targetValidation);
        }

        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<>(graph);
        GraphPath<String, DefaultWeightedEdge> path = dijkstra.getPath(source, target);
        if (path == null) {
            return new ArrayList<>();
        }
        return path.getVertexList();
    }


    public List<List<String>> allPaths(String source, String target, int maxDepth) {
        String sourceValidation = validateNodeExists(source);
        if (sourceValidation != null) {
            return Collections.singletonList(Collections.singletonList(sourceValidation));
        }

        String targetValidation = validateNodeExists(target);
        if (targetValidation != null) {
            return Collections.singletonList(Collections.singletonList(targetValidation));
        }

        AllDirectedPaths<String, DefaultWeightedEdge> allPaths = new AllDirectedPaths<>(graph);
        return allPaths.getAllPaths(source, target, true, maxDepth).stream()
                .map(GraphPath::getVertexList)
                .collect(Collectors.toList());
    }

    public List<String> longestPath(String source, String target, int maxNodes) {
        String sourceValidation = validateNodeExists(source);
        if (sourceValidation != null) {
            return Collections.singletonList(sourceValidation);
        }

        String targetValidation = validateNodeExists(target);
        if (targetValidation != null) {
            return Collections.singletonList(targetValidation);
        }

        AllDirectedPaths<String, DefaultWeightedEdge> allPaths = new AllDirectedPaths<>(graph);
        List<GraphPath<String, DefaultWeightedEdge>> paths = allPaths.getAllPaths(source, target, true, maxNodes);

        GraphPath<String, DefaultWeightedEdge> longestPath = null;
        double maxWeight = Double.NEGATIVE_INFINITY;

        for (GraphPath<String, DefaultWeightedEdge> path : paths) {
            if (path.getLength() <= maxNodes && path.getWeight() > maxWeight) {
                maxWeight = path.getWeight();
                longestPath = path;
            }
        }

        return longestPath != null ? longestPath.getVertexList() : new ArrayList<>();
    }


    public List<Set<String>> identifyClusters() {
        ConnectivityInspector<String, DefaultWeightedEdge> inspector = new ConnectivityInspector<>(graph);
        return inspector.connectedSets();
    }

    public List<String> highConnectivityNodes(int minDegree) {
        return graph.vertexSet().stream()
                .filter(node -> graph.edgesOf(node).size() >= minDegree)
                .collect(Collectors.toList());
    }

    public List<String> nodesWithDegree(int degree) {
        return graph.vertexSet().stream()
                .filter(node -> graph.edgesOf(node).size() == degree)
                .collect(Collectors.toList());
    }

    public List<String> isolatedNodes() {
        return graph.vertexSet().stream()
                .filter(v -> graph.edgesOf(v).isEmpty())
                .collect(Collectors.toList());
    }
}
