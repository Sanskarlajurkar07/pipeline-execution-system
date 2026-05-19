package com.vectorshift.pipeline.model;

/**
 * Represents a directed edge connecting two nodes in the pipeline.
 */
public class Edge {
    
    private String source;
    private String target;
    private String sourceHandle;
    private String targetHandle;
    
    public Edge() {
    }
    
    public Edge(String source, String target) {
        this.source = source;
        this.target = target;
    }
    
    public Edge(String source, String target, String sourceHandle, String targetHandle) {
        this.source = source;
        this.target = target;
        this.sourceHandle = sourceHandle;
        this.targetHandle = targetHandle;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getTarget() {
        return target;
    }
    
    public void setTarget(String target) {
        this.target = target;
    }
    
    public String getSourceHandle() {
        return sourceHandle;
    }
    
    public void setSourceHandle(String sourceHandle) {
        this.sourceHandle = sourceHandle;
    }
    
    public String getTargetHandle() {
        return targetHandle;
    }
    
    public void setTargetHandle(String targetHandle) {
        this.targetHandle = targetHandle;
    }
}
