package org.example.json_uploader;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3JsonUploader implements JsonUploader {
    private static final String BUCKET_NAME = "bucket-words-graphs";
    private final S3Client s3;

    public S3JsonUploader() {
        this.s3 = S3Client.builder()
                .region(Region.US_EAST_1) // Configura la región según tu entorno
                .credentialsProvider(DefaultCredentialsProvider.create()) // Proveedor predeterminado
                .build();
    }

    public void uploadJson(String fileName, String json) {
        System.out.println("Guardando grafo en S3...");
        s3.putObject(PutObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(fileName)
                        .build(),
                RequestBody.fromString(json));
        System.out.println("Grafo guardado en S3.");
    }
}
