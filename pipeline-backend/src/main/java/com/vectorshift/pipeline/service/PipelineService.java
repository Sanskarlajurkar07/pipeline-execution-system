package com.vectorshift.pipeline.service;

import com.vectorshift.pipeline.client.GeminiClient;
import com.vectorshift.pipeline.execution.ExecutionResult;
import com.vectorshift.pipeline.execution.PipelineExecutor;
import com.vectorshift.pipeline.model.Edge;
import com.vectorshift.pipeline.model.Node;
import com.vectorshift.pipeline.model.request.Pipeline;
import com.vectorshift.pipeline.model.request.RunPipeline;
import com.vectorshift.pipeline.model.response.ParseResponse;
import com.vectorshift.pipeline.model.response.RunResponse;
import com.vectorshift.pipeline.sorter.TopologicalSorter;
import com.vectorshift.pipeline.validator.DagValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service layer for pipeline operations.
 * Coordinates DAG validation, topological sorting, and execution.
 */
@Service
public class PipelineService {

    private final DagValidator dagValidator;
    private final TopologicalSorter topologicalSorter;
    private final PipelineExecutor pipelineExecutor;

    public PipelineService(GeminiClient geminiClient) {
        this.dagValidator = new DagValidator();
        this.topologicalSorter = new TopologicalSorter();
        this.pipelineExecutor = new PipelineExecutor(geminiClient);
    }

    /**
     * Parses a pipeline and validates if it's a DAG.
     * 
     * @param pipeline the pipeline to parse
     * @return parse response with node/edge counts and DAG status
     */
    public ParseResponse parsePipeline(Pipeline pipeline) {
        List<Node> nodes = pipeline.getNodes();
        List<Edge> edges = pipeline.getEdges();

        int numNodes = nodes.size();
        int numEdges = edges.size();
        boolean isDAG = dagValidator.isAcyclic(nodes, edges);

        return new ParseResponse(numNodes, numEdges, isDAG);
    }

    /**
     * Runs a pipeline by validating, sorting, and executing nodes.
     * 
     * @param runPipeline the pipeline to run with inputs
     * @return run response with execution results
     * @throws IllegalArgumentException if pipeline contains a cycle
     */
    public RunResponse runPipeline(RunPipeline runPipeline) {
        List<Node> nodes = runPipeline.getNodes();
        List<Edge> edges = runPipeline.getEdges();

        // Validate DAG
        if (!dagValidator.isAcyclic(nodes, edges)) {
            throw new IllegalArgumentException("Pipeline contains a cycle and cannot be executed");
        }

        // Sort nodes topologically (returns node IDs)
        List<String> sortedNodeIds = topologicalSorter.sort(nodes, edges);
        
        // Convert node IDs back to Node objects in sorted order
        Map<String, Node> nodeMap = new HashMap<>();
        for (Node node : nodes) {
            nodeMap.put(node.getId(), node);
        }
        
        List<Node> sortedNodes = new ArrayList<>();
        for (String nodeId : sortedNodeIds) {
            sortedNodes.add(nodeMap.get(nodeId));
        }

        // Execute pipeline
        ExecutionResult result = pipelineExecutor.execute(sortedNodes, edges, runPipeline.getInputs());

        // Build response
        return new RunResponse(
            result.getNodeOutputs(),
            result.getExecutionTrace(),
            nodes.size(),
            edges.size(),
            true  // We already validated it's a DAG
        );
    }
}
