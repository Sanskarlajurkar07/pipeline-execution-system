package com.vectorshift.pipeline.processor;

import com.vectorshift.pipeline.client.GeminiClient;
import com.vectorshift.pipeline.execution.ExecutionContext;
import com.vectorshift.pipeline.model.Edge;
import com.vectorshift.pipeline.model.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Tests for LLM node processor with mocked Gemini client.
 * Verifies prompt construction, edge handling, and error scenarios.
 */
@ExtendWith(MockitoExtension.class)
class LlmNodeProcessorTest {
    
    @Mock
    private GeminiClient geminiClient;
    
    private LlmNodeProcessor processor;
    
    @BeforeEach
    void setUp() {
        processor = new LlmNodeProcessor(geminiClient);
    }
    
    @Test
    void shouldReturnCorrectNodeType() {
        assertThat(processor.getNodeType()).isEqualTo("llm");
    }
    
    @Test
    void shouldCallGeminiWithNodeData() throws Exception {
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("systemInstructions", "You are helpful");
        llmNode.getData().put("prompt", "Say hello");
        llmNode.getData().put("model", "gemini-2.0-flash-exp");
        llmNode.getData().put("apiKey", "test-key");
        
        when(geminiClient.generate("You are helpful", "Say hello", "gemini-2.0-flash-exp", "test-key"))
            .thenReturn("Hello!");
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(llmNode, context);
        
        assertThat(result).isEqualTo("Hello!");
    }
    
    @Test
    void shouldUseDefaultModelWhenNotSpecified() throws Exception {
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("prompt", "Test");
        llmNode.getData().put("apiKey", "test-key");
        
        when(geminiClient.generate(any(), eq("Test"), eq("gemini-2.5-flash"), eq("test-key")))
            .thenReturn("Response");
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(llmNode, context);
        
        assertThat(result).isEqualTo("Response");
    }

    
    @Test
    void shouldOverrideSystemInstructionsFromEdge() throws Exception {
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("systemInstructions", "Original");
        llmNode.getData().put("prompt", "Test");
        llmNode.getData().put("apiKey", "test-key");
        
        ExecutionContext context = new ExecutionContext(
            Arrays.asList(new Edge("input1", "llm1", null, "llm1-system")),
            new HashMap<>()
        );
        context.setNodeOutput("input1", "Override system");
        
        when(geminiClient.generate(eq("Override system"), any(), any(), any()))
            .thenReturn("Response");
        
        String result = processor.process(llmNode, context);
        
        assertThat(result).isEqualTo("Response");
    }
    
    @Test
    void shouldOverridePromptFromEdge() throws Exception {
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("prompt", "Original");
        llmNode.getData().put("apiKey", "test-key");
        
        ExecutionContext context = new ExecutionContext(
            Arrays.asList(new Edge("input1", "llm1", null, "llm1-prompt")),
            new HashMap<>()
        );
        context.setNodeOutput("input1", "Override prompt");
        
        when(geminiClient.generate(any(), eq("Override prompt"), any(), any()))
            .thenReturn("Response");
        
        String result = processor.process(llmNode, context);
        
        assertThat(result).isEqualTo("Response");
    }
    
    @Test
    void shouldHandleApiError() throws Exception {
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("prompt", "Test");
        llmNode.getData().put("apiKey", "test-key");
        
        when(geminiClient.generate(any(), any(), any(), any()))
            .thenThrow(new RuntimeException("API Error"));
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(llmNode, context);
        
        assertThat(result).startsWith("Error:");
        assertThat(result).contains("API Error");
    }
    
    @Test
    void shouldHandleMissingApiKey() throws Exception {
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("prompt", "Test");
        // No API key provided
        
        when(geminiClient.generate(any(), any(), any(), any()))
            .thenThrow(new IllegalArgumentException("Missing API Key"));
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(llmNode, context);
        
        assertThat(result).contains("Error:");
        assertThat(result).contains("Missing API Key");
    }
    
    // ========== BUG CONDITION EXPLORATION TESTS ==========
    // Property 1: Bug Condition - Model Selection Ignored by Backend
    // CRITICAL: These tests MUST FAIL on unfixed code - failure confirms the bug exists
    // DO NOT attempt to fix the test or the code when it fails
    // NOTE: These tests encode the expected behavior - they will validate the fix when they pass after implementation
    
