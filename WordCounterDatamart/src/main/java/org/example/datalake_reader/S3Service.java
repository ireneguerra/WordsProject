package org.example.datalake_reader;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;

public class S3Service {
    private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public List<S3Object> listFiles(String bucketName) {
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();
        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
        return listResponse.contents();
    }

    public String getFileContent(String bucketName, String key) {
        S3FileReader fileReader = new S3FileReader(s3Client);
        return fileReader.getFileContent(bucketName, key);
    }
}
