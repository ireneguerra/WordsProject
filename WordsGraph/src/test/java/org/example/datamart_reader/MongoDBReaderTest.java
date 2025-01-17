import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.mongodb.client.MongoCollection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MongoDBReaderTest {

    private MongoDBConnection connection;

    @BeforeEach
    void setup() {
        connection = new MongoDBConnection();
        connection.connect();
        MongoCollection<Document> collection = connection.getCollection("word-counter");
        collection.deleteMany(new Document());
        collection.insertOne(new Document("_id", "word1").append("count", 5));
        collection.insertOne(new Document("_id", "word2").append("count", 10));
    }

    @Test
    void testReadWordsAndWeights() {
        MongoCollection<Document> collection = connection.getCollection("word-counter");
        long count = collection.countDocuments();
        assertEquals(2, count, "La colección debe contener 2 documentos.");
    }
}
