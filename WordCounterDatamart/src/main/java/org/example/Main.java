package org.example;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.controller();
    }
}
