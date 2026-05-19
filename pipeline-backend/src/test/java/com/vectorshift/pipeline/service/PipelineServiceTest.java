package com.vectorshift.pipeline.service;

import com.vectorshift.pipeline.model.Edge;
import com.vectorshift.pipeline.model.Node;
import com.vectorshift.pipeline.model.request.Pipeline;
import com.vectorshift.pipeline.model.request.RunPipeline;
import com.vectorshift.pipeline.model.response.ParseResponse;
import com.vectorshift.pipeline.model.response.RunResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for Pipeline service layer.
 * Verifies parse and run pipeline operations.
 */
class PipelineServiceTest {

    private PipelineService service;

    @BeforeEach
    void setUp() {
        service = new PipelineService();
    }

    @Test
    void shouldParsePipelineSuccessfully() {
        // Given: valid DAG pipeline
        Node input = new Node("input1", "customInput", Map.of());
        Node output = new Node("output1", "customOutput", Map.of());
        Edge edge = new Edge("input1", "output1", "value", "input");
        
        Pipeline pipeline = new Pipeline(List.of(input, output), List.of(edge));

        // When
        ParseResponse response = service.parsePipeline(pipeline);

        // Then
        assertThat(response.getNumNodes()).isEqualTo(2);
        assertThat(response.getNumEdges()).isEqualTo(1);
        assertThat(response.isDag()).isTrue();
    }

    @Test
    void shouldDetectCyclicGraph() {
        // Given: cyclic graph
        Node node1 = new Node("node1", "text", Map.of());
        Node node2 = new Node("node2", "text", Map.of());
        Edge edge1 = new Edge("node1", "node2", "out", "in");
        Edge edge2 = new Edge("node2", "node1", "out", "in");
        
        Pipeline pipeline = new Pipeline(List.of(node1, node2), List.of(edge1, edge2));

        // When
        ParseResponse response = service.parsePipeline(pipeline);

        // Then
        assertThat(response.getNumNodes()).isEqualTo(2);
        assertThat(response.getNumEdges()).isEqualTo(2);
        assertThat(response.isDag()).isFalse();
    }

    @Test
    void shouldHandleEmptyPipeline() {
        // Given: empty pipeline
        Pipeline pipeline = new Pipeline(List.of(), List.of());

        // When
        ParseResponse response = service.parsePipeline(pipeline);

        // Then
        assertThat(response.getNumNodes()).isEqualTo(0);
        assertThat(response.getNumEdges()).isEqualTo(0);
        assertThat(response.isDag()).isTrue();
    }

    @Test
    void shouldRunPipelineSuccessfully() {
        // Given: simple input -> output pipeline
        Node input = new Node("input1", "customInput", Map.of());
        Node output = new Node("output1", "customOutput", Map.of());
        Edge edge = new Edge("input1", "output1", "value", "input");
        
        RunPipeline runPipeline = new RunPipeline(
            List.of(input, output),
            List.of(edge),
            Map.of("input1", "Hello World")
        );

        // When
        RunResponse response = service.runPipeline(runPipeline);

        // Then
        assertThat(response.getOutputs()).containsEntry("input1", "Hello World");
        assertThat(response.getOutputs()).containsEntry("output1", "Hello World");
        assertThat(response.getTrace()).hasSize(2);
        assertThat(response.getNumNodes()).isEqualTo(2);
        assertThat(response.getNumEdges()).isEqualTo(1);
        assertThat(response.isDag()).isTrue();
    }

    @Test
    void shouldFailOnCyclicGraphDuringRun() {
        // Given: cyclic graph
        Node node1 = new Node("node1", "text", Map.of());
        Node node2 = new Node("node2", "text", Map.of());
        Edge edge1 = new Edge("node1", "node2", "out", "in");
        Edge edge2 = new Edge("node2", "node1", "out", "in");
        
        RunPipeline runPipeline = new RunPipeline(
            List.of(node1, node2),
            List.of(edge1, edge2),
            Map.of()
        );

        // When/Then
        assertThatThrownBy(() -> service.runPipeline(runPipeline))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Pipeline contains a cycle");
    }

    @Test
    void shouldRunPipelineWithTextNodeAndVariableSubstitution() {
        // Given: input -> text -> output
        Node input = new Node("input1", "customInput", Map.of());
        Node text = new Node("text1", "text", Map.of("text", "Hello {{ name }}!"));
        Node output = new Node("output1", "customOutput", Map.of());
        
        RunPipeline runPipeline = new RunPipeline(
            List.of(input, text, output),
            List.of(
                new Edge("input1", "text1", "value", "text1-name"),
                new Edge("text1", "output1", "output", "input")
            ),
            Map.of("input1", "Alice")
        );

        // When
        RunResponse response = service.runPipeline(runPipeline);

        // Then
        assertThat(response.getOutputs()).containsEntry("text1", "Hello Alice!");
        assertThat(response.getOutputs()).containsEntry("output1", "Hello Alice!");
        assertThat(response.getNumNodes()).isEqualTo(3);
        assertThat(response.getNumEdges()).isEqualTo(2);
    }

    @Test
    void shouldHandleExecutionErrors() {
        // Given: pipeline with unknown node type
        Node unknown = new Node("unknown1", "unknownType", Map.of());
        
        RunPipeline runPipeline = new RunPipeline(
            List.of(unknown),
            List.of(),
            Map.of()
        );

        // When
        RunResponse response = service.runPipeline(runPipeline);

        // Then
        assertThat(response.getTrace()).hasSize(1);
        assertThat(response.getTrace().get(0).getError()).isNotNull();
    }

    @Test
    void shouldHandleSingleNodePipeline() {
        // Given: single input node
        Node input = new Node("input1", "customInput", Map.of());
        
        RunPipeline runPipeline = new RunPipeline(
            List.of(input),
            List.of(),
            Map.of("input1", "Test Value")
        );

        // When
        RunResponse response = service.runPipeline(runPipeline);

        // Then
        assertThat(response.getOutputs()).containsEntry("input1", "Test Value");
        assertThat(response.getTrace()).hasSize(1);
        assertThat(response.getNumNodes()).isEqualTo(1);
        assertThat(response.getNumEdges()).isEqualTo(0);
    }
}
