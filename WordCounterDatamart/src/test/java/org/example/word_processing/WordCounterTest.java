package org.example.word_processing;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WordCounterTest {

    private final WordCounter wordCounter = new WordCounter();
    private final WordProcessor wordProcessor = new WordProcessor();

    @Test
    void testCountWords_SimpleInput() {
        String[] words = {"hello", "world", "hello"};
        Map<String, Integer> wordCount = wordCounter.countWords(words, wordProcessor);

        assertEquals(2, wordCount.get("hello"));
        assertEquals(1, wordCount.get("world"));
    }

    @Test
    void testCountWords_FilteredInput() {
        String[] words = {"hello", "world", "the"};
        Map<String, Integer> wordCount = wordCounter.countWords(words, wordProcessor);

        assertEquals(1, wordCount.get("hello"));
        assertEquals(1, wordCount.get("world"));
        assertNull(wordCount.get("the"));
    }
}
