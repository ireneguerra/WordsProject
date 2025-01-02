package org.example.graph_to_json;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.IOException;

public interface GraphToJson {
    String convertGraphToJson(Graph<String, DefaultWeightedEdge> graph) throws IOException;
}
