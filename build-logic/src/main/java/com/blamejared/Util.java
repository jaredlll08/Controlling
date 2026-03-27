package com.blamejared;

import org.gradle.api.Project;

import java.util.Locale;

class Util {
    
    static String property(Project project, String property) {
        
        if(project.property(property) instanceof String str) {
            return str;
        } else {
            throw new IllegalStateException("Unable to find property: '" + property + "'");
        }
    }
    
    static String capitalize(String input) {
        
        return input.substring(0, 1).toUpperCase(Locale.ROOT) + input.substring(1);
    }
    
}
