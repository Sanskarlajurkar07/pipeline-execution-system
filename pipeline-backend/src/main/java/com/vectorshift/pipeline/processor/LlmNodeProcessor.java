package com.vectorshift.pipeline.processor;

import com.vectorshift.pipeline.client.GeminiClient;
import com.vectorshift.pipeline.execution.ExecutionContext;
import com.vectorshift.pipeline.model.Edge;
import com.vectorshift.pipeline.model.Node;
import org.springframework.stereotype.Component;

/**
 * Processes LLM nodes using Gemini API.
 * Handles system instructions, prompts, and API key configuration.
 */
@Component
public class LlmNodeProcessor implements NodeProcessor {
    
    private final GeminiClient geminiClient;
    
    public LlmNodeProcessor(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }
    
    @Override
    public String process(Node node, ExecutionContext context) {
        try {
            // Extract data from node
            String systemInstructions = getStringValue(node.getData().get("systemInstructions"), "");
            String prompt = getStringValue(node.getData().get("prompt"), "");
            // Extract model from node data, default to gemini-2.5-flash (matches frontend default)
            String model = getStringValue(node.getData().get("model"), "gemini-2.5-flash");
            String apiKey = getStringValue(node.getData().get("apiKey"), null);
            
            // Override with values from incoming edges if present
            for (Edge edge : context.getEdges()) {
                if (edge.getTarget().equals(node.getId())) {
                    String sourceId = edge.getSource();
                    String targetHandle = edge.getTargetHandle();
                    String sourceOutput = context.getNodeOutputs().get(sourceId);
                    
                    if (targetHandle != null && sourceOutput != null) {
                        if (targetHandle.endsWith("-system")) {
                            systemInstructions = sourceOutput;
                        } else if (targetHandle.endsWith("-prompt")) {
                            prompt = sourceOutput;
                        }
                    }
                }
            }
            
            // Call Gemini API (pass null if apiKey is empty to use default)
            String effectiveApiKey = (apiKey != null && !apiKey.trim().isEmpty()) ? apiKey : null;
            String response = geminiClient.generate(systemInstructions, prompt, model, effectiveApiKey);
            return response;
            
        } catch (Exception e) {
            // Return error message prefixed with "Error:"
            return "Error: " + e.getMessage();
        }
    }
    
    private String getStringValue(Object value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }
    
    @Override
    public String getNodeType() {
        return "llm";
    }
}
