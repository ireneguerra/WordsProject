package org.example.crawler;

import org.example.datalake_storage.DatalakeFeeder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GutenbergCrawler implements BookCrawler {

    private static final int DOWNLOAD_DELAY_MS = 2000;
    private final DatalakeFeeder datalakeFeeder;

    public GutenbergCrawler(DatalakeFeeder datalakeFeeder) {
        this.datalakeFeeder = datalakeFeeder;
    }

    @Override
    public void downloadBooks(int startBookId, int booksToDownload) {
        for (int bookId = startBookId; bookId < startBookId + booksToDownload; bookId++) {
            try {
                String downloadUrl = buildBookDownloadUrl(bookId);
                if (isValidUrl(downloadUrl)) {
                    datalakeFeeder.saveData(downloadUrl, bookId);
                    System.out.println("Descargado y guardado en S3 el libro ID: " + bookId);
                } else {
                    System.out.println("No se encontró archivo de texto para el libro ID: " + bookId);
                }
                Thread.sleep(DOWNLOAD_DELAY_MS);
            } catch (Exception e) {
                System.out.println("Error al descargar el libro ID: " + bookId + " - " + e.getMessage());
            }
        }
    }

    private String buildBookDownloadUrl(int bookId) {
        return "https://www.gutenberg.org/cache/epub/" + bookId + "/pg" + bookId + ".txt";
    }

    private boolean isValidUrl(String urlString) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("HEAD");
            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            return false;
        }
    }
}
