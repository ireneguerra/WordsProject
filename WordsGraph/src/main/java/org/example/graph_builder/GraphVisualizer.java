package org.example.graph_builder;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import javax.swing.*;

public class GraphVisualizer {

    public static void visualize(Graph<String, DefaultWeightedEdge> jGraphTGraph) {
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        try {
            var vertexMap = new java.util.HashMap<String, Object>();
            for (String vertex : jGraphTGraph.vertexSet()) {
                Object jGraphXNode = graph.insertVertex(parent, null, vertex, 0, 0, 80, 30);
                vertexMap.put(vertex, jGraphXNode);
            }
            for (DefaultWeightedEdge edge : jGraphTGraph.edgeSet()) {
                String source = jGraphTGraph.getEdgeSource(edge);
                String target = jGraphTGraph.getEdgeTarget(edge);
                double weight = jGraphTGraph.getEdgeWeight(edge);

                graph.insertEdge(parent, null, String.format("%.1f", weight), vertexMap.get(source), vertexMap.get(target));
            }
        } finally {
            graph.getModel().endUpdate();
        }

        mxCircleLayout layout = new mxCircleLayout(graph);
        layout.execute(graph.getDefaultParent());

        JFrame frame = new JFrame("Graph Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new mxGraphComponent(graph));
        frame.pack();
        frame.setVisible(true);
    }
}


