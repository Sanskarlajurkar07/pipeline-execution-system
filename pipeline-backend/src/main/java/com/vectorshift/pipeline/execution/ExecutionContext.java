package com.vectorshift.pipeline.execution;

import com.vectorshift.pipeline.model.Edge;
import com.vectorshift.pipeline.model.ExecutionTrace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds runtime state during pipeline execution.
 * Provides access to edges, inputs, node outputs, and execution trace.
 */
public class ExecutionContext {
    
    private final List<Edge> edges;
    private final Map<String, String> inputs;
    private final Map<String, String> nodeOutputs;
    private final List<ExecutionTrace> trace;
    
    public ExecutionContext(List<Edge> edges, Map<String, String> inputs) {
        this.edges = edges != null ? edges : new ArrayList<>();
        this.inputs = inputs != null ? inputs : new HashMap<>();
        this.nodeOutputs = new HashMap<>();
        this.trace = new ArrayList<>();
    }
    
    public List<Edge> getEdges() {
        return edges;
    }
    
    public Map<String, String> getInputs() {
        return inputs;
    }
    
    public Map<String, String> getNodeOutputs() {
        return nodeOutputs;
    }
    
    public List<ExecutionTrace> getTrace() {
        return trace;
    }
    
    /**
     * Stores output for a node.
     */
    public void setNodeOutput(String nodeId, String output) {
        nodeOutputs.put(nodeId, output);
    }
    
    /**
     * Adds an execution trace entry.
     */
    public void addTrace(ExecutionTrace traceEntry) {
        trace.add(traceEntry);
    }
}
