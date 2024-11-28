package org.example;

import org.example.crawler.BookCrawler;
import org.example.crawler.GutenbergCrawler;
import org.example.datalake_storage.DatalakeFeeder;
import org.example.datalake_storage.S3DatalakeFeeder;

public class Controller {
    private final BookCrawler bookCrawler;

    public Controller() {
        DatalakeFeeder datalakeFeeder = new S3DatalakeFeeder();
        this.bookCrawler = new GutenbergCrawler(datalakeFeeder);
    }

    public void startDownloadProcess(int startBookId, int booksToDownload) {
        System.out.println("Iniciando el proceso de descarga de libros...");
        bookCrawler.downloadBooks(startBookId, booksToDownload);
        System.out.println("Proceso de descarga completado.");
    }

    public static void main(String[] args) {
        Controller controller = new Controller();

        int startBookId = 1;
        int booksToDownload = 10;

        controller.startDownloadProcess(startBookId, booksToDownload);
    }
}
