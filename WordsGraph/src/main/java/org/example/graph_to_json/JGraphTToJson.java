package org.example.graph_to_json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.IOException;

public class JGraphTToJson implements GraphToJson {
    public String convertGraphToJson(Graph<String, DefaultWeightedEdge> graph) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode graphJson = mapper.createObjectNode();

        ArrayNode verticesJson = graphJson.putArray("vertices");
        for (String vertex : graph.vertexSet()) {
            verticesJson.add(vertex);
        }
        ArrayNode edgesJson = graphJson.putArray("edges");
        for (DefaultWeightedEdge edge : graph.edgeSet()) {
            ObjectNode edgeJson1 = mapper.createObjectNode();
            edgeJson1.put("source", graph.getEdgeSource(edge));
            edgeJson1.put("target", graph.getEdgeTarget(edge));
            edgeJson1.put("weight", graph.getEdgeWeight(edge));
            edgesJson.add(edgeJson1);

            ObjectNode edgeJson2 = mapper.createObjectNode();
            edgeJson2.put("source", graph.getEdgeTarget(edge));
            edgeJson2.put("target", graph.getEdgeSource(edge));
            edgeJson2.put("weight", graph.getEdgeWeight(edge));
            edgesJson.add(edgeJson2);
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(graphJson);
    }

}
