package org.example.datamart_storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MongoServiceTest {

    private MongoService mongoService;

    @BeforeEach
    void setUp() {
        mongoService = new MongoService();
    }

    @Test
    void testUpsertWords() {
        Map<String, Integer> wordCount = new HashMap<>();
        wordCount.put("test", 2);
        wordCount.put("example", 3);

        mongoService.upsertWords(wordCount);

        assertFalse(mongoService.isCollectionEmpty());
    }

    @Test
    void testInsertWords() {
        Map<String, Integer> wordCount = new HashMap<>();
        wordCount.put("insert", 1);

        mongoService.insertWords(wordCount);

        assertFalse(mongoService.isCollectionEmpty());
    }
}

