package com.vectorshift.pipeline.execution;

import com.vectorshift.pipeline.client.GeminiClient;
import com.vectorshift.pipeline.model.Edge;
import com.vectorshift.pipeline.model.ExecutionTrace;
import com.vectorshift.pipeline.model.Node;
import com.vectorshift.pipeline.processor.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PipelineExecutorTest {

    private PipelineExecutor executor;
    private GeminiClient mockGeminiClient;

    @BeforeEach
    void setUp() {
        mockGeminiClient = mock(GeminiClient.class);
        executor = new PipelineExecutor(mockGeminiClient);
    }

    @Test
    void shouldExecuteSimplePipelineWithInputAndOutput() {
        // Given: input -> output
        Node input = new Node("input1", "customInput", Map.of());
        Node output = new Node("output1", "customOutput", Map.of());
        
        List<Node> sortedNodes = List.of(input, output);
        List<Edge> edges = List.of(
            new Edge("input1", "output1", "value", "input")
        );
        Map<String, String> inputs = Map.of("input1", "Hello World");

        // When
        ExecutionResult result = executor.execute(sortedNodes, edges, inputs);

        // Then
        assertThat(result.getNodeOutputs()).containsEntry("input1", "Hello World");
        assertThat(result.getNodeOutputs()).containsEntry("output1", "Hello World");
        assertThat(result.getExecutionTrace()).hasSize(2);
        assertThat(result.getExecutionTrace().get(0).getNode()).isEqualTo("input1");
        assertThat(result.getExecutionTrace().get(1).getNode()).isEqualTo("output1");
    }

    @Test
    void shouldExecutePipelineWithTextNodeAndVariableSubstitution() {
        // Given: input -> text -> output
        Node input = new Node("input1", "customInput", Map.of());
        Node text = new Node("text1", "text", Map.of("text", "Hello {{ value }}!"));
        Node output = new Node("output1", "customOutput", Map.of());
        
        List<Node> sortedNodes = List.of(input, text, output);
        List<Edge> edges = List.of(
            new Edge("input1", "text1", "value", "text1-value"),
            new Edge("text1", "output1", "output", "input")
        );
        Map<String, String> inputs = Map.of("input1", "Alice");

        // When
        ExecutionResult result = executor.execute(sortedNodes, edges, inputs);

        // Then
        assertThat(result.getNodeOutputs()).containsEntry("text1", "Hello Alice!");
        assertThat(result.getNodeOutputs()).containsEntry("output1", "Hello Alice!");
        assertThat(result.getExecutionTrace()).hasSize(3);
    }

    @Test
    void shouldExecutePipelineWithLlmNode() throws Exception {
        // Given: input -> llm -> output
        when(mockGeminiClient.generate(anyString(), anyString(), anyString(), anyString()))
            .thenReturn("AI response");

        Node input = new Node("input1", "customInput", Map.of());
        Node llm = new Node("llm1", "llm", Map.of(
            "model", "gemini-pro",
            "systemInstructions", "You are helpful",
            "prompt", "Answer: What is AI?"
        ));
        Node output = new Node("output1", "customOutput", Map.of());
        
        List<Node> sortedNodes = List.of(input, llm, output);
        List<Edge> edges = List.of(
            new Edge("input1", "llm1", "value", "input"),
            new Edge("llm1", "output1", "response", "input")
        );
        Map<String, String> inputs = Map.of("input1", "What is AI?");

        // When
        ExecutionResult result = executor.execute(sortedNodes, edges, inputs);

        // Then
        assertThat(result.getNodeOutputs()).containsEntry("llm1", "AI response");
        assertThat(result.getNodeOutputs()).containsEntry("output1", "AI response");
        verify(mockGeminiClient).generate(eq("You are helpful"), eq("Answer: What is AI?"), eq("gemini-pro"), anyString());
    }

    @Test
    void shouldHandleMultipleInputs() throws Exception {
        // Given: two inputs -> text (combines them) -> output
        Node input1 = new Node("input1", "customInput", Map.of());
        Node input2 = new Node("input2", "customInput", Map.of());
        Node text = new Node("text1", "text", Map.of("text", "{{ first }} and {{ second }}"));
        Node output = new Node("output1", "customOutput", Map.of());
        
        List<Node> sortedNodes = List.of(input1, input2, text, output);
        List<Edge> edges = List.of(
            new Edge("input1", "text1", "value", "text1-first"),
            new Edge("input2", "text1", "value", "text1-second"),
            new Edge("text1", "output1", "output", "input")
        );
        Map<String, String> inputs = Map.of("input1", "A", "input2", "B");

        // When
        ExecutionResult result = executor.execute(sortedNodes, edges, inputs);

        // Then
        assertThat(result.getNodeOutputs()).containsEntry("text1", "A and B");
    }

    @Test
    void shouldRecordExecutionTraceWithTimings() {
        // Given: simple input -> output
        Node input = new Node("input1", "customInput", Map.of("inputName", "test"));
        Node output = new Node("output1", "customOutput", Map.of());
        
        List<Node> sortedNodes = List.of(input, output);
        List<Edge> edges = List.of(new Edge("input1", "output1", "value", "input"));
        Map<String, String> inputs = Map.of("test", "data");

        // When
        ExecutionResult result = executor.execute(sortedNodes, edges, inputs);

        // Then
        List<ExecutionTrace> trace = result.getExecutionTrace();
        assertThat(trace).hasSize(2);
        assertThat(trace.get(0).getTime()).isGreaterThanOrEqualTo(0);
        assertThat(trace.get(1).getTime()).isGreaterThanOrEqualTo(0);
        assertThat(trace.get(0).getType()).isEqualTo("customInput");
        assertThat(trace.get(1).getType()).isEqualTo("customOutput");
    }

    @Test
    void shouldHandleProcessorError() {
        // Given: node with unknown type
        Node unknown = new Node("unknown1", "unknownType", Map.of());
        
        List<Node> sortedNodes = List.of(unknown);
        List<Edge> edges = List.of();
        Map<String, String> inputs = Map.of();

        // When
        ExecutionResult result = executor.execute(sortedNodes, edges, inputs);

        // Then
        ExecutionTrace trace = result.getExecutionTrace().get(0);
        assertThat(trace.getError()).isNotNull();
        assertThat(trace.getError()).contains("Unknown node type");
    }

    @Test
    void shouldHandleEmptyPipeline() {
        // Given: no nodes
        List<Node> sortedNodes = List.of();
        List<Edge> edges = List.of();
        Map<String, String> inputs = Map.of();

        // When
        ExecutionResult result = executor.execute(sortedNodes, edges, inputs);

        // Then
        assertThat(result.getNodeOutputs()).isEmpty();
        assertThat(result.getExecutionTrace()).isEmpty();
    }

    @Test
    void shouldHandleLlmNodeWithEdgeOverrides() throws Exception {
        // Given: input -> llm (with edge overrides) -> output
        when(mockGeminiClient.generate(anyString(), anyString(), anyString(), anyString()))
            .thenReturn("Custom response");

        Node input = new Node("input1", "customInput", Map.of());
        Node llm = new Node("llm1", "llm", Map.of(
            "model", "gemini-pro",
            "systemInstructions", "Default system",
            "prompt", "Default prompt"
        ));
        Node output = new Node("output1", "customOutput", Map.of());
        
        List<Node> sortedNodes = List.of(input, llm, output);
        List<Edge> edges = List.of(
            new Edge("input1", "llm1", "value", "llm1-system"),  // Override system
            new Edge("llm1", "output1", "response", "input")
        );
        Map<String, String> inputs = Map.of("input1", "Custom system message");

        // When
        ExecutionResult result = executor.execute(sortedNodes, edges, inputs);

        // Then
        verify(mockGeminiClient).generate(eq("Custom system message"), eq("Default prompt"), eq("gemini-pro"), anyString());
    }
}
