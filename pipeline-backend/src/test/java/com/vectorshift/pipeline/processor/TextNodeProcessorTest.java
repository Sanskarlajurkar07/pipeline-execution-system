package com.vectorshift.pipeline.processor;

import com.vectorshift.pipeline.execution.ExecutionContext;
import com.vectorshift.pipeline.model.Edge;
import com.vectorshift.pipeline.model.Node;
import com.vectorshift.pipeline.util.VariableSubstitutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Text node processor.
 * Verifies variable extraction from edges and template substitution.
 */
class TextNodeProcessorTest {
    
    private TextNodeProcessor processor;
    
    @BeforeEach
    void setUp() {
        processor = new TextNodeProcessor(new VariableSubstitutor());
    }
    
    @Test
    void shouldReturnCorrectNodeType() {
        assertThat(processor.getNodeType()).isEqualTo("text");
    }
    
    @Test
    void shouldSubstituteVariablesFromIncomingEdges() {
        // Text node with template and incoming edge
        Node textNode = createNode("text1", "text");
        textNode.getData().put("text", "Hello {{ name }}!");
        
        // Setup context with upstream output
        ExecutionContext context = new ExecutionContext(
            Arrays.asList(new Edge("input1", "text1", null, "text1-name")),
            new HashMap<>()
        );
        context.setNodeOutput("input1", "World");
        
        String result = processor.process(textNode, context);
        
        assertThat(result).isEqualTo("Hello World!");
    }
    
    @Test
    void shouldExtractVariableNameFromTargetHandle() {
        // Target handle format: nodeId-variableName
        Node textNode = createNode("text1", "text");
        textNode.getData().put("text", "{{ greeting }} {{ name }}");
        
        ExecutionContext context = new ExecutionContext(
            Arrays.asList(
                new Edge("input1", "text1", null, "text1-greeting"),
                new Edge("input2", "text1", null, "text1-name")
            ),
            new HashMap<>()
        );
        context.setNodeOutput("input1", "Hi");
        context.setNodeOutput("input2", "Alice");
        
        String result = processor.process(textNode, context);
        
        assertThat(result).isEqualTo("Hi Alice");
    }
    
    @Test
    void shouldHandleNoIncomingEdges() {
        // Text node with no incoming edges
        Node textNode = createNode("text1", "text");
        textNode.getData().put("text", "Static text");
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(textNode, context);
        
        assertThat(result).isEqualTo("Static text");
    }
    
    @Test
    void shouldHandleEmptyTemplate() {
        Node textNode = createNode("text1", "text");
        textNode.getData().put("text", "");
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(textNode, context);
        
        assertThat(result).isEmpty();
    }
    
    @Test
    void shouldHandleMissingTextData() {
        // Node data doesn't have "text" field
        Node textNode = createNode("text1", "text");
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(textNode, context);
        
        assertThat(result).isEmpty();
    }
    
    private Node createNode(String id, String type) {
        Node node = new Node();
        node.setId(id);
        node.setType(type);
        return node;
    }
}
