package org.example.word_processing;

import java.util.HashMap;
import java.util.Map;

public class WordProcessingService {
    private final WordProcessor wordProcessor;
    private final WordCounter wordCounter;
    private final Map<String, Integer> globalWordCount;

    public WordProcessingService(WordProcessor wordProcessor, WordCounter wordCounter) {
        this.wordProcessor = wordProcessor;
        this.wordCounter = wordCounter;
        this.globalWordCount = new HashMap<>();
    }

    public void processFile(String content) {
        String[] processedWords = wordProcessor.processContent(content);
        Map<String, Integer> fileWordCount = wordCounter.countWords(processedWords, wordProcessor);

        fileWordCount.forEach((word, count) ->
                globalWordCount.merge(word, count, Integer::sum)
        );
    }

    public Map<String, Integer> getGlobalWordCount() {
        return globalWordCount;
    }
}

