package org.example.datalake_storage;

import java.util.ArrayList;
import java.util.List;

public class StubDatalakeFeeder implements DatalakeFeeder {
    private final List<String> savedData = new ArrayList<>();

    @Override
    public void saveData(String url, int dataId) {
        savedData.add(url);
        System.out.println("Simulación: Guardado en S3: " + url);
    }

    public List<String> getSavedData() {
        return savedData;
    }
}

