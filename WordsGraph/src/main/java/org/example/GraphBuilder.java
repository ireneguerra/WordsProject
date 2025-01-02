package org.example;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

public interface GraphBuilder {
    Graph<String, DefaultWeightedEdge> buildGraph();

}
