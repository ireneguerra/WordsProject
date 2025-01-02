package org.example;

import org.example.datamart_reader.DatamartReader;
import org.example.datamart_reader.MongoDBReader;
import org.example.graph_builder.GraphBuilder;
import org.example.graph_builder.GraphVisualizer;
import org.example.graph_builder.JGraphTBuilder;
import org.example.graph_to_json.GraphToJson;
import org.example.graph_to_json.JGraphTToJson;
import org.example.json_uploader.JsonUploader;
import org.example.json_uploader.S3JsonUploader;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.IOException;
import java.util.Map;

public class Controller {
    private final DatamartReader datamartReader;
    private final GraphBuilder graphBuilder;
    private final GraphToJson graphToJson;
    private final JsonUploader jsonUploader;

    public Controller() {
        this.datamartReader = new MongoDBReader();
        this.graphBuilder = new JGraphTBuilder(null);
        this.graphToJson = new JGraphTToJson();
        this.jsonUploader = new S3JsonUploader();
    }

    public void controller() throws IOException {
        Graph<String, DefaultWeightedEdge> graph = buildGraph();
        String graphJson = buildGraphToJson(graph);
        System.out.println(graphJson);
        saveGraph(graphJson);
    }

    private Graph<String, DefaultWeightedEdge> buildGraph() {
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
        return graph;
    }

    private String buildGraphToJson(Graph<String, DefaultWeightedEdge> graph) throws IOException {
        String graphJson = graphToJson.convertGraphToJson(graph);
        return graphJson;
    }

    private void saveGraph(String graphJson) {
        jsonUploader.uploadJson("graph.json", graphJson);
    }

    private void close() {
        datamartReader.closeConnection();
    }
}
