package com.vectorshift.pipeline.sorter;

import com.vectorshift.pipeline.model.Edge;
import com.vectorshift.pipeline.model.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for topological sorting using Kahn's algorithm.
 * Verifies that nodes are ordered such that dependencies come before dependents.
 */
class TopologicalSorterTest {
    
    private TopologicalSorter sorter;
    
    @BeforeEach
    void setUp() {
        sorter = new TopologicalSorter();
    }
    
    @Test
    void shouldReturnEmptyListForEmptyGraph() {
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        
        List<String> result = sorter.sort(nodes, edges);
        
        assertThat(result).isEmpty();
    }
    
    @Test
    void shouldReturnSingleNodeForGraphWithOneNode() {
        List<Node> nodes = Arrays.asList(
            createNode("1", "customInput")
        );
        List<Edge> edges = new ArrayList<>();
        
        List<String> result = sorter.sort(nodes, edges);
        
        assertThat(result).containsExactly("1");
    }
    
    @Test
    void shouldSortLinearChain() {
        // Linear chain: 1 -> 2 -> 3
        List<Node> nodes = Arrays.asList(
            createNode("1", "customInput"),
            createNode("2", "text"),
            createNode("3", "customOutput")
        );
        List<Edge> edges = Arrays.asList(
            new Edge("1", "2"),
            new Edge("2", "3")
        );
        
        List<String> result = sorter.sort(nodes, edges);
        
        // Should be in order: 1, 2, 3
        assertThat(result).containsExactly("1", "2", "3");
    }
    
    @Test
    void shouldSortDiamondGraph() {
        // Diamond: 1 -> 2, 1 -> 3, 2 -> 4, 3 -> 4
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
        
        List<String> result = sorter.sort(nodes, edges);
        
        // 1 must come first, 4 must come last
        // 2 and 3 can be in any order (both depend only on 1)
        assertThat(result).hasSize(4);
        assertThat(result.get(0)).isEqualTo("1");
        assertThat(result.get(3)).isEqualTo("4");
        assertThat(result).contains("2", "3");
    }
    
    @Test
    void shouldSortGraphWithMultipleRoots() {
        // Multiple roots: 1 -> 3, 2 -> 3
        List<Node> nodes = Arrays.asList(
            createNode("1", "customInput"),
            createNode("2", "customInput"),
            createNode("3", "customOutput")
        );
        List<Edge> edges = Arrays.asList(
            new Edge("1", "3"),
            new Edge("2", "3")
        );
        
        List<String> result = sorter.sort(nodes, edges);
        
        // 1 and 2 can be in any order, but both must come before 3
        assertThat(result).hasSize(3);
        assertThat(result.get(2)).isEqualTo("3");
        assertThat(result).contains("1", "2");
    }
    
    @Test
    void shouldSortDisconnectedNodes() {
        // Disconnected nodes (no edges)
        List<Node> nodes = Arrays.asList(
            createNode("1", "customInput"),
            createNode("2", "text"),
            createNode("3", "customOutput")
        );
        List<Edge> edges = new ArrayList<>();
        
        List<String> result = sorter.sort(nodes, edges);
        
        // All nodes should be present (order doesn't matter)
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder("1", "2", "3");
    }
    
    @Test
    void shouldSortComplexDAG() {
        // Complex DAG: 1 -> 2, 1 -> 3, 2 -> 4, 3 -> 4, 3 -> 5, 4 -> 5
        List<Node> nodes = Arrays.asList(
            createNode("1", "customInput"),
            createNode("2", "text"),
            createNode("3", "text"),
            createNode("4", "llm"),
            createNode("5", "customOutput")
        );
        List<Edge> edges = Arrays.asList(
            new Edge("1", "2"),
            new Edge("1", "3"),
            new Edge("2", "4"),
            new Edge("3", "4"),
            new Edge("3", "5"),
            new Edge("4", "5")
        );
        
        List<String> result = sorter.sort(nodes, edges);
        
        // Verify all edges point forward in the ordering
        assertThat(result).hasSize(5);
        int pos1 = result.indexOf("1");
        int pos2 = result.indexOf("2");
        int pos3 = result.indexOf("3");
        int pos4 = result.indexOf("4");
        int pos5 = result.indexOf("5");
        
        // Check all dependencies are satisfied
        assertThat(pos1).isLessThan(pos2);
        assertThat(pos1).isLessThan(pos3);
        assertThat(pos2).isLessThan(pos4);
        assertThat(pos3).isLessThan(pos4);
        assertThat(pos3).isLessThan(pos5);
        assertThat(pos4).isLessThan(pos5);
    }
    
    @Test
    void shouldThrowExceptionForCyclicGraph() {
        // Cyclic graph: 1 -> 2 -> 1
        List<Node> nodes = Arrays.asList(
            createNode("1", "text"),
            createNode("2", "text")
        );
        List<Edge> edges = Arrays.asList(
            new Edge("1", "2"),
            new Edge("2", "1")
        );
        
        // Should throw exception because graph has a cycle
        assertThatThrownBy(() -> sorter.sort(nodes, edges))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cycle");
    }
    
    // Helper method to create nodes
    private Node createNode(String id, String type) {
        Node node = new Node();
        node.setId(id);
        node.setType(type);
        return node;
    }
}
