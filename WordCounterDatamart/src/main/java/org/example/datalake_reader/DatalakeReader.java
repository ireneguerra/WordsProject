package org.example.datalake_reader;

public interface DatalakeReader {
    String readBook(String bucketName, String bookPrefix);
}




