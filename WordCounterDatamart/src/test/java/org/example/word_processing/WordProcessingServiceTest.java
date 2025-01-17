package org.example.word_processing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WordProcessingServiceTest {

    private WordProcessingService wordProcessingService;

    @BeforeEach
    void setUp() {
        wordProcessingService = new WordProcessingService(new WordProcessor(), new WordCounter());
    }

    @Test
    void testProcessFile_UpdateGlobalWordCount() {
        String content1 = "This is a simple test";
        String content2 = "Another test with simple words";

        wordProcessingService.processFile(content1);
        wordProcessingService.processFile(content2);

        Map<String, Integer> globalWordCount = wordProcessingService.getGlobalWordCount();

        assertEquals(2, globalWordCount.get("simple"));
        assertEquals(2, globalWordCount.get("test"));
        assertEquals(1, globalWordCount.get("words"));
        assertNull(globalWordCount.get("this"));
    }
}
