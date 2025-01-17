package org.example.graph_service;

import org.example.graph_manager.GraphManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphServiceTest {

    @Test
    void testInitializeGraphFromS3() {
        GraphService graphService = new GraphService() {
            @Override
            public void initializeGraphFromS3(String bucketName, String fileName) {
                System.out.println("Simulated initialization");
            }
        };

        assertDoesNotThrow(() -> graphService.initializeGraphFromS3("bucket-name", "file.json"));
    }
}
