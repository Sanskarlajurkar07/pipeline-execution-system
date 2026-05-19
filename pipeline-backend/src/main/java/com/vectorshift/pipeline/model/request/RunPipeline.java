package com.vectorshift.pipeline.model.request;

import com.vectorshift.pipeline.model.Edge;
import com.vectorshift.pipeline.model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Request model for pipeline execution endpoint.
 * Includes input values for customInput nodes.
 */
public class RunPipeline {
    
    private List<Node> nodes;
    private List<Edge> edges;
    private Map<String, String> inputs;
    
    public RunPipeline() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.inputs = new HashMap<>();
    }
    
    public RunPipeline(List<Node> nodes, List<Edge> edges, Map<String, String> inputs) {
        this.nodes = nodes != null ? nodes : new ArrayList<>();
        this.edges = edges != null ? edges : new ArrayList<>();
        this.inputs = inputs != null ? inputs : new HashMap<>();
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
    
    public Map<String, String> getInputs() {
        return inputs;
    }
    
    public void setInputs(Map<String, String> inputs) {
        this.inputs = inputs;
    }
}
