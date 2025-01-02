package org.example.datalake_storage;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.S3Exception;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class S3DatalakeFeeder implements DatalakeFeeder {

    private final S3Client s3;
    private static final String BUCKET_NAME = "bucket-datalake-gutenberg-books";

    public S3DatalakeFeeder() {
        this.s3 = S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create("https://s3.us-east-1.amazonaws.com"))
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }

    @Override
    public void saveData(String url, int dataId) {
        String fileName = "libro_" + dataId + ".txt";

        try (InputStream inputStream = new BufferedInputStream(new URL(url).openStream())) {
            s3.putObject(PutObjectRequest.builder()
                            .bucket(BUCKET_NAME)
                            .key("libros/" + fileName)
                            .build(),
                    RequestBody.fromInputStream(inputStream, inputStream.available()));

            System.out.println("Libro ID " + dataId + " subido correctamente a S3.");
        } catch (IOException | S3Exception e) {
            System.out.println("Error al guardar el libro ID: " + dataId + " en S3 - " + e.getMessage());
        }
    }
}
