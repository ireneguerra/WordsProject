package org.example.datalake_reader;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class S3FileReader implements FileReader {

    private final S3Client s3Client;

    public S3FileReader(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String getFileContent(String bucketName, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(s3Client.getObject(getObjectRequest), StandardCharsets.UTF_8)
        )) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (S3Exception e) {
            throw new RuntimeException("Error de S3: " + e.awsErrorDetails().errorMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo desde S3: " + e.getMessage(), e);
        }
    }
}