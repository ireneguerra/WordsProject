package org.example.client_manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class S3ClientManagerTest {

    @Test
    void testGetFileContent() {
        S3ClientManager clientManager = new S3ClientManager() {
            @Override
            public String getFileContent(String bucketName, String fileName) {
                return "Mock file content";
            }
        };

        String content = clientManager.getFileContent("bucket-name", "file.txt");
        assertEquals("Mock file content", content);
    }
}
