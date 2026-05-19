package com.vectorshift.pipeline.controller;

import com.vectorshift.pipeline.model.request.Pipeline;
import com.vectorshift.pipeline.model.request.RunPipeline;
import com.vectorshift.pipeline.model.response.ParseResponse;
import com.vectorshift.pipeline.model.response.RunResponse;
import com.vectorshift.pipeline.model.response.StatusResponse;
import com.vectorshift.pipeline.service.PipelineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for pipeline operations.
 * Provides endpoints for health check, parse, and run.
 */
@RestController
public class PipelineController {

    private final PipelineService pipelineService;

    public PipelineController(PipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    /**
     * Health check endpoint.
     * 
     * @return status response with "ok"
     */
    @GetMapping("/")
    public ResponseEntity<StatusResponse> healthCheck() {
        return ResponseEntity.ok(new StatusResponse("ok"));
    }

    /**
     * Parse pipeline endpoint.
     * Validates the pipeline structure and returns metadata.
     * 
     * @param pipeline the pipeline to parse
     * @return parse response with node/edge counts and DAG status
     */
    @PostMapping("/pipelines/parse")
    public ResponseEntity<ParseResponse> parsePipeline(@RequestBody Pipeline pipeline) {
        ParseResponse response = pipelineService.parsePipeline(pipeline);
        return ResponseEntity.ok(response);
    }

    /**
     * Run pipeline endpoint.
     * Executes the pipeline and returns results.
     * 
     * @param runPipeline the pipeline to run with inputs
     * @return run response with execution results
     */
    @PostMapping("/pipelines/run")
    public ResponseEntity<RunResponse> runPipeline(@RequestBody RunPipeline runPipeline) {
        RunResponse response = pipelineService.runPipeline(runPipeline);
        return ResponseEntity.ok(response);
    }
}
