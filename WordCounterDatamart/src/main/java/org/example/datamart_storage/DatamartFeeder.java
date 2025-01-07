package org.example.datamart_storage;

public interface DatamartFeeder {
    void insertWord(String word, int count);
    void upsertWord(String word, int count);
}
