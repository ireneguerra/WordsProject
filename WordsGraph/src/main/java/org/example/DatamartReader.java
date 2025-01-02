package org.example;

import java.util.Map;

public interface DatamartReader {
    Map<String, Integer> readWordsAndWeights();
    void closeConnection();
}
