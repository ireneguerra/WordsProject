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

    /**
     * Lista los archivos en una carpeta específica dentro del bucket.
     *
     * @param bucketName Nombre del bucket.
     * @param prefix     Prefijo que representa la carpeta (por ejemplo, "libros_sin_procesar/").
     * @return Lista de objetos S3 en la carpeta.
     */
    public List<S3Object> listFiles(String bucketName, String prefix) {
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix) // Filtra archivos solo en la carpeta especificada
                .build();
        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
        return listResponse.contents();
    }

    /**
     * Obtiene el contenido de un archivo como String.
     *
     * @param bucketName Nombre del bucket.
     * @param key        Clave del archivo (path completo dentro del bucket).
     * @return Contenido del archivo como String.
     */
    public String getFileContent(String bucketName, String key) {
        S3FileReader fileReader = new S3FileReader(s3Client);
        return fileReader.getFileContent(bucketName, key);
    }

    /**
     * Guarda un archivo en S3 en una carpeta específica.
     *
     * @param bucketName     Nombre del bucket.
     * @param destinationKey Clave del archivo destino (carpeta dentro del bucket).
     * @param content        Contenido del archivo a guardar.
     */
    public void saveFile(String bucketName, String destinationKey, String content) {
        try {
            // Subir el archivo procesado a la carpeta especificada
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

    /**
     * Elimina un archivo de una carpeta específica en S3.
     *
     * @param bucketName Nombre del bucket.
     * @param key        Clave del archivo a eliminar.
     */
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
