package com.vectorshift.pipeline.validator;

import com.vectorshift.pipeline.model.Edge;
import com.vectorshift.pipeline.model.Node;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Validates whether a graph is a Directed Acyclic Graph (DAG).
 * Uses Kahn's algorithm for cycle detection.
 */
@Component
public class DagValidator {
    
    /**
     * Checks if the graph is acyclic using Kahn's algorithm.
     * 
     * We use Kahn's because it's straightforward and gives us the
     * in-degree calculation we need for topological sort anyway.
     * 
     * @param nodes list of nodes in the graph
     * @param edges list of directed edges
     * @return true if the graph is acyclic, false if it contains cycles
     */
    public boolean isAcyclic(List<Node> nodes, List<Edge> edges) {
        // Empty graph is trivially acyclic
        if (nodes == null || nodes.isEmpty()) {
            return true;
        }
        
        // Build adjacency list and calculate in-degrees
        Map<String, List<String>> adjacencyList = buildAdjacencyList(edges);
        Map<String, Integer> inDegree = calculateInDegrees(nodes, edges);
        
        // Queue for nodes with no incoming edges
        Queue<String> queue = new LinkedList<>();
        
        // Start with nodes that have no dependencies
        for (Node node : nodes) {
            if (inDegree.get(node.getId()) == 0) {
                queue.add(node.getId());
            }
        }
        
        int processedCount = 0;
        
        // Process nodes in topological order
        while (!queue.isEmpty()) {
            String current = queue.poll();
            processedCount++;
            
            // Reduce in-degree for all neighbors
            List<String> neighbors = adjacencyList.getOrDefault(current, new ArrayList<>());
            for (String neighbor : neighbors) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                
                // If in-degree becomes 0, add to queue
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }
        
        // If we processed all nodes, graph is acyclic
        // If some nodes remain, there's a cycle
        return processedCount == nodes.size();
    }
    
    /**
     * Builds an adjacency list representation of the graph.
     */
    private Map<String, List<String>> buildAdjacencyList(List<Edge> edges) {
        Map<String, List<String>> adjacencyList = new HashMap<>();
        
        if (edges != null) {
            for (Edge edge : edges) {
                String source = edge.getSource();
                String target = edge.getTarget();
                
                if (source != null && target != null) {
                    adjacencyList
                        .computeIfAbsent(source, k -> new ArrayList<>())
                        .add(target);
                }
            }
        }
        
        return adjacencyList;
    }
    
    /**
     * Calculates in-degree (number of incoming edges) for each node.
     */
    private Map<String, Integer> calculateInDegrees(List<Node> nodes, List<Edge> edges) {
        Map<String, Integer> inDegree = new HashMap<>();
        
        // Initialize all nodes with in-degree 0
        for (Node node : nodes) {
            inDegree.put(node.getId(), 0);
        }
        
        // Count incoming edges for each node
        if (edges != null) {
            for (Edge edge : edges) {
                String target = edge.getTarget();
                if (target != null && inDegree.containsKey(target)) {
                    inDegree.put(target, inDegree.get(target) + 1);
                }
            }
        }
        
        return inDegree;
    }
}
