package org.example;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/graph")
public class GraphApiController {
    private final GraphService graphService;

    public GraphApiController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping("/shortest-path")
    public String getShortestPath(@RequestParam String source, @RequestParam String target) {
        return "Camino más corto: " + graphService.getShortestPath(source, target);
    }

    @GetMapping("/all-paths")
    public String getAllPaths(@RequestParam String source, @RequestParam String target) {
        return "Todos los caminos: " + graphService.getAllPaths(source, target);
    }

    @GetMapping("/isolated-nodes")
    public String getIsolatedNodes() {
        return "Nodos aislados: " + graphService.getIsolatedNodes();
    }

    @GetMapping("/limited-paths")
    public String getLimitedPaths(@RequestParam String source, @RequestParam String target, @RequestParam int k) {
        return "Caminos limitados: " + graphService.getLimitedPaths(source, target, k);
    }

    @GetMapping("/longest-path")
    public String getLongestPath(@RequestParam String source, @RequestParam String target) {
        return "Camino más largo: " + graphService.getLongestPath(source, target);
    }

    @GetMapping("/identify-clusters")
    public String getIdentifyClusters() {
        return "Clusters: " + graphService.getIdentifyClusters();
    }

    @GetMapping("/high-connectivity-nodes")
    public String getHighConnectivityNodes(@RequestParam int minDegree) {
        return "Nodos con alta conectividad: " + graphService.getHighConnectivityNodes(minDegree);
    }

    @GetMapping("/nodes-with-degree")
    public String getNodesWithDegree(@RequestParam int degree) {
        return "Nodos con grado " + degree + ": " + graphService.getNodesWithDegree(degree);
    }
}
