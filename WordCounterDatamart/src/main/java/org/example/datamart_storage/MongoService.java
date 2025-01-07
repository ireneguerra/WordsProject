package org.example.datamart_storage;
import java.util.Map;
import com.mongodb.client.MongoCollection;
import org.bson.Document;


public class MongoService implements DatamartService {
    private final MongoDBConnection mongoDBConnection;
    private final DatamartFeeder datamartFeeder;
    private final MongoCollection<Document> mongoCollection;

    public MongoService() {
        this.mongoDBConnection = new MongoDBConnection();
        this.mongoDBConnection.connect();
        this.datamartFeeder = new MongoDBFeeder(mongoDBConnection.getDatabase(), "word-counter");
        this.mongoCollection = mongoDBConnection.getCollection("word-counter");

    }

    public void upsertWords(Map<String, Integer> wordCount) {
        wordCount.forEach(datamartFeeder::upsertWord);
    }

    public void insertWords(Map<String, Integer> wordCount) {
        wordCount.forEach(datamartFeeder::insertWord);
    }

    public boolean isCollectionEmpty() {
        long count = mongoCollection.countDocuments();
        return count == 0;
    }

    public void closeConnection() {
        mongoDBConnection.close();
    }
}
