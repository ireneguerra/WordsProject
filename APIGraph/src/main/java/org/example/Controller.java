package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Controller {
    private final GraphManager graphManager;
    private GraphQueries graphQueries;
    private final S3ClientManager s3ClientManager;
    private final String fileName = "graph.json";

    public Controller() {
        this.graphManager = new GraphManager();
        this.s3ClientManager = new S3ClientManager();
    }

    public void controller() {
        String json = loadGraphFromS3(fileName);
        GraphData graphData = convertJsonToGraphData(json);
        graphManager.loadGraphFromJson(graphData);
        this.graphQueries = new GraphQueries(graphManager.getGraph());
        String source = "near";
        String target = "yeas";
        System.out.println(graphQueries.shortestPath(source, target));
    }

    private String loadGraphFromS3(String fileName) {
        String json = s3ClientManager.getFileContent(fileName);
        return json;
    }

    private GraphData convertJsonToGraphData(String json) {

        GraphData graphData;
        try {
            graphData = new ObjectMapper().readValue(json, GraphData.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear el JSON", e);
        }
        return graphData;
    }
}
