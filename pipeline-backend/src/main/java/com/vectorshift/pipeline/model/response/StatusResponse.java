package com.vectorshift.pipeline.model.response;

/**
 * Response for health check endpoint.
 */
public class StatusResponse {
    
    private String status;
    
    public StatusResponse() {
        this.status = "ok";
    }
    
    public StatusResponse(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
