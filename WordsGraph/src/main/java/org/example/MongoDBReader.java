package org.example;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class MongoDBReader implements DatamartReader {
    private final MongoDBConnection mongoDBConnection;

    public MongoDBReader() {
        this.mongoDBConnection = new MongoDBConnection();
        this.mongoDBConnection.connect();
    }

    public Map<String, Integer> readWordsAndWeights() {
        Map<String, Integer> wordWeights = new HashMap<>();

        MongoDatabase database = mongoDBConnection.getDatabase();
        MongoCollection<Document> collection = database.getCollection("word-counter");

        // Leer los documentos de la colección
        FindIterable<Document> documents = collection.find();

        for (Document document : documents) {
            String word = document.getString("word");
            Integer weight = document.getInteger("count");
            if (word != null && weight != null) {
                wordWeights.put(word, weight);
            }
        }
        System.out.println(wordWeights);
        System.out.println(wordWeights.size());
        return wordWeights;
    }

    public void closeConnection() {
        mongoDBConnection.close();
    }
}
