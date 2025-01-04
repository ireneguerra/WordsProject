package org.example;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.neo4j.driver.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphToNeo4j {

    private static final Logger logger = LoggerFactory.getLogger(GraphToNeo4j.class);
    private final Session session;

    public GraphToNeo4j(Session session) {
        this.session = session;
    }

    public void exportGraphToNeo4j(Graph<String, DefaultWeightedEdge> graph) {
        logger.info("Iniciando la exportación del grafo dirigido a Neo4j...");

        // Exportar nodos
        graph.vertexSet().forEach(vertex -> {
            logger.info("Procesando nodo: {}", vertex);
            session.run("MERGE (n:Node {name: $name})",
                    org.neo4j.driver.Values.parameters("name", vertex));
        });
        logger.info("Exportación de nodos completada.");

        // Exportar aristas (grafo dirigido)
        graph.edgeSet().forEach(edge -> {
            String source = graph.getEdgeSource(edge);
            String target = graph.getEdgeTarget(edge);
            double weight = graph.getEdgeWeight(edge);

            logger.info("Procesando arista: {} -> {} (peso: {})", source, target, weight);

            session.run("MATCH (a:Node {name: $source}), (b:Node {name: $target}) " +
                            "MERGE (a)-[r:CONNECTED {weight: $weight}]->(b)",
                    org.neo4j.driver.Values.parameters(
                            "source", source,
                            "target", target,
                            "weight", weight
                    ));
        });
        logger.info("Exportación de aristas completada.");

        logger.info("Grafo dirigido exportado a Neo4j exitosamente.");
    }
}