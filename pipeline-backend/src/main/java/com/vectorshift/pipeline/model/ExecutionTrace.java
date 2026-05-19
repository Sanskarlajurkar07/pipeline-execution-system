package com.vectorshift.pipeline.model;

/**
 * Records execution information for a single node.
 * Includes output, timing, and error details.
 */
public class ExecutionTrace {
    
    private String node;
    private String type;
    private String output;
    private double time;
    private String error;
    
    public ExecutionTrace() {
    }
    
    public ExecutionTrace(String node, String type, String output, double time) {
        this.node = node;
        this.type = type;
        this.output = output;
        this.time = time;
        this.error = null;
    }
    
    public ExecutionTrace(String node, String type, String output, double time, String error) {
        this.node = node;
        this.type = type;
        this.output = output;
        this.time = time;
        this.error = error;
    }
    
    public String getNode() {
        return node;
    }
    
    public void setNode(String node) {
        this.node = node;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getOutput() {
        return output;
    }
    
    public void setOutput(String output) {
        this.output = output;
    }
    
    public double getTime() {
        return time;
    }
    
    public void setTime(double time) {
        this.time = time;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
}
