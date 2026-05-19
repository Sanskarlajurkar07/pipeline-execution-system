package com.vectorshift.pipeline.execution;

import com.vectorshift.pipeline.client.GeminiClient;
import com.vectorshift.pipeline.model.Edge;
import com.vectorshift.pipeline.model.ExecutionTrace;
import com.vectorshift.pipeline.model.Node;
import com.vectorshift.pipeline.processor.*;
import com.vectorshift.pipeline.util.VariableSubstitutor;

import java.util.*;

public class PipelineExecutor {
    
    private final Map<String, NodeProcessor> processors;

    public PipelineExecutor(GeminiClient geminiClient) {
        this.processors = new HashMap<>();
        VariableSubstitutor variableSubstitutor = new VariableSubstitutor();
        
        processors.put("customInput", new CustomInputProcessor());
        processors.put("text", new TextNodeProcessor(variableSubstitutor));
        processors.put("customOutput", new CustomOutputProcessor());
        processors.put("llm", new LlmNodeProcessor(geminiClient));
    }

    public ExecutionResult execute(List<Node> sortedNodes, List<Edge> edges, Map<String, String> inputs) {
        ExecutionContext context = new ExecutionContext(edges, inputs);
        List<ExecutionTrace> trace = new ArrayList<>();

        for (Node node : sortedNodes) {
            long startTime = System.currentTimeMillis();
            String output = null;
            String error = null;

            try {
                NodeProcessor processor = processors.get(node.getType());
                if (processor == null) {
                    throw new IllegalArgumentException("Unknown node type: " + node.getType());
                }
                
                output = processor.process(node, context);
                context.setNodeOutput(node.getId(), output);
                
            } catch (Exception e) {
                error = e.getMessage();
                output = "Error: " + error;
                context.setNodeOutput(node.getId(), output);
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            trace.add(new ExecutionTrace(
                node.getId(),
                node.getType(),
                output,
                duration,
                error
            ));
        }

        return new ExecutionResult(context.getNodeOutputs(), trace);
    }
}
