package org.example.graph_builder;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JGraphTBuilderTest {

    @Test
    void testBuildGraph() {
        Map<String, Integer> wordWeights = new HashMap<>();
        wordWeights.put("word1", 5);
        wordWeights.put("word2", 10);
        wordWeights.put("word3", 15);

        JGraphTBuilder builder = new JGraphTBuilder(wordWeights);
        Graph<String, DefaultWeightedEdge> graph = builder.buildGraph();

        assertEquals(3, graph.vertexSet().size());
        assertTrue(graph.containsVertex("word1"));
        assertTrue(graph.containsVertex("word2"));
        assertTrue(graph.containsVertex("word3"));

        assertNull(graph.getEdge("word1", "word1"));
        assertNotNull(graph.getEdge("word1", "word2"));
    }
}

