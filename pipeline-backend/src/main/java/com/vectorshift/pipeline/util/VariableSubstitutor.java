package com.vectorshift.pipeline.util;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Performs mustache-style variable substitution in templates.
 * Replaces {{ variableName }} with values from a variable map.
 */
@Component
public class VariableSubstitutor {
    
    // Regex pattern for mustache-style variables: {{ variableName }}
    // Matches: {{ followed by optional whitespace, valid identifier, optional whitespace, }}
    // Valid identifiers: start with letter/underscore/$, followed by letters/digits/underscores/dots/$
    private static final Pattern VARIABLE_PATTERN = 
        Pattern.compile("\\{\\{\\s*([a-zA-Z_$][a-zA-Z0-9_.$]*)\\s*\\}\\}");
    
    /**
     * Replaces {{ variableName }} with values from the map.
     * Leaves unmatched variables as-is for debugging.
     * 
     * @param template the template string with variables
     * @param variables map of variable names to values
     * @return template with variables replaced
     */
    public String substitute(String template, Map<String, String> variables) {
        if (template == null || template.isEmpty()) {
            return template;
        }
        
        if (variables == null || variables.isEmpty()) {
            return template;
        }
        
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        StringBuffer result = new StringBuffer();
        
        // Find and replace all variable occurrences
        while (matcher.find()) {
            String variableName = matcher.group(1);  // Extract variable name (without {{ }})
            String value = variables.get(variableName);
            
            if (value != null) {
                // Replace with actual value (escape special regex chars)
                matcher.appendReplacement(result, Matcher.quoteReplacement(value));
            } else {
                // Keep original placeholder if variable not found
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        
        matcher.appendTail(result);
        return result.toString();
    }
}
