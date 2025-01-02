package org.example.graph_builder;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.Map;

public class JGraphTBuilder implements GraphBuilder {
    private final Map<String, Integer> wordWeights;

    public JGraphTBuilder(Map<String, Integer> wordWeights) {
        this.wordWeights = wordWeights;
    }

    public Graph<String, DefaultWeightedEdge> buildGraph() {
        Graph<String, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        for (String word : wordWeights.keySet()) {
            graph.addVertex(word);
        }
        for (String word1 : wordWeights.keySet()) {
            for (String word2 : wordWeights.keySet()) {
                if (!word1.equals(word2) && areConnected(word1, word2)) {
                    double weight = (wordWeights.get(word1) + wordWeights.get(word2)) / 2.0;
                    DefaultWeightedEdge edge = graph.addEdge(word1, word2);
                    if (edge != null) {
                        graph.setEdgeWeight(edge, weight);
                    }
                }
            }
        }

        return graph;
    }

    private boolean areConnected(String word1, String word2) {
        if (Math.abs(word1.length() - word2.length()) > 1) {
            return false;
        }

        int differences = 0;
        if (word1.length() == word2.length()) {
            for (int i = 0; i < word1.length(); i++) {
                if (word1.charAt(i) != word2.charAt(i)) {
                    differences++;
                }
                if (differences > 1) {
                    return false;
                }
            }
        } else {
            String longer = word1.length() > word2.length() ? word1 : word2;
            String shorter = word1.length() > word2.length() ? word2 : word1;

            for (int i = 0, j = 0; i < longer.length(); i++) {
                if (j < shorter.length() && longer.charAt(i) == shorter.charAt(j)) {
                    j++;
                } else {
                    differences++;
                }
                if (differences > 1) {
                    return false;
                }
            }
        }

        return true;
    }
}
