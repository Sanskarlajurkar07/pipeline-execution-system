package com.vectorshift.pipeline.processor;

import com.vectorshift.pipeline.execution.ExecutionContext;
import com.vectorshift.pipeline.model.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for CustomInput node processor.
 * Verifies input value resolution from inputs map and node data.
 */
class CustomInputProcessorTest {
    
    private CustomInputProcessor processor;
    
    @BeforeEach
    void setUp() {
        processor = new CustomInputProcessor();
    }
    
    @Test
    void shouldReturnCorrectNodeType() {
        assertThat(processor.getNodeType()).isEqualTo("customInput");
    }
    
    @Test
    void shouldGetValueFromInputsMap() {
        // Input value provided in inputs map
        Node node = createNode("input1", "customInput");
        Map<String, String> inputs = new HashMap<>();
        inputs.put("input1", "Hello from inputs");
        ExecutionContext context = new ExecutionContext(new ArrayList<>(), inputs);
        
        String result = processor.process(node, context);
        
        assertThat(result).isEqualTo("Hello from inputs");
    }
    
    @Test
    void shouldGetValueFromNodeDataWhenNotInInputs() {
        // Input value not in inputs map, use node data
        Node node = createNode("input1", "customInput");
        node.getData().put("inputValue", "Hello from node data");
        ExecutionContext context = new ExecutionContext(new ArrayList<>(), new HashMap<>());
        
        String result = processor.process(node, context);
        
        assertThat(result).isEqualTo("Hello from node data");
    }
    
    @Test
    void shouldPreferInputsMapOverNodeData() {
        // Both inputs map and node data have values, inputs map takes precedence
        Node node = createNode("input1", "customInput");
        node.getData().put("inputValue", "From node data");
        Map<String, String> inputs = new HashMap<>();
        inputs.put("input1", "From inputs map");
        ExecutionContext context = new ExecutionContext(new ArrayList<>(), inputs);
        
        String result = processor.process(node, context);
        
        assertThat(result).isEqualTo("From inputs map");
    }
    
    @Test
    void shouldReturnEmptyStringWhenNoValueProvided() {
        // No value in inputs map or node data
        Node node = createNode("input1", "customInput");
        ExecutionContext context = new ExecutionContext(new ArrayList<>(), new HashMap<>());
        
        String result = processor.process(node, context);
        
        assertThat(result).isEmpty();
    }
    
    private Node createNode(String id, String type) {
        Node node = new Node();
        node.setId(id);
        node.setType(type);
        return node;
    }
}
