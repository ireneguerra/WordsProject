package org.example.graph;

import java.util.List;

public class GraphData {
    private List<String> vertices;
    private List<Edge> edges;

    public static class Edge {
        private String source;
        private String target;
        private double weight;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }
    }

    public List<String> getVertices() {
        return vertices;
    }

    public void setVertices(List<String> vertices) {
        this.vertices = vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
}
