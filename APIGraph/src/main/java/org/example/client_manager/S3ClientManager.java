package org.example.client_manager;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class S3ClientManager implements ClientManager {
    private final S3Client s3;

    public S3ClientManager() {
        this.s3 = S3Client.builder()
                .region(Region.US_EAST_1) // Cambia a tu región
                .credentialsProvider(DefaultCredentialsProvider.create()) // Proveedor predeterminado
                .build();
    }

    public String getFileContent(String bucketName, String fileName) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(s3.getObject(request))
            );

            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el JSON desde S3", e);
        }
    }
}
