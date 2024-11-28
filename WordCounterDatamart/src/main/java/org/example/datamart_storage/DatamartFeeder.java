package org.example.datamart_storage;

public interface DatamartFeeder {
    void upsertWord(String word, int count);
}
