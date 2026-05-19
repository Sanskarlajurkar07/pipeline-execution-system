package com.vectorshift.pipeline.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vectorshift.pipeline.model.Edge;
import com.vectorshift.pipeline.model.Node;
import com.vectorshift.pipeline.model.request.Pipeline;
import com.vectorshift.pipeline.model.request.RunPipeline;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Pipeline REST controller.
 * Tests all three endpoints with MockMvc.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PipelineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnHealthCheckStatus() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void shouldParsePipelineSuccessfully() throws Exception {
        // Given: valid DAG pipeline
        Node input = new Node("input1", "customInput", Map.of());
        Node output = new Node("output1", "customOutput", Map.of());
        Edge edge = new Edge("input1", "output1", "value", "input");
        Pipeline pipeline = new Pipeline(List.of(input, output), List.of(edge));

        // When/Then
        mockMvc.perform(post("/pipelines/parse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pipeline)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.num_nodes").value(2))
            .andExpect(jsonPath("$.num_edges").value(1))
            .andExpect(jsonPath("$.is_dag").value(true));
    }

    @Test
    void shouldDetectCyclicGraphInParse() throws Exception {
        // Given: cyclic graph
        Node node1 = new Node("node1", "text", Map.of());
        Node node2 = new Node("node2", "text", Map.of());
        Edge edge1 = new Edge("node1", "node2", "out", "in");
        Edge edge2 = new Edge("node2", "node1", "out", "in");
        Pipeline pipeline = new Pipeline(List.of(node1, node2), List.of(edge1, edge2));

        // When/Then
        mockMvc.perform(post("/pipelines/parse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pipeline)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.num_nodes").value(2))
            .andExpect(jsonPath("$.num_edges").value(2))
            .andExpect(jsonPath("$.is_dag").value(false));
    }

    @Test
    void shouldRunPipelineSuccessfully() throws Exception {
        // Given: simple input -> output pipeline
        Node input = new Node("input1", "customInput", Map.of());
        Node output = new Node("output1", "customOutput", Map.of());
        Edge edge = new Edge("input1", "output1", "value", "input");
        RunPipeline runPipeline = new RunPipeline(
            List.of(input, output),
            List.of(edge),
            Map.of("input1", "Hello World")
        );

        // When/Then
        mockMvc.perform(post("/pipelines/run")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(runPipeline)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.outputs.input1").value("Hello World"))
            .andExpect(jsonPath("$.outputs.output1").value("Hello World"))
            .andExpect(jsonPath("$.num_nodes").value(2))
            .andExpect(jsonPath("$.num_edges").value(1))
            .andExpect(jsonPath("$.is_dag").value(true))
            .andExpect(jsonPath("$.trace").isArray())
            .andExpect(jsonPath("$.trace.length()").value(2));
    }

    @Test
    void shouldRejectCyclicGraphInRun() throws Exception {
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
        mockMvc.perform(post("/pipelines/run")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(runPipeline)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldRunPipelineWithTextNodeAndVariableSubstitution() throws Exception {
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

        // When/Then
        mockMvc.perform(post("/pipelines/run")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(runPipeline)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.outputs.text1").value("Hello Alice!"))
            .andExpect(jsonPath("$.outputs.output1").value("Hello Alice!"))
            .andExpect(jsonPath("$.num_nodes").value(3))
            .andExpect(jsonPath("$.trace.length()").value(3));
    }

    @Test
    void shouldHandleInvalidJsonInParse() throws Exception {
        // Given: invalid JSON
        String invalidJson = "{invalid json}";

        // When/Then
        mockMvc.perform(post("/pipelines/parse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleInvalidJsonInRun() throws Exception {
        // Given: invalid JSON
        String invalidJson = "{invalid json}";

        // When/Then
        mockMvc.perform(post("/pipelines/run")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleEmptyPipelineInParse() throws Exception {
        // Given: empty pipeline
        Pipeline pipeline = new Pipeline(List.of(), List.of());

        // When/Then
        mockMvc.perform(post("/pipelines/parse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pipeline)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.num_nodes").value(0))
            .andExpect(jsonPath("$.num_edges").value(0))
            .andExpect(jsonPath("$.is_dag").value(true));
    }
}
