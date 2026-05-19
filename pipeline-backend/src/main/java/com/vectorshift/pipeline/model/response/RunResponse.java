package com.vectorshift.pipeline.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vectorshift.pipeline.model.ExecutionTrace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Response for pipeline execution endpoint.
 * Includes outputs, execution trace, and pipeline metadata.
 */
public class RunResponse {
    
    private Map<String, String> outputs;
    private List<ExecutionTrace> trace;
    
    @JsonProperty("num_nodes")
    private int numNodes;
    
    @JsonProperty("num_edges")
    private int numEdges;
    
    @JsonProperty("is_dag")
    private boolean isDag;
    
    public RunResponse() {
        this.outputs = new HashMap<>();
        this.trace = new ArrayList<>();
    }
    
    public RunResponse(Map<String, String> outputs, List<ExecutionTrace> trace, 
                      int numNodes, int numEdges, boolean isDag) {
        this.outputs = outputs;
        this.trace = trace;
        this.numNodes = numNodes;
        this.numEdges = numEdges;
        this.isDag = isDag;
    }
    
    public Map<String, String> getOutputs() {
        return outputs;
    }
    
    public void setOutputs(Map<String, String> outputs) {
        this.outputs = outputs;
    }
    
    public List<ExecutionTrace> getTrace() {
        return trace;
    }
    
    public void setTrace(List<ExecutionTrace> trace) {
        this.trace = trace;
    }
    
    public int getNumNodes() {
        return numNodes;
    }
    
    public void setNumNodes(int numNodes) {
        this.numNodes = numNodes;
    }
    
    public int getNumEdges() {
        return numEdges;
    }
    
    public void setNumEdges(int numEdges) {
        this.numEdges = numEdges;
    }
    
    public boolean isDag() {
        return isDag;
    }
    
    public void setDag(boolean dag) {
        isDag = dag;
    }
}
