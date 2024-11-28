package org.example;

public class Main {

    public static void main(String[] args) {
        Controller controller = new Controller();

        int startBookId = 1;
        int booksToDownload = 10;

        controller.startDownloadProcess(startBookId, booksToDownload);
    }
}

