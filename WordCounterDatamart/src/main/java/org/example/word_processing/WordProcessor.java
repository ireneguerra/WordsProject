package org.example.word_processing;

import java.util.ArrayList;
import java.util.List;

public class WordProcessor implements StopWords {
    @Override
    public boolean isValidWord(String word) {
        return !STOPWORDS.contains(word) && word.length() >= 3 && word.length() <= 8;
    }

    public String[] processContent(String content) {
        content = content.toLowerCase();

        String[] words = content.split("[^a-zA-Z]+");

        List<String> filteredWords = new ArrayList<>();
        for (String word : words) {
            if (isValidWord(word)) {
                filteredWords.add(word.trim());
            }
        }
        return filteredWords.toArray(new String[0]);
    }
}



