package com.vectorshift.pipeline.sorter;

import com.vectorshift.pipeline.model.Edge;
import com.vectorshift.pipeline.model.Node;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Performs topological sorting on a directed acyclic graph (DAG).
 * Returns nodes in an order where dependencies are processed before dependents.
 * Uses Kahn's algorithm for sorting.
 */
@Component
public class TopologicalSorter {
    
    /**
     * Returns nodes in topological order using Kahn's algorithm.
     * Nodes with no dependencies come first, then nodes that depend on them.
     * 
     * @param nodes list of nodes in the graph
     * @param edges list of directed edges
     * @return list of node IDs in topological order
     * @throws IllegalArgumentException if the graph contains a cycle
     */
    public List<String> sort(List<Node> nodes, List<Edge> edges) {
        if (nodes == null || nodes.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Build adjacency list and calculate in-degrees
        Map<String, List<String>> adjacencyList = buildAdjacencyList(edges);
        Map<String, Integer> inDegree = calculateInDegrees(nodes, edges);
        
        // Queue for nodes with no incoming edges
        Queue<String> queue = new LinkedList<>();
        List<String> result = new ArrayList<>();
        
        // Start with nodes that have no dependencies
        for (Node node : nodes) {
            if (inDegree.get(node.getId()) == 0) {
                queue.add(node.getId());
            }
        }
        
        // Process nodes in topological order
        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);
            
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
        
        // If we didn't process all nodes, there's a cycle
        if (result.size() != nodes.size()) {
            throw new IllegalArgumentException(
                "Graph contains a cycle and cannot be topologically sorted"
            );
        }
        
        return result;
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
