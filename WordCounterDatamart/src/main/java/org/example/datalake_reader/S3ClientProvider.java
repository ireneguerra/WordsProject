package org.example.datalake_reader;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class S3ClientProvider {

    private static final Region REGION = Region.US_EAST_1;

    public S3Client createS3Client() {
        return S3Client.builder()
                .region(REGION) // Configura la región (puedes cambiarla si es necesario)
                .credentialsProvider(DefaultCredentialsProvider.create()) // Proveedor predeterminado
                .build();
    }
}
