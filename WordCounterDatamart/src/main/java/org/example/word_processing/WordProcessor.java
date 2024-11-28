package org.example.word_processing;

import java.util.ArrayList;
import java.util.List;

public class WordProcessor implements StopWords {
    @Override
    public boolean isValidWord(String word) {
        // Validar si la palabra no es una stopword y cumple con el rango de longitud
        return !STOPWORDS.contains(word) && word.length() >= 3 && word.length() <= 8;
    }

    public String[] processContent(String content) {
        // Normalizar el contenido a minúsculas
        content = content.toLowerCase();

        // Usar expresión regular para extraer solo palabras alfabéticas válidas
        String[] words = content.split("[^a-zA-Z]+");

        // Filtrar palabras inválidas
        List<String> filteredWords = new ArrayList<>();
        for (String word : words) {
            if (isValidWord(word)) {
                filteredWords.add(word.trim());
            }
        }

        // Convertir la lista filtrada a un array y devolverla
        return filteredWords.toArray(new String[0]);
    }
}



