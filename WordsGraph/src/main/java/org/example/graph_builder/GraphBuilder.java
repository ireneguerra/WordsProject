package org.example.graph_builder;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

public interface GraphBuilder {
    Graph<String, DefaultWeightedEdge> buildGraph();

}
