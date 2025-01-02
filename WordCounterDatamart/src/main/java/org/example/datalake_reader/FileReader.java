package org.example.datalake_reader;

public interface FileReader {
    String getFileContent(String bucketName, String key);
}
