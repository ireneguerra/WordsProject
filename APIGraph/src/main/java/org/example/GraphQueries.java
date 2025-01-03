package org.example;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;
import java.util.stream.Collectors;

public class GraphQueries {
    private final Graph<String, DefaultWeightedEdge> graph;

    public GraphQueries(Graph<String, DefaultWeightedEdge> graph) {
        this.graph = graph;
    }

    public List<String> shortestPath(String source, String target) {
        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<>(graph);
        GraphPath<String, DefaultWeightedEdge> path = dijkstra.getPath(source, target);
        if (path == null) {
            return new ArrayList<>();
        }
        return path.getVertexList();
    }

    public List<List<String>> allPaths(String source, String target) {
        AllDirectedPaths<String, DefaultWeightedEdge> allPaths = new AllDirectedPaths<>(graph);
        return allPaths.getAllPaths(source, target, true, Integer.MAX_VALUE).stream()
                .map(GraphPath::getVertexList)
                .collect(Collectors.toList());
    }

    public List<List<String>> limitedPaths(String source, String target, int k) {
        AllDirectedPaths<String, DefaultWeightedEdge> allPaths = new AllDirectedPaths<>(graph);
        return allPaths.getAllPaths(source, target, true, k).stream()
                .map(GraphPath::getVertexList)
                .collect(Collectors.toList());
    }

    public double longestPath(String source, String target) {
        Set<String> visited = new HashSet<>();
        return dfsLongestPath(source, target, 0, visited);
    }

    private double dfsLongestPath(String current, String target, double currentDistance, Set<String> visited) {
        if (current.equals(target)) {
            return currentDistance;
        }
        visited.add(current);
        double maxDistance = -1;

        for (DefaultWeightedEdge edge : graph.outgoingEdgesOf(current)) {
            String neighbor = graph.getEdgeTarget(edge);
            double edgeWeight = graph.getEdgeWeight(edge);

            if (!visited.contains(neighbor)) {
                double distance = dfsLongestPath(neighbor, target, currentDistance + edgeWeight, visited);
                maxDistance = Math.max(maxDistance, distance);
            }
        }

        visited.remove(current);
        return maxDistance;
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
