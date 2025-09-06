package io.github.dkjsiogu.arsenalgraft.api.config;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 配置架构 - 定义配置的结构
 */
public class ConfigurationSchema {
    private final List<ConfigurationSection> sections;
    private final Map<String, ConfigurationOption> allOptions;
    
    public ConfigurationSchema(List<ConfigurationSection> sections) {
        this.sections = sections;
        this.allOptions = new HashMap<>();
        
        // Build a map of all options across all sections for quick lookup
        for (ConfigurationSection section : sections) {
            for (ConfigurationOption option : section.getOptions()) {
                allOptions.put(option.getName(), option);
            }
        }
    }
    
    public List<ConfigurationSection> getSections() {
        return sections;
    }
    
    /**
     * Gets an option by name from any section
     */
    public ConfigurationOption getOption(String name) {
        return allOptions.get(name);
    }
    
    /**
     * Checks if an option with the given name exists
     */
    public boolean hasOption(String name) {
        return allOptions.containsKey(name);
    }
    
    /**
     * Gets the section that contains the specified option
     */
    public ConfigurationSection getSectionForOption(String optionName) {
        for (ConfigurationSection section : sections) {
            if (section.hasOption(optionName)) {
                return section;
            }
        }
        return null;
    }
}
