package org.example.datamart_reader;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class MongoDBConnection implements DatamartConnection {

    private MongoClient mongoClient;
    private MongoDatabase database;

    public void connect() {
        String host = readHostFromFile("WordCounterDatamart/src/main/resources/public_ip.txt");
        int port = 27017;

        if (host == null || host.isEmpty()) {
            throw new IllegalStateException("No se pudo leer la IP pública del archivo.");
        }

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(Collections.singletonList(new ServerAddress(host, port))))
                .build();

        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("words-project");

        System.out.println("Conexión a MongoDB establecida con éxito.");
    }

    private String readHostFromFile(String filePath) {
        try {
            return Files.readString(Paths.get(filePath)).trim();
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de IP pública: " + e.getMessage());
            return null;
        }
    }

    public MongoDatabase getDatabase() {
        if (database == null) {
            throw new IllegalStateException("Debes llamar al método connect() antes de obtener la base de datos.");
        }
        return database;
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Conexión a MongoDB cerrada.");
        }
    }
}