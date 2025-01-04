package org.example.crawler;

import org.example.datalake_storage.DatalakeFeeder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Random;

public class GutenbergCrawler implements BookCrawler {

    private static final int DOWNLOAD_DELAY_MS = 2000; // Tiempo de espera entre descargas
    private final DatalakeFeeder datalakeFeeder;

    public GutenbergCrawler(DatalakeFeeder datalakeFeeder) {
        this.datalakeFeeder = datalakeFeeder;
    }

    @Override
    public void downloadBooks(int startRange, int endRange, int booksToDownload) {
        Random random = new Random();
        HashSet<Integer> bookIds = new HashSet<>();

        // Generar IDs únicos aleatorios dentro del rango especificado
        while (bookIds.size() < booksToDownload) {
            int randomBookId = random.nextInt(endRange - startRange + 1) + startRange;
            bookIds.add(randomBookId);
        }

        // Descargar los libros con los IDs generados
        for (int bookId : bookIds) {
            try {
                String downloadUrl = buildBookDownloadUrl(bookId);
                if (isValidUrl(downloadUrl)) {
                    datalakeFeeder.saveData(downloadUrl, bookId);
                    System.out.println("Descargado y guardado en S3 el libro ID: " + bookId);
                } else {
                    System.out.println("No se encontró archivo de texto para el libro ID: " + bookId);
                }
                Thread.sleep(DOWNLOAD_DELAY_MS); // Esperar antes de la siguiente descarga
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