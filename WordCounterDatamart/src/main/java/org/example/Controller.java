package org.example;

import org.example.datalake_reader.S3ClientProvider;
import org.example.datalake_reader.S3Service;
import org.example.datamart_storage.DatamartService;
import org.example.datamart_storage.MongoService;
import org.example.word_processing.WordCounter;
import org.example.word_processing.WordProcessingService;
import org.example.word_processing.WordProcessor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;

public class Controller {
    public void controller() {
        S3ClientProvider s3ClientProvider = new S3ClientProvider();
        S3Client s3Client = s3ClientProvider.createS3Client();

        S3Service s3Service = new S3Service(s3Client);
        WordProcessingService wordProcessingService = new WordProcessingService(new WordProcessor(), new WordCounter());
        DatamartService datamartService = new MongoService();

        String bucketName = "bucket-datalake-gutenberg-irene-raul";

        List<S3Object> files = s3Service.listFiles(bucketName);

        for (S3Object file : files) {
            String key = file.key();
            System.out.println("Procesando archivo: " + key);

            try {
                String content = s3Service.getFileContent(bucketName, key);
                wordProcessingService.processFile(content);
            } catch (Exception e) {
                System.err.println("Error procesando el archivo " + key + ": " + e.getMessage());
            }
        }

        datamartService.upsertWords(wordProcessingService.getGlobalWordCount());

        datamartService.closeConnection();

        System.out.println("Todos los archivos fueron procesados y las palabras acumuladas.");
    }
}