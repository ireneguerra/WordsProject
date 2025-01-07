package org.example.word_processing;

import java.util.HashMap;
import java.util.Map;

public class WordCounter {

    public Map<String, Integer> countWords(String[] words, WordProcessor processor) {
        Map<String, Integer> wordCount = new HashMap<>();

        for (String word : words) {
            if (processor.isValidWord(word)) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }
        return wordCount;
    }
}

