package org.example.word_processing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordProcessorTest {

    private final WordProcessor wordProcessor = new WordProcessor();

    @Test
    void testIsValidWord_ValidWord() {
        assertTrue(wordProcessor.isValidWord("hello"));
    }

    @Test
    void testIsValidWord_StopWord() {
        assertFalse(wordProcessor.isValidWord("the"));
    }

    @Test
    void testIsValidWord_ShortWord() {
        assertFalse(wordProcessor.isValidWord("hi"));
    }

    @Test
    void testIsValidWord_LongWord() {
        assertFalse(wordProcessor.isValidWord("supercalifragilistic"));
    }

    @Test
    void testProcessContent_FilteredWords() {
        String content = "This is a test with some valid words and stopwords.";
        String[] expectedWords = {"test", "valid", "words", "stopwords"};
        String[] processedWords = wordProcessor.processContent(content);

        assertArrayEquals(expectedWords, processedWords);
    }
}

