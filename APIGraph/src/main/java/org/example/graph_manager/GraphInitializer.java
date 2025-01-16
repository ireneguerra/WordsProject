package org.example.graph_manager;

import org.example.graph_service.GraphService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

@Component
public class GraphInitializer {
    private static final Logger logger = LoggerFactory.getLogger(GraphInitializer.class);
    private final GraphService graphService;

    @Value("${s3.bucket-name}")
    private String bucketName;

    @Value("${s3.file-name}")
    private String fileName;

    public GraphInitializer(GraphService graphService) {
        this.graphService = graphService;
    }

    @PostConstruct
    public void initializeGraph() {
        logger.info("Descargando JSON de S3 y construyendo el grafo...");
        graphService.initializeGraphFromS3(bucketName, fileName);
        logger.info("Grafo inicializado exitosamente.");
    }
}