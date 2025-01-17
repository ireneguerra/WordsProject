import org.bson.Document;
import org.example.datamart_reader.MongoDBReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MongoDBReaderTest {

    private MongoDBReader mongoDBReader;

    @BeforeEach
    void setup() {
        mongoDBReader = new MongoDBReader();
        mongoDBReader.getMongoDBConnection().getCollection("word-counter").deleteMany(new Document());

        mongoDBReader.getMongoDBConnection().getCollection("word-counter").insertOne(
            new Document("_id", "word1").append("count", 5)
        );
        mongoDBReader.getMongoDBConnection().getCollection("word-counter").insertOne(
            new Document("_id", "word2").append("count", 10)
        );
    }

    @Test
    void testReadWordsAndWeights() {
        Map<String, Integer> expected = Map.of("word1", 5, "word2", 10);
        Map<String, Integer> result = mongoDBReader.readWordsAndWeights();
        assertEquals(expected, result, "Los datos leídos no coinciden con lo esperado.");
    }
}
