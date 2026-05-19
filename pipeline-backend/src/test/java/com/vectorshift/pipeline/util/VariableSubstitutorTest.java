package com.vectorshift.pipeline.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for mustache-style variable substitution.
 * Tests various template patterns and edge cases.
 */
class VariableSubstitutorTest {
    
    private VariableSubstitutor substitutor;
    
    @BeforeEach
    void setUp() {
        substitutor = new VariableSubstitutor();
    }
    
    @Test
    void shouldSubstituteSingleVariable() {
        String template = "Hello {{ name }}!";
        Map<String, String> variables = new HashMap<>();
        variables.put("name", "World");
        
        String result = substitutor.substitute(template, variables);
        
        assertThat(result).isEqualTo("Hello World!");
    }
    
    @Test
    void shouldSubstituteMultipleVariables() {
        String template = "{{ greeting }} {{ name }}, welcome to {{ place }}!";
        Map<String, String> variables = new HashMap<>();
        variables.put("greeting", "Hello");
        variables.put("name", "Alice");
        variables.put("place", "Wonderland");
        
        String result = substitutor.substitute(template, variables);
        
        assertThat(result).isEqualTo("Hello Alice, welcome to Wonderland!");
    }
    
    @Test
    void shouldHandleVariablesWithoutSpaces() {
        String template = "Value: {{value}}";
        Map<String, String> variables = new HashMap<>();
        variables.put("value", "42");
        
        String result = substitutor.substitute(template, variables);
        
        assertThat(result).isEqualTo("Value: 42");
    }
    
    @Test
    void shouldHandleVariablesWithExtraWhitespace() {
        String template = "{{  name  }} is here";
        Map<String, String> variables = new HashMap<>();
        variables.put("name", "Bob");
        
        String result = substitutor.substitute(template, variables);
        
        assertThat(result).isEqualTo("Bob is here");
    }
    
    @Test
    void shouldLeaveUnmatchedVariablesUnchanged() {
        String template = "Hello {{ name }}, your age is {{ age }}";
        Map<String, String> variables = new HashMap<>();
        variables.put("name", "Charlie");
        // age is not provided
        
        String result = substitutor.substitute(template, variables);
        
        assertThat(result).isEqualTo("Hello Charlie, your age is {{ age }}");
    }
    
    @Test
    void shouldHandleEmptyTemplate() {
        String template = "";
        Map<String, String> variables = new HashMap<>();
        variables.put("name", "Test");
        
        String result = substitutor.substitute(template, variables);
        
        assertThat(result).isEmpty();
    }
    
    @Test
    void shouldHandleTemplateWithNoVariables() {
        String template = "This is plain text with no variables";
        Map<String, String> variables = new HashMap<>();
        
        String result = substitutor.substitute(template, variables);
        
        assertThat(result).isEqualTo("This is plain text with no variables");
    }
    
    @Test
    void shouldHandleEmptyVariableMap() {
        String template = "Hello {{ name }}!";
        Map<String, String> variables = new HashMap<>();
        
        String result = substitutor.substitute(template, variables);
        
        assertThat(result).isEqualTo("Hello {{ name }}!");
    }
    
    @Test
    void shouldHandleVariablesWithUnderscores() {
        String template = "{{ user_name }} logged in";
        Map<String, String> variables = new HashMap<>();
        variables.put("user_name", "john_doe");
        
        String result = substitutor.substitute(template, variables);
        
        assertThat(result).isEqualTo("john_doe logged in");
    }
    
    @Test
    void shouldHandleVariablesWithDots() {
        String template = "{{ user.name }} from {{ user.city }}";
        Map<String, String> variables = new HashMap<>();
        variables.put("user.name", "Jane");
        variables.put("user.city", "NYC");
        
        String result = substitutor.substitute(template, variables);
        
        assertThat(result).isEqualTo("Jane from NYC");
    }
    
    @Test
    void shouldHandleVariablesWithDollarSign() {
        String template = "Price: {{ $price }}";
        Map<String, String> variables = new HashMap<>();
        variables.put("$price", "$99.99");
        
        String result = substitutor.substitute(template, variables);
        
        assertThat(result).isEqualTo("Price: $99.99");
    }
    
    @Test
    void shouldHandleSameVariableMultipleTimes() {
        String template = "{{ name }} and {{ name }} are friends";
        Map<String, String> variables = new HashMap<>();
        variables.put("name", "Alice");
        
        String result = substitutor.substitute(template, variables);
        
        assertThat(result).isEqualTo("Alice and Alice are friends");
    }
    
    @Test
    void shouldPreserveTextOutsideVariables() {
        String template = "Start {{ var1 }} middle {{ var2 }} end";
        Map<String, String> variables = new HashMap<>();
        variables.put("var1", "A");
        variables.put("var2", "B");
        
        String result = substitutor.substitute(template, variables);
        
        assertThat(result).isEqualTo("Start A middle B end");
        // Verify "Start ", " middle ", " end" are preserved
        assertThat(result).contains("Start ", " middle ", " end");
    }
    
    @Test
    void shouldHandleVariablesAtStartAndEnd() {
        String template = "{{ start }} middle {{ end }}";
        Map<String, String> variables = new HashMap<>();
        variables.put("start", "BEGIN");
        variables.put("end", "FINISH");
        
        String result = substitutor.substitute(template, variables);
        
        assertThat(result).isEqualTo("BEGIN middle FINISH");
    }
    
    @Test
    void shouldNotMatchInvalidVariableSyntax() {
        String template = "{ name } or {{{}}} or {{ }} should not match";
        Map<String, String> variables = new HashMap<>();
        variables.put("name", "Test");
        
        String result = substitutor.substitute(template, variables);
        
        // Invalid syntax should remain unchanged (single braces, empty variables)
        assertThat(result).isEqualTo("{ name } or {{{}}} or {{ }} should not match");
    }
}
