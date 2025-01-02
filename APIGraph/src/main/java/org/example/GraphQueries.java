package org.example;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;
import java.util.stream.Collectors;

public class GraphQueries {
    private final Graph<String, DefaultWeightedEdge> graph;

    public GraphQueries(Graph<String, DefaultWeightedEdge> graph) {
        this.graph = graph;
    }

    public List<String> shortestPath(String source, String target) {
        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<>(graph);
        return dijkstra.getPath(source, target).getVertexList();
    }

    public List<List<String>> allPaths(String source, String target, int k) {
        return graph.vertexSet().stream()
                .filter(v -> v.equals(source))
                .flatMap(v -> graph.edgeSet().stream()
                        .filter(e -> graph.getEdgeSource(e).equals(v) || graph.getEdgeTarget(e).equals(v))
                        .map(e -> List.of(graph.getEdgeSource(e), graph.getEdgeTarget(e))))
                .limit(k)
                .collect(Collectors.toList());
    }

    public List<String> isolatedNodes() {
        return graph.vertexSet().stream()
                .filter(v -> graph.edgesOf(v).isEmpty())
                .collect(Collectors.toList());
    }
}