package org.example;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/graph")
public class GraphApiController {
    private final GraphService graphService;

    public GraphApiController(GraphService graphService) {
        this.graphService = graphService;
    }

    @Operation(summary = "Obtener el camino más corto entre dos nodos",
            description = "Devuelve el camino más corto desde el nodo fuente al nodo destino usando Dijkstra.")
    @GetMapping("/shortest-path")
    public String getShortestPath(
            @Parameter(description = "Nodo fuente") @RequestParam String source,
            @Parameter(description = "Nodo destino") @RequestParam String target) {
        return "Camino más corto: " + graphService.getShortestPath(source, target);
    }

    @Operation(summary = "Obtener todos los caminos entre dos nodos",
            description = "Devuelve todas las rutas posibles desde el nodo fuente al nodo destino.")
    @GetMapping("/all-paths")
    public String getAllPaths(
            @Parameter(description = "Nodo fuente") @RequestParam String source,
            @Parameter(description = "Nodo destino") @RequestParam String target) {
        return "Todos los caminos: " + graphService.getAllPaths(source, target);
    }

    @Operation(summary = "Obtener nodos aislados", description = "Devuelve los nodos que no tienen conexiones.")
    @GetMapping("/isolated-nodes")
    public String getIsolatedNodes() {
        return "Nodos aislados: " + graphService.getIsolatedNodes();
    }

    @Operation(summary = "Obtener caminos limitados entre dos nodos",
            description = "Devuelve un número limitado de caminos desde el nodo fuente al nodo destino.")
    @GetMapping("/limited-paths")
    public String getLimitedPaths(
            @Parameter(description = "Nodo fuente") @RequestParam String source,
            @Parameter(description = "Nodo destino") @RequestParam String target,
            @Parameter(description = "Número máximo de caminos") @RequestParam int k) {
        return "Caminos limitados: " + graphService.getLimitedPaths(source, target, k);
    }

    @Operation(summary = "Obtener el camino más largo entre dos nodos",
            description = "Calcula el camino más largo entre dos nodos en un grafo.")
    @GetMapping("/longest-path")
    public String getLongestPath(
            @Parameter(description = "Nodo fuente") @RequestParam String source,
            @Parameter(description = "Nodo destino") @RequestParam String target) {
        return "Camino más largo: " + graphService.getLongestPath(source, target);
    }

    @Operation(summary = "Identificar clusters", description = "Devuelve los clusters en el grafo.")
    @GetMapping("/identify-clusters")
    public String getIdentifyClusters() {
        return "Clusters: " + graphService.getIdentifyClusters();
    }

    @Operation(summary = "Nodos con alta conectividad",
            description = "Devuelve los nodos que tienen al menos un número mínimo de conexiones.")
    @GetMapping("/high-connectivity-nodes")
    public String getHighConnectivityNodes(
            @Parameter(description = "Grado mínimo de conectividad") @RequestParam int minDegree) {
        return "Nodos con alta conectividad: " + graphService.getHighConnectivityNodes(minDegree);
    }

    @Operation(summary = "Nodos con un grado específico",
            description = "Devuelve los nodos que tienen un grado exacto de conexiones.")
    @GetMapping("/nodes-with-degree")
    public String getNodesWithDegree(
            @Parameter(description = "Grado de conectividad exacto") @RequestParam int degree) {
        return "Nodos con grado " + degree + ": " + graphService.getNodesWithDegree(degree);
    }
}
