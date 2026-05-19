package com.vectorshift.pipeline.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for CORS configuration.
 * Verifies that CORS headers are properly set.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CorsConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAllowCorsForGetRequest() throws Exception {
        mockMvc.perform(get("/")
                .header("Origin", "http://localhost:3000"))
            .andExpect(status().isOk())
            .andExpect(header().exists("Access-Control-Allow-Origin"))
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    void shouldHandlePreflightRequest() throws Exception {
        mockMvc.perform(options("/pipelines/parse")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type"))
            .andExpect(status().isOk())
            .andExpect(header().exists("Access-Control-Allow-Origin"))
            .andExpect(header().exists("Access-Control-Allow-Methods"))
            .andExpect(header().exists("Access-Control-Allow-Headers"))
            .andExpect(header().exists("Access-Control-Max-Age"));
    }

    @Test
    void shouldAllowCorsForPostRequest() throws Exception {
        mockMvc.perform(post("/pipelines/parse")
                .header("Origin", "http://localhost:3000")
                .contentType("application/json")
                .content("{\"nodes\": [], \"edges\": []}"))
            .andExpect(status().isOk())
            .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    @Test
    void shouldAllowDifferentOrigins() throws Exception {
        mockMvc.perform(get("/")
                .header("Origin", "http://example.com"))
            .andExpect(status().isOk())
            .andExpect(header().exists("Access-Control-Allow-Origin"))
            .andExpect(header().string("Access-Control-Allow-Origin", "http://example.com"));
    }
}
