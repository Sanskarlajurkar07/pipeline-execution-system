package com.vectorshift.pipeline.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a node in the pipeline graph.
 * Each node has an ID, type, and associated data.
 */
public class Node {
    
    private String id;
    private String type;
    private Map<String, Object> data;
    
    public Node() {
        this.data = new HashMap<>();
    }
    
    public Node(String id, String type, Map<String, Object> data) {
        this.id = id;
        this.type = type;
        this.data = data != null ? data : new HashMap<>();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
