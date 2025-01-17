package org.example.json_uploader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class S3JsonUploaderTest {

    @Test
    void testUploadJson() {
        S3JsonUploader uploader = new S3JsonUploader() {
            @Override
            public void uploadJson(String fileName, String json) {
                System.out.println("Simulación: Subiendo JSON al bucket...");
                assertEquals("graph.json", fileName);
                assertTrue(json.contains("vertices"));
            }
        };

        String fileName = "graph.json";
        String json = "{ \"vertices\": [\"word1\", \"word2\"], \"edges\": [] }";

        assertDoesNotThrow(() -> uploader.uploadJson(fileName, json));
    }
}
