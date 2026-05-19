package com.vectorshift.pipeline.validator;

import com.vectorshift.pipeline.model.Edge;
import com.vectorshift.pipeline.model.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DAG validation using Kahn's algorithm.
 * Tests various graph structures to ensure correct cycle detection.
 */
class DagValidatorTest {
    
    private DagValidator validator;
    
    @BeforeEach
    void setUp() {
        validator = new DagValidator();
    }
    
    @Test
    void shouldReturnTrueForEmptyGraph() {
        // Empty graph is trivially acyclic
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        
        boolean result = validator.isAcyclic(nodes, edges);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldReturnTrueForSingleNode() {
        // Single node with no edges is acyclic
        List<Node> nodes = Arrays.asList(
            createNode("1", "customInput")
        );
        List<Edge> edges = new ArrayList<>();
        
        boolean result = validator.isAcyclic(nodes, edges);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldReturnTrueForLinearChain() {
        // Linear chain: 1 -> 2 -> 3 (no cycles)
        List<Node> nodes = Arrays.asList(
            createNode("1", "customInput"),
            createNode("2", "text"),
            createNode("3", "customOutput")
        );
        List<Edge> edges = Arrays.asList(
            new Edge("1", "2"),
            new Edge("2", "3")
        );
        
        boolean result = validator.isAcyclic(nodes, edges);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldReturnTrueForDisconnectedNodes() {
        // Multiple disconnected nodes (no edges between them)
        List<Node> nodes = Arrays.asList(
            createNode("1", "customInput"),
            createNode("2", "text"),
            createNode("3", "customOutput")
        );
        List<Edge> edges = new ArrayList<>();
        
        boolean result = validator.isAcyclic(nodes, edges);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldReturnTrueForDiamondGraph() {
        // Diamond: 1 -> 2, 1 -> 3, 2 -> 4, 3 -> 4 (no cycles)
        List<Node> nodes = Arrays.asList(
            createNode("1", "customInput"),
            createNode("2", "text"),
            createNode("3", "text"),
            createNode("4", "customOutput")
        );
        List<Edge> edges = Arrays.asList(
            new Edge("1", "2"),
            new Edge("1", "3"),
            new Edge("2", "4"),
            new Edge("3", "4")
        );
        
        boolean result = validator.isAcyclic(nodes, edges);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void shouldReturnFalseForSimpleCycle() {
        // Simple cycle: 1 -> 2 -> 1
        List<Node> nodes = Arrays.asList(
            createNode("1", "text"),
            createNode("2", "text")
        );
        List<Edge> edges = Arrays.asList(
            new Edge("1", "2"),
            new Edge("2", "1")
        );
        
        boolean result = validator.isAcyclic(nodes, edges);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldReturnFalseForSelfLoop() {
        // Self-loop: 1 -> 1
        List<Node> nodes = Arrays.asList(
            createNode("1", "text")
        );
        List<Edge> edges = Arrays.asList(
            new Edge("1", "1")
        );
        
        boolean result = validator.isAcyclic(nodes, edges);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldReturnFalseForLongerCycle() {
        // Longer cycle: 1 -> 2 -> 3 -> 1
        List<Node> nodes = Arrays.asList(
            createNode("1", "text"),
            createNode("2", "text"),
            createNode("3", "text")
        );
        List<Edge> edges = Arrays.asList(
            new Edge("1", "2"),
            new Edge("2", "3"),
            new Edge("3", "1")
        );
        
        boolean result = validator.isAcyclic(nodes, edges);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void shouldReturnFalseForCycleInLargerGraph() {
        // Graph with cycle: 1 -> 2 -> 3 -> 2 (cycle between 2 and 3)
        List<Node> nodes = Arrays.asList(
            createNode("1", "customInput"),
            createNode("2", "text"),
            createNode("3", "text"),
            createNode("4", "customOutput")
        );
        List<Edge> edges = Arrays.asList(
            new Edge("1", "2"),
            new Edge("2", "3"),
            new Edge("3", "2"),  // Creates cycle
            new Edge("3", "4")
        );
        
        boolean result = validator.isAcyclic(nodes, edges);
        
        assertThat(result).isFalse();
    }
    
    // Helper method to create nodes
    private Node createNode(String id, String type) {
        Node node = new Node();
        node.setId(id);
        node.setType(type);
        return node;
    }
}
