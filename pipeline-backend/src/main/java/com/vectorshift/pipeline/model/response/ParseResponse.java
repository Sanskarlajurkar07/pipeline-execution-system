package com.vectorshift.pipeline.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response for pipeline parse endpoint.
 * Returns node/edge counts and DAG validation result.
 */
public class ParseResponse {
    
    @JsonProperty("num_nodes")
    private int numNodes;
    
    @JsonProperty("num_edges")
    private int numEdges;
    
    @JsonProperty("is_dag")
    private boolean isDag;
    
    public ParseResponse() {
    }
    
    public ParseResponse(int numNodes, int numEdges, boolean isDag) {
        this.numNodes = numNodes;
        this.numEdges = numEdges;
        this.isDag = isDag;
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
