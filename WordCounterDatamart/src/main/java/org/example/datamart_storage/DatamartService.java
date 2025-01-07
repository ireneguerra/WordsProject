package org.example.datamart_storage;

import java.util.Map;

public interface DatamartService {
    void upsertWords(Map<String, Integer> wordCount);
    void insertWords(Map<String, Integer> wordCount);
    boolean isCollectionEmpty();
    void closeConnection();

}
