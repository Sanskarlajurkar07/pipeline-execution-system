package com.vectorshift.pipeline.processor;

import com.vectorshift.pipeline.execution.ExecutionContext;
import com.vectorshift.pipeline.model.Edge;
import com.vectorshift.pipeline.model.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for CustomOutput node processor.
 * Verifies output retrieval from first incoming edge.
 */
class CustomOutputProcessorTest {
    
    private CustomOutputProcessor processor;
    
    @BeforeEach
    void setUp() {
        processor = new CustomOutputProcessor();
    }
    
    @Test
    void shouldReturnCorrectNodeType() {
        assertThat(processor.getNodeType()).isEqualTo("customOutput");
    }
    
    @Test
    void shouldGetOutputFromFirstIncomingEdge() {
        Node outputNode = createNode("output1", "customOutput");
        
        ExecutionContext context = new ExecutionContext(
            Arrays.asList(new Edge("text1", "output1")),
            new HashMap<>()
        );
        context.setNodeOutput("text1", "Final result");
        
        String result = processor.process(outputNode, context);
        
        assertThat(result).isEqualTo("Final result");
    }
    
    @Test
    void shouldReturnEmptyStringWhenNoIncomingEdges() {
        Node outputNode = createNode("output1", "customOutput");
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(outputNode, context);
        
        assertThat(result).isEmpty();
    }
    
    @Test
    void shouldReturnEmptyStringWhenSourceOutputNotFound() {
        Node outputNode = createNode("output1", "customOutput");
        
        ExecutionContext context = new ExecutionContext(
            Arrays.asList(new Edge("text1", "output1")),
            new HashMap<>()
        );
        // text1 output not set
        
        String result = processor.process(outputNode, context);
        
        assertThat(result).isEmpty();
    }
    
    @Test
    void shouldUseFirstEdgeWhenMultipleIncomingEdges() {
        Node outputNode = createNode("output1", "customOutput");
        
        ExecutionContext context = new ExecutionContext(
            Arrays.asList(
                new Edge("text1", "output1"),
                new Edge("text2", "output1")
            ),
            new HashMap<>()
        );
        context.setNodeOutput("text1", "First");
        context.setNodeOutput("text2", "Second");
        
        String result = processor.process(outputNode, context);
        
        // Should use first edge
        assertThat(result).isEqualTo("First");
    }
    
    private Node createNode(String id, String type) {
        Node node = new Node();
        node.setId(id);
        node.setType(type);
        return node;
    }
}