    @Test
    void bugExploration_shouldUseGemini25FlashWhenSelectedByUser() throws Exception {
        // User selects "gemini-2.5-flash" from frontend dropdown
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("model", "gemini-2.5-flash");
        llmNode.getData().put("prompt", "Hello, world!");
        llmNode.getData().put("apiKey", "test-key");
        
        // Mock the expected behavior: backend should use "gemini-2.5-flash"
        when(geminiClient.generate(any(), eq("Hello, world!"), eq("gemini-2.5-flash"), eq("test-key")))
            .thenReturn("Response from gemini-2.5-flash");
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(llmNode, context);
        
        // EXPECTED TO FAIL on unfixed code: backend uses "gemini-pro" instead of "gemini-2.5-flash"
        assertThat(result).isEqualTo("Response from gemini-2.5-flash");
    }
    
    @Test
    void bugExploration_shouldUseGemini20FlashWhenSelectedByUser() throws Exception {
        // User selects "gemini-2.0-flash" from frontend dropdown
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("model", "gemini-2.0-flash");
        llmNode.getData().put("prompt", "Test prompt");
        llmNode.getData().put("apiKey", "test-key");
        
        // Mock the expected behavior: backend should use "gemini-2.0-flash"
        when(geminiClient.generate(any(), eq("Test prompt"), eq("gemini-2.0-flash"), eq("test-key")))
            .thenReturn("Response from gemini-2.0-flash");
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(llmNode, context);
        
        // EXPECTED TO FAIL on unfixed code: backend uses "gemini-pro" instead of "gemini-2.0-flash"
        assertThat(result).isEqualTo("Response from gemini-2.0-flash");
    }
    
    @Test
    void bugExploration_shouldUseGemini15ProWhenSelectedByUser() throws Exception {
        // User selects "gemini-1.5-pro" from frontend dropdown
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("model", "gemini-1.5-pro");
        llmNode.getData().put("prompt", "Another test");
        llmNode.getData().put("apiKey", "test-key");
        
        // Mock the expected behavior: backend should use "gemini-1.5-pro"
        when(geminiClient.generate(any(), eq("Another test"), eq("gemini-1.5-pro"), eq("test-key")))
            .thenReturn("Response from gemini-1.5-pro");
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(llmNode, context);
        
        // EXPECTED TO FAIL on unfixed code: backend uses "gemini-pro" instead of "gemini-1.5-pro"
        assertThat(result).isEqualTo("Response from gemini-1.5-pro");
    }
    
    @Test
    void bugExploration_shouldUseGemini15FlashWhenSelectedByUser() throws Exception {
        // User selects "gemini-1.5-flash" from frontend dropdown
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("model", "gemini-1.5-flash");
        llmNode.getData().put("prompt", "Final test");
        llmNode.getData().put("apiKey", "test-key");
        
        // Mock the expected behavior: backend should use "gemini-1.5-flash"
        when(geminiClient.generate(any(), eq("Final test"), eq("gemini-1.5-flash"), eq("test-key")))
            .thenReturn("Response from gemini-1.5-flash");
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(llmNode, context);
        
        // EXPECTED TO FAIL on unfixed code: backend uses "gemini-pro" instead of "gemini-1.5-flash"
        assertThat(result).isEqualTo("Response from gemini-1.5-flash");
    }
    
    @Test
    void bugExploration_shouldUseDefaultModelWhenModelFieldMissing() throws Exception {
        // Node data does not contain "model" field (legacy node or corrupted data)
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("prompt", "Default model test");
        llmNode.getData().put("apiKey", "test-key");
        // No model field
        
        // Mock the expected behavior: backend should use "gemini-2.5-flash" as default
        when(geminiClient.generate(any(), eq("Default model test"), eq("gemini-2.5-flash"), eq("test-key")))
            .thenReturn("Response from default model");
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(llmNode, context);
        
        // EXPECTED TO FAIL on unfixed code: backend uses "gemini-pro" instead of "gemini-2.5-flash"
        assertThat(result).isEqualTo("Response from default model");
    }
    
    // ========== PRESERVATION PROPERTY TESTS ==========
    // Property 2: Preservation - Non-Model Field Processing Unchanged
    // These tests verify that existing behavior continues to work correctly after the fix
    // EXPECTED TO PASS on both unfixed and fixed code
    
    @Test
    void preservation_shouldExtractSystemInstructionsFromNodeData() throws Exception {
        // Verify system instructions extraction works correctly
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("systemInstructions", "You are a helpful assistant");
        llmNode.getData().put("prompt", "Test");
        llmNode.getData().put("apiKey", "test-key");
        
        when(geminiClient.generate(eq("You are a helpful assistant"), any(), any(), any()))
            .thenReturn("Response");
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(llmNode, context);
        
        assertThat(result).isEqualTo("Response");
    }
    
