package org.example.crawler;

import org.example.datalake_storage.StubDatalakeFeeder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GutenbergCrawlerTest {

    private StubDatalakeFeeder datalakeFeeder;
    private GutenbergCrawler gutenbergCrawler;

    @BeforeEach
    void setUp() {
        datalakeFeeder = new StubDatalakeFeeder();
        gutenbergCrawler = new GutenbergCrawler(datalakeFeeder);
    }

    @Test
    void testDownloadBooks_ValidUrl_Success() {
        int startRange = 1;
        int endRange = 10;
        int booksToDownload = 3;

        gutenbergCrawler.downloadBooks(startRange, endRange, booksToDownload);

        List<String> savedData = datalakeFeeder.getSavedData();
        assertEquals(booksToDownload, savedData.size());
    }

    @Test
    void testBuildBookDownloadUrl() {
        int bookId = 123;
        String expectedUrl = "https://www.gutenberg.org/cache/epub/123/pg123.txt";

        String actualUrl = gutenbergCrawler.buildBookDownloadUrl(bookId);

        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void testIsValidUrl() {
        String validUrl = "https://www.gutenberg.org/cache/epub/1/pg1.txt";
        String invalidUrl = "https://www.gutenberg.org/cache/epub/1/nonexistent.txt";

        assertTrue(gutenbergCrawler.isValidUrl(validUrl));
        assertFalse(gutenbergCrawler.isValidUrl(invalidUrl));
    }
}
