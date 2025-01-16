package org.example.datalake_storage;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class S3DatalakeFeeder implements DatalakeFeeder {

    private final S3Client s3;
    private static final String BUCKET_NAME = "bucket-datalake-books";
    private static final String PROCESSED_FOLDER = "libros_procesados/";
    private static final String UNPROCESSED_FOLDER = "libros_sin_procesar/";

    public S3DatalakeFeeder() {
        this.s3 = S3Client.builder()
                .region(Region.US_EAST_1) // Configura la región según tus necesidades
                .credentialsProvider(DefaultCredentialsProvider.create()) // Proveedor predeterminado
                .build();
    }

    @Override
    public void saveData(String url, int dataId) {
        String fileName = "libro_" + dataId + ".txt";

        if (isFileInProcessedFolder(fileName)) {
            System.out.println("El libro ID " + dataId + " ya existe en 'libros_procesados/'. Se omitirá la descarga.");
            return;
        }

        try (InputStream inputStream = new BufferedInputStream(new URL(url).openStream())) {
            s3.putObject(PutObjectRequest.builder()
                            .bucket(BUCKET_NAME)
                            .key(UNPROCESSED_FOLDER + fileName)
                            .build(),
                    RequestBody.fromInputStream(inputStream, inputStream.available()));

            System.out.println("Libro ID " + dataId + " subido correctamente a S3.");
        } catch (IOException | S3Exception e) {
            System.out.println("Error al guardar el libro ID: " + dataId + " en S3 - " + e.getMessage());
        }
    }

    private boolean isFileInProcessedFolder(String fileName) {
        try {
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(BUCKET_NAME)
                    .prefix(PROCESSED_FOLDER)
                    .build();

            ListObjectsV2Response listResponse = s3.listObjectsV2(listRequest);
            if (listResponse.contents().isEmpty()) {
                System.out.println("La carpeta 'libros_procesados/' no existe o está vacía. Se procederá normalmente.");
                return false;
            }

            List<String> existingFiles = listResponse.contents().stream()
                    .map(s3Object -> s3Object.key().replace(PROCESSED_FOLDER, ""))
                    .collect(Collectors.toList());

            return existingFiles.contains(fileName);
        } catch (S3Exception e) {
            System.err.println("Error al listar los archivos en 'libros_procesados/': " + e.awsErrorDetails().errorMessage());
            return false;
        }
    }
}
