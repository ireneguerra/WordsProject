package org.example;

import org.example.graph_service.GraphService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphApiControllerTest {

    @Test
    void testGetShortestPath() {
        GraphService graphService = new GraphService() {
            @Override
            public String getShortestPath(String source, String target) {
                return "[A, B]";
            }
        };
        GraphApiController controller = new GraphApiController(graphService);

        String response = controller.getShortestPath("A", "B");
        assertEquals("Camino más corto: [A, B]", response);
    }

    @Test
    void testGetIsolatedNodes() {
        GraphService graphService = new GraphService() {
            @Override
            public String getIsolatedNodes() {
                return "[C]";
            }
        };
        GraphApiController controller = new GraphApiController(graphService);

        String response = controller.getIsolatedNodes();
        assertEquals("Nodos aislados: [C]", response);
    }
}

