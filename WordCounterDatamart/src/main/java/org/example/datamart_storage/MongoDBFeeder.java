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
    public void insertWord(String word, int count) {
        try {
            var doc = new Document("_id", word).append("count", count);
            collection.insertOne(doc);
            System.out.println("Palabra insertada: " + word + " con count: " + count);
        } catch (Exception e) {
            System.err.println("Error al insertar la palabra: " + word + ". Puede que ya exista un documento con ese _id.");
        }
    }

    public void upsertWord(String word, int count) {
        var filter = new Document("_id", word);

        var update = Updates.inc("count", count);

        var result = collection.updateOne(filter, update, new UpdateOptions().upsert(true));

        if (result.getMatchedCount() > 0) {
            System.out.println("Palabra actualizada: " + word + " con incremento en count: " + count);
        } else {
            System.out.println("Nuevo documento insertado: " + word + " con count: " + count);
        }
    }
}