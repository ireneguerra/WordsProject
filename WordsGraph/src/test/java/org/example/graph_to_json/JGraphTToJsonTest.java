package org.example.graph_to_json;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class JGraphTToJsonTest {

    @Test
    void testConvertGraphToJson() throws IOException {
        Graph<String, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        graph.addVertex("word1");
        graph.addVertex("word2");
        DefaultWeightedEdge edge = graph.addEdge("word1", "word2");
        graph.setEdgeWeight(edge, 2.5);

        JGraphTToJson converter = new JGraphTToJson();
        String json = converter.convertGraphToJson(graph);

        assertNotNull(json);
        assertTrue(json.contains("word1"));
        assertTrue(json.contains("word2"));
        assertTrue(json.contains("2.5"));
    }
}
