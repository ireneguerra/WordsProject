package org.example.datamart_storage;
import java.util.Map;

public class MongoService {
    private final MongoDBConnection mongoDBConnection;
    private final DatamartFeeder datamartFeeder;

    public MongoService() {
        this.mongoDBConnection = new MongoDBConnection();
        this.mongoDBConnection.connect();
        this.datamartFeeder = new MongoDBFeeder(mongoDBConnection.getDatabase(), "word-counter");
    }

    public void insertOrUpdateWords(Map<String, Integer> wordCount) {
        wordCount.forEach(datamartFeeder::upsertWord);
    }

    public void closeConnection() {
        mongoDBConnection.close();
    }
}
