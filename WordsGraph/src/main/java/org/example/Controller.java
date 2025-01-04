package org.example;

import org.example.datamart_reader.DatamartReader;
import org.example.datamart_reader.MongoDBReader;
import org.example.graph_builder.GraphBuilder;
import org.example.graph_builder.JGraphTBuilder;
import org.example.json_uploader.JsonUploader;
import org.example.json_uploader.S3JsonUploader;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.IOException;
import java.util.Map;

public class Controller {
    private final DatamartReader datamartReader;
    private final GraphBuilder graphBuilder;
    private final JsonUploader jsonUploader;
    private final Client client;
    private final GraphToNeo4j graphToNeo4j;

    public Controller() {
        this.datamartReader = new MongoDBReader();
        this.graphBuilder = new JGraphTBuilder(null);
        this.jsonUploader = new S3JsonUploader();
        this.client = new Neo4jClient("bolt://44.223.11.113/::7687", "neo4j", "navy-pyramid-leopard-graph-easy-957");
        this.graphToNeo4j = new GraphToNeo4j(client.getSession());

    }

    public void controller() throws IOException {
        Graph<String, DefaultWeightedEdge> graph = buildGraph();
        //String graphJson = buildGraphToJson(graph);
        //System.out.println(graphJson);
        //saveGraph(graphJson);
        graphToNeo4j.exportGraphToNeo4j(graph);


    }

    private Graph<String, DefaultWeightedEdge> buildGraph() {
        Map<String, Integer> wordWeights = datamartReader.readWordsAndWeights();

        JGraphTBuilder graphBuilder = new JGraphTBuilder(wordWeights);
        Graph<String, DefaultWeightedEdge> graph = graphBuilder.buildGraph();
        System.out.println("Nodos del grafo: " + graph.vertexSet());
        for (DefaultWeightedEdge edge : graph.edgeSet()) {
            String source = graph.getEdgeSource(edge);
            String target = graph.getEdgeTarget(edge);
            double weight = graph.getEdgeWeight(edge);
            System.out.println(source + " --(" + weight + ")--> " + target);
        }
        return graph;
    }

    private void close() {
        datamartReader.closeConnection();
    }
}