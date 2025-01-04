package org.example;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        int startRange = 1;
        int endRange = 1000;
        int booksToDownload = 50;

        controller.startDownloadProcess(startRange, endRange, booksToDownload);
    }
}