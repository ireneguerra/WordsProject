package org.example.datalake_reader;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.sync.RequestBody;

import java.util.List;

public class S3Service {
    private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public List<S3Object> listFiles(String bucketName, String prefix) {
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();
        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
        return listResponse.contents();
    }
    
    public String getFileContent(String bucketName, String key) {
        S3FileReader fileReader = new S3FileReader(s3Client);
        return fileReader.getFileContent(bucketName, key);
    }

    public void saveFile(String bucketName, String destinationKey, String content) {
        try {

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(destinationKey)
                            .build(),
                    RequestBody.fromString(content)
            );

            System.out.println("Archivo guardado correctamente en " + destinationKey);
        } catch (S3Exception e) {
            System.err.println("Error al guardar el archivo en " + destinationKey + ": " + e.awsErrorDetails().errorMessage());
        }
    }

    public void deleteFile(String bucketName, String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());

            System.out.println("Archivo eliminado correctamente de " + key);
        } catch (S3Exception e) {
            System.err.println("Error al eliminar el archivo " + key + ": " + e.awsErrorDetails().errorMessage());
        }
    }
}
