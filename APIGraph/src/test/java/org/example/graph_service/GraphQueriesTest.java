package org.example.graph_service;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphQueriesTest {

    @Test
    void testShortestPath() {
        Graph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        graph.addVertex("A");
        graph.addVertex("B");
        DefaultWeightedEdge edge = graph.addEdge("A", "B");
        graph.setEdgeWeight(edge, 1.0);

        GraphQueries queries = new GraphQueries(graph);
        List<String> path = queries.shortestPath("A", "B");

        assertEquals(List.of("A", "B"), path);
    }

    @Test
    void testIsolatedNodes() {
        Graph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B");

        GraphQueries queries = new GraphQueries(graph);
        List<String> isolatedNodes = queries.isolatedNodes();

        assertEquals(List.of("C"), isolatedNodes);
    }
}

