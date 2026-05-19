package com.vectorshift.pipeline.processor;

import com.vectorshift.pipeline.execution.ExecutionContext;
import com.vectorshift.pipeline.model.Edge;
import com.vectorshift.pipeline.model.Node;
import org.springframework.stereotype.Component;

/**
 * Processes customOutput nodes.
 * Returns output from the first incoming edge.
 */
@Component
public class CustomOutputProcessor implements NodeProcessor {
    
    @Override
    public String process(Node node, ExecutionContext context) {
        String nodeId = node.getId();
        
        // Find first incoming edge
        for (Edge edge : context.getEdges()) {
            if (edge.getTarget().equals(nodeId)) {
                String sourceId = edge.getSource();
                String output = context.getNodeOutputs().get(sourceId);
                return output != null ? output : "";
            }
        }
        
        // No incoming edges
        return "";
    }
    
    @Override
    public String getNodeType() {
        return "customOutput";
    }
}
