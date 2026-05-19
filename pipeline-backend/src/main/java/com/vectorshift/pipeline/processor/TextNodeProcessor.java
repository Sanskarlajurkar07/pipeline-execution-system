package com.vectorshift.pipeline.processor;

import com.vectorshift.pipeline.execution.ExecutionContext;
import com.vectorshift.pipeline.model.Edge;
import com.vectorshift.pipeline.model.Node;
import com.vectorshift.pipeline.util.VariableSubstitutor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Processes text nodes with variable substitution.
 * Extracts variables from incoming edges and substitutes them in the template.
 */
@Component
public class TextNodeProcessor implements NodeProcessor {
    
    private final VariableSubstitutor variableSubstitutor;
    
    public TextNodeProcessor(VariableSubstitutor variableSubstitutor) {
        this.variableSubstitutor = variableSubstitutor;
    }
    
    @Override
    public String process(Node node, ExecutionContext context) {
        // Get template from node data
        Object textObj = node.getData().get("text");
        String template = textObj != null ? textObj.toString() : "";
        
        // Extract variables from incoming edges
        Map<String, String> variables = extractVariablesFromEdges(node, context);
        
        // Perform substitution
        return variableSubstitutor.substitute(template, variables);
    }
    
    /**
     * Extracts variable names and values from incoming edges.
     * Target handle format: nodeId-variableName
     */
    private Map<String, String> extractVariablesFromEdges(Node node, ExecutionContext context) {
        Map<String, String> variables = new HashMap<>();
        String nodeId = node.getId();
        
        for (Edge edge : context.getEdges()) {
            // Check if this edge targets our node
            if (edge.getTarget().equals(nodeId)) {
                String sourceId = edge.getSource();
                String targetHandle = edge.getTargetHandle();
                
                // Extract variable name from target handle
                if (targetHandle != null && targetHandle.startsWith(nodeId + "-")) {
                    String variableName = targetHandle.substring((nodeId + "-").length());
                    
                    // Get output from source node
                    String value = context.getNodeOutputs().get(sourceId);
                    if (value != null) {
                        variables.put(variableName, value);
                    }
                }
            }
        }
        
        return variables;
    }
    
    @Override
    public String getNodeType() {
        return "text";
    }
}
