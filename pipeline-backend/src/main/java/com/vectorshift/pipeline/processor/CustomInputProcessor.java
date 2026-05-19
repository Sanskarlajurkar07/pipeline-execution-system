package com.vectorshift.pipeline.processor;

import com.vectorshift.pipeline.execution.ExecutionContext;
import com.vectorshift.pipeline.model.Node;
import org.springframework.stereotype.Component;

/**
 * Processes customInput nodes.
 * Returns value from inputs map, or falls back to node data.
 */
@Component
public class CustomInputProcessor implements NodeProcessor {
    
    @Override
    public String process(Node node, ExecutionContext context) {
        String nodeId = node.getId();
        
        // Check inputs map first
        if (context.getInputs().containsKey(nodeId)) {
            return context.getInputs().get(nodeId);
        }
        
        // Fall back to node data
        Object inputValue = node.getData().get("inputValue");
        if (inputValue != null) {
            return inputValue.toString();
        }
        
        // Default to empty string
        return "";
    }
    
    @Override
    public String getNodeType() {
        return "customInput";
    }
}
