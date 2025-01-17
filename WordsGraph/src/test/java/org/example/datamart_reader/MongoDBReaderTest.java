package org.example.datamart_reader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MongoDBReaderTest {

    private MongoDBReader mongoDBReader;

    @BeforeEach
    void setUp() {
        mongoDBReader = new MongoDBReader() {
            protected MongoDBConnection getMongoDBConnection() {
                return new StubMongoDBConnection();
            }
        };
    }

    @Test
    void testReadWordsAndWeights() {
        Map<String, Integer> expected = new HashMap<>();
        expected.put("word1", 5);
        expected.put("word2", 10);

        Map<String, Integer> result = mongoDBReader.readWordsAndWeights();

        assertEquals(expected, result);
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

