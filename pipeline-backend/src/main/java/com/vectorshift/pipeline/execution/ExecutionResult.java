package com.vectorshift.pipeline.execution;

import com.vectorshift.pipeline.model.ExecutionTrace;

import java.util.List;
import java.util.Map;

public class ExecutionResult {
    private final Map<String, String> nodeOutputs;
    private final List<ExecutionTrace> executionTrace;

    public ExecutionResult(Map<String, String> nodeOutputs, List<ExecutionTrace> executionTrace) {
        this.nodeOutputs = nodeOutputs;
        this.executionTrace = executionTrace;
    }

    public Map<String, String> getNodeOutputs() {
        return nodeOutputs;
    }

    public List<ExecutionTrace> getExecutionTrace() {
        return executionTrace;
    }
}
