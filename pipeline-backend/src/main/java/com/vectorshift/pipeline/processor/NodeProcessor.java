package com.vectorshift.pipeline.processor;

import com.vectorshift.pipeline.execution.ExecutionContext;
import com.vectorshift.pipeline.model.Node;

/**
 * Interface for processing different node types.
 * Each processor handles one specific node type.
 */
public interface NodeProcessor {
    
    /**
     * Process a single node and return its output.
     * Has access to execution context for reading upstream outputs.
     * 
     * @param node the node to process
     * @param context execution context with edges, inputs, and outputs
     * @return the output value for this node
     */
    String process(Node node, ExecutionContext context);
    
    /**
     * Returns the node type this processor handles.
     * 
     * @return node type string (e.g., "customInput", "text", "llm")
     */
    String getNodeType();
}
