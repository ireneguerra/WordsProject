package org.example.datamart_reader;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import java.util.Collections;

public class MongoDBConnection implements DatamartConnection {

    private MongoClient mongoClient;
    private MongoDatabase database;

    public void connect() {
        String bucketName = "raluraluraluralu";
        String key = "public_ip.txt";
        String host = readHostFromS3(bucketName, key);
        int port = 27017;

        if (host == null || host.isEmpty()) {
            throw new IllegalStateException("No se pudo leer la IP pública del bucket.");
        }

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(Collections.singletonList(new ServerAddress(host, port))))
                .build();

        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("words-project");

        System.out.println("Conexión a MongoDB establecida con éxito.");
    }

    private String readHostFromS3(String bucketName, String key) {
        try (S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            byte[] fileBytes = s3Client.getObjectAsBytes(getObjectRequest).asByteArray();

            return new String(fileBytes).trim();

        }
    }


    public MongoDatabase getDatabase() {
        if (database == null) {
            throw new IllegalStateException("Debes llamar al método connect() antes de obtener la base de datos.");
        }
        return database;
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Conexión a MongoDB cerrada.");
        }
    }
}
