package org.example.datamart_storage;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;

public class MongoDBFeeder implements DatamartFeeder {
    private final MongoCollection<Document> collection;

    public MongoDBFeeder(MongoDatabase database, String collectionName) {
        this.collection = database.getCollection(collectionName);
    }
    public void upsertWord(String word, int count) {
        var filter = Filters.eq("word", word);
        var update = Updates.set("count", count);
        collection.updateOne(filter, update, new UpdateOptions().upsert(true));
        System.out.println("Palabra actualizada o insertada: " + word + " con count: " + count);
    }
}