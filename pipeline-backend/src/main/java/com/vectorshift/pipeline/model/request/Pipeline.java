package com.vectorshift.pipeline.model.request;

import com.vectorshift.pipeline.model.Edge;
import com.vectorshift.pipeline.model.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Request model for pipeline parsing endpoint.
 */
public class Pipeline {
    
    private List<Node> nodes;
    private List<Edge> edges;
    
    public Pipeline() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }
    
    public Pipeline(List<Node> nodes, List<Edge> edges) {
        this.nodes = nodes != null ? nodes : new ArrayList<>();
        this.edges = edges != null ? edges : new ArrayList<>();
    }
    
    public List<Node> getNodes() {
        return nodes;
    }
    
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
    
    public List<Edge> getEdges() {
        return edges;
    }
    
    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
}
