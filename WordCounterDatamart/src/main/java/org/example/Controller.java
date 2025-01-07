package org.example;

import org.example.datalake_reader.S3ClientProvider;
import org.example.datalake_reader.S3Service;
import org.example.datamart_storage.DatamartService;
import org.example.datamart_storage.MongoService;
import org.example.word_processing.WordCounter;
import org.example.word_processing.WordProcessingService;
import org.example.word_processing.WordProcessor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;

public class Controller {
    private final DatamartService datamartService;
    private final WordProcessingService wordProcessingService;
    private final S3ClientProvider s3ClientProvider;

    public Controller() {
        this.datamartService = new MongoService();
        this.wordProcessingService = new WordProcessingService(new WordProcessor(), new WordCounter());
        this.s3ClientProvider = new S3ClientProvider();
    }

    public void controller() {
        S3Client s3Client = s3ClientProvider.createS3Client();
        S3Service s3Service = new S3Service(s3Client);

        String bucketName = "bucket-datalake-books";
        String sourceFolder = "libros_sin_procesar/";
        String destinationFolder = "libros_procesados/";

        List<S3Object> files = s3Service.listFiles(bucketName, sourceFolder);

        if (files.isEmpty()) {
            System.out.println("No hay archivos para procesar en la carpeta " + sourceFolder);
            return;
        }

        for (S3Object file : files) {
            String sourceKey = file.key();
            String destinationKey = destinationFolder + sourceKey.substring(sourceFolder.length());
            System.out.println("Procesando archivo: " + sourceKey);

            try {
                String content = s3Service.getFileContent(bucketName, sourceKey);

                wordProcessingService.processFile(content);

                s3Service.saveFile(bucketName, destinationKey, content);

                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(sourceKey)
                        .build());

                System.out.println("Archivo " + sourceKey + " eliminado de " + sourceFolder);

            } catch (Exception e) {
                System.err.println("Error procesando el archivo " + sourceKey + ": " + e.getMessage());
            }
        }

        processWord();
        datamartService.closeConnection();
        System.out.println("Todos los archivos fueron procesados y guardados en la carpeta " + destinationFolder);
    }



    private void processWord() {
        if (isCollectionEmpty()) {
            System.out.println("La colección está vacía. Insertando el documento...");
            datamartService.insertWords(wordProcessingService.getGlobalWordCount());
        } else {
            System.out.println("La colección no está vacía. Actualizando o insertando el documento...");
            datamartService.upsertWords(wordProcessingService.getGlobalWordCount());
        }
    }

    private boolean isCollectionEmpty() {
        return datamartService.isCollectionEmpty();
    }
}