    @Test
    void preservation_shouldExtractPromptFromNodeData() throws Exception {
        // Verify prompt extraction works correctly
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("prompt", "What is the meaning of life?");
        llmNode.getData().put("apiKey", "test-key");
        
        when(geminiClient.generate(any(), eq("What is the meaning of life?"), any(), any()))
            .thenReturn("42");
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(llmNode, context);
        
        assertThat(result).isEqualTo("42");
    }
    
    @Test
    void preservation_shouldExtractApiKeyFromNodeData() throws Exception {
        // Verify API key extraction works correctly (personal key)
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("prompt", "Test");
        llmNode.getData().put("apiKey", "my-personal-key");
        
        when(geminiClient.generate(any(), any(), any(), eq("my-personal-key")))
            .thenReturn("Response");
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(llmNode, context);
        
        assertThat(result).isEqualTo("Response");
    }
    
    @Test
    void preservation_shouldUseNullApiKeyWhenNotProvided() throws Exception {
        // Verify API key fallback logic (null when not provided, GeminiClient uses default)
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("prompt", "Test");
        // No API key provided
        
        when(geminiClient.generate(any(), any(), any(), eq(null)))
            .thenReturn("Response");
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(llmNode, context);
        
        assertThat(result).isEqualTo("Response");
    }
    
    @Test
    void preservation_shouldOverrideSystemInstructionsFromEdge() throws Exception {
        // Verify edge override logic for system instructions
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("systemInstructions", "Original system");
        llmNode.getData().put("prompt", "Test");
        llmNode.getData().put("apiKey", "test-key");
        
        ExecutionContext context = new ExecutionContext(
            Arrays.asList(new Edge("input1", "llm1", null, "llm1-system")),
            new HashMap<>()
        );
        context.setNodeOutput("input1", "Overridden system from edge");
        
        when(geminiClient.generate(eq("Overridden system from edge"), any(), any(), any()))
            .thenReturn("Response");
        
        String result = processor.process(llmNode, context);
        
        assertThat(result).isEqualTo("Response");
    }
    
    @Test
    void preservation_shouldOverridePromptFromEdge() throws Exception {
        // Verify edge override logic for prompt
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("prompt", "Original prompt");
        llmNode.getData().put("apiKey", "test-key");
        
        ExecutionContext context = new ExecutionContext(
            Arrays.asList(new Edge("input1", "llm1", null, "llm1-prompt")),
            new HashMap<>()
        );
        context.setNodeOutput("input1", "Overridden prompt from edge");
        
        when(geminiClient.generate(any(), eq("Overridden prompt from edge"), any(), any()))
            .thenReturn("Response");
        
        String result = processor.process(llmNode, context);
        
        assertThat(result).isEqualTo("Response");
    }
    
    @Test
    void preservation_shouldHandleEmptySystemInstructions() throws Exception {
        // Verify empty system instructions are handled correctly
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("systemInstructions", "");
        llmNode.getData().put("prompt", "Test");
        llmNode.getData().put("apiKey", "test-key");
        
        when(geminiClient.generate(eq(""), any(), any(), any()))
            .thenReturn("Response");
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(llmNode, context);
        
        assertThat(result).isEqualTo("Response");
    }
    
    @Test
    void preservation_shouldHandleApiException() throws Exception {
        // Verify error handling returns "Error: " + message
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("prompt", "Test");
        llmNode.getData().put("apiKey", "test-key");
        
        when(geminiClient.generate(any(), any(), any(), any()))
            .thenThrow(new RuntimeException("API rate limit exceeded"));
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(llmNode, context);
        
        assertThat(result).startsWith("Error:");
        assertThat(result).contains("API rate limit exceeded");
    }
    
    @Test
    void preservation_shouldHandleMissingApiKeyException() throws Exception {
        // Verify missing API key error handling
        Node llmNode = createNode("llm1", "llm");
        llmNode.getData().put("prompt", "Test");
        // No API key
        
        when(geminiClient.generate(any(), any(), any(), any()))
            .thenThrow(new IllegalArgumentException("Missing API Key. Please provide one in the node or .env"));
        
        ExecutionContext context = new ExecutionContext(Arrays.asList(), new HashMap<>());
        
        String result = processor.process(llmNode, context);
        
        assertThat(result).startsWith("Error:");
        assertThat(result).contains("Missing API Key");
    }
    
    private Node createNode(String id, String type) {
        Node node = new Node();
        node.setId(id);
        node.setType(type);
        return node;
    }
}
