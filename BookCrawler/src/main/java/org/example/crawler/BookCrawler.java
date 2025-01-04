package org.example.crawler;

public interface BookCrawler {
    void downloadBooks(int startRange, int endRange, int booksToDownload);
}