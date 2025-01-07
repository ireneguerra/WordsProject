package org.example.graph_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.graph_manager.GraphManager;
import org.example.client_manager.S3ClientManager;
import org.example.graph.GraphData;
import org.springframework.stereotype.Service;

@Service
public class GraphService {
    private final GraphManager graphManager = new GraphManager();
    private GraphQueries graphQueries;
    private final S3ClientManager s3ClientManager = new S3ClientManager();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void initializeGraphFromS3(String bucketName, String fileName) {
        String json = s3ClientManager.getFileContent(bucketName, fileName);
        GraphData graphData = convertJsonToGraphData(json);
        graphManager.loadGraphFromJson(graphData);
        this.graphQueries = new GraphQueries(graphManager.getGraph());
    }

    public String getShortestPath(String source, String target) {
        return String.valueOf(graphQueries.shortestPath(source, target));
    }

    public String getIsolatedNodes() {
        return String.valueOf(graphQueries.isolatedNodes());
    }

    public String getAllPaths(String source, String target, int maxDepth) {
        return String.valueOf(graphQueries.allPaths(source, target, maxDepth));
    }

    public String getLongestPath(String source, String target, int maxDepth) {
        return String.valueOf(graphQueries.longestPath(source, target, maxDepth));
    }

    public String getIdentifyClusters() {
        return String.valueOf(graphQueries.identifyClusters());
    }

    public String getHighConnectivityNodes(int minDegree) {
        return String.valueOf(graphQueries.highConnectivityNodes(minDegree));
    }

    public String getNodesWithDegree(int degree) {
        return String.valueOf(graphQueries.nodesWithDegree(degree));
    }

    private GraphData convertJsonToGraphData(String json) {
        try {
            return objectMapper.readValue(json, GraphData.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear el JSON", e);
        }
    }
}
