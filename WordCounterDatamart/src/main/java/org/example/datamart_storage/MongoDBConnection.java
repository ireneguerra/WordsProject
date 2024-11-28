package org.example.datamart_storage;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.Collections;

public class MongoDBConnection implements DatamartConnection {

    private MongoClient mongoClient;
    private MongoDatabase database;

    public void connect() {
        String host = "44.203.53.90"; // Reemplaza con la IP pública de tu instancia
        int port = 27017;

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(Collections.singletonList(new ServerAddress(host, port))))
                .build();

        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("words-project");

        System.out.println("Conexión a MongoDB establecida con éxito.");
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
