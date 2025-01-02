package org.example;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Map;

public class Controller {
    private final DatamartReader datamartReader;
    private final GraphBuilder graphBuilder;

    public Controller() {
        this.datamartReader = new MongoDBReader();
        this.graphBuilder = new JGraphTBuilder(null);
    }

    public void buildGraph() {
        Map<String, Integer> wordWeights = datamartReader.readWordsAndWeights();

        JGraphTBuilder graphBuilder = new JGraphTBuilder(wordWeights);
        Graph<String, DefaultWeightedEdge> graph = graphBuilder.buildGraph();

        GraphVisualizer.visualize(graph);

        System.out.println("Nodos del grafo: " + graph.vertexSet());
        for (DefaultWeightedEdge edge : graph.edgeSet()) {
            String source = graph.getEdgeSource(edge);
            String target = graph.getEdgeTarget(edge);
            double weight = graph.getEdgeWeight(edge);
            System.out.println(source + " --(" + weight + ")--> " + target);
        }
    }

    public void close() {
        datamartReader.closeConnection();
    }
}
