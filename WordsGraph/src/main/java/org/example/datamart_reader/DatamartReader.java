package org.example.datamart_reader;

import java.util.Map;

public interface DatamartReader {
    Map<String, Integer> readWordsAndWeights();
    void closeConnection();
}
