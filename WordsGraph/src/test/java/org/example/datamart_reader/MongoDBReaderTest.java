package org.example.datamart_reader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MongoDBReaderTest {

    private MongoDBReader mongoDBReader;

    @BeforeEach
    void setup() {
        // Limpia la colección antes de la prueba para garantizar un estado limpio
        mongoDBReader.getMongoDBConnection().getCollection("word-counter").deleteMany(new Document());
    
        // Inserta datos específicos para la prueba
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


    @Test
    void testCloseConnection() {
        // Verifica que cerrar la conexión no cause errores.
        assertDoesNotThrow(() -> mongoDBReader.closeConnection());
    }

    // Stub para MongoDBConnection
    static class StubMongoDBConnection extends MongoDBConnection {
        @Override
        public void connect() {
            System.out.println("Simulando conexión a MongoDB...");
        }

        public Map<String, Integer> readWordsAndWeights() {
            Map<String, Integer> wordWeights = new HashMap<>();
            wordWeights.put("word1", 5);
            wordWeights.put("word2", 10);
            return wordWeights;
        }
    }
}

