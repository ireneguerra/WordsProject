package org.example.graph_manager;

import org.example.graph.GraphData;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class GraphManager {
    private final Graph<String, DefaultWeightedEdge> graph;

    public GraphManager() {
        this.graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
    }

    public void loadGraphFromJson(GraphData graphData) {
        for (String vertex : graphData.getVertices()) {
            this.graph.addVertex(vertex);
        }

        for (GraphData.Edge edge : graphData.getEdges()) {
            DefaultWeightedEdge graphEdge = this.graph.addEdge(edge.getSource(), edge.getTarget());
            if (graphEdge != null) {
                this.graph.setEdgeWeight(graphEdge, edge.getWeight());
            }
        }
    }

    public Graph<String, DefaultWeightedEdge> getGraph() {
        return graph;
    }
}
