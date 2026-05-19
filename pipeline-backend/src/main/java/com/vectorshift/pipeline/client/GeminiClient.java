package com.vectorshift.pipeline.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Client for Google Gemini API.
 * Uses REST API to generate text responses.
 */
@Component
public class GeminiClient {
    
    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";
    
    @Value("${gemini.api.key:}")
    private String defaultApiKey;
    
    private final RestTemplate restTemplate;
    
    public GeminiClient() {
        this.restTemplate = new RestTemplate();
    }
    
    @PostConstruct
    public void init() {
        if (defaultApiKey != null && !defaultApiKey.isEmpty()) {
            log.info("Gemini API key loaded successfully (length: {})", defaultApiKey.length());
        } else {
            log.warn("Gemini API key not configured");
        }
    }
    
    /**
     * Generates text using Gemini API.
     * 
     * @param systemInstructions system-level instructions for the model
     * @param prompt user prompt
     * @param model model name (e.g., "gemini-2.0-flash-exp")
     * @param apiKey API key (uses default if null)
     * @return generated text
     * @throws Exception if API call fails
     */
    public String generate(String systemInstructions, String prompt, String model, String apiKey) throws Exception {
        // Use provided API key or fall back to default
        String key = (apiKey != null && !apiKey.trim().isEmpty()) 
            ? apiKey.trim() 
            : (defaultApiKey != null ? defaultApiKey.trim() : "");
        
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Missing API Key. Please provide one in the node or .env");
        }
        
        log.info("Calling Gemini API with model: {}, prompt length: {}", model, prompt.length());
        
        try {
            // Build request URL
            String url = String.format(GEMINI_API_URL, model, key);
            
            // Combine system instructions with prompt if present
            String fullPrompt = prompt;
            if (systemInstructions != null && !systemInstructions.isEmpty()) {
                fullPrompt = "System Instructions: " + systemInstructions + "\n\n" + prompt;
            }
            
            // Build request body (v1 API doesn't support systemInstruction)
            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of(
                        "parts", List.of(
                            Map.of("text", fullPrompt)
                        )
                    )
                ),
                "generationConfig", Map.of(
                    "temperature", 0.7,
                    "maxOutputTokens", 2048
                )
            );
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // Make API call
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            // Extract text from response
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (!parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }
            
            throw new Exception("No response from Gemini API");
            
        } catch (Exception e) {
            log.error("Gemini API call failed: {}", e.getMessage());
            throw new Exception("Gemini API error: " + e.getMessage());
        }
    }
}
