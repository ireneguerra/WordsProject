package org.example.datalake_reader;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

public class S3ClientProvider {

    private static final Region REGION = Region.US_EAST_1;
    private static final String ENDPOINT = "https://s3.us-east-1.amazonaws.com";

    public S3Client createS3Client() {
        return S3Client.builder()
                .region(REGION)
                .endpointOverride(URI.create(ENDPOINT))
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }
}
