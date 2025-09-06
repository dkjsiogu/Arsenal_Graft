package io.github.dkjsiogu.arsenalgraft.api.config;

import net.minecraft.network.chat.Component;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents a section of configuration options in the schema.
 * Sections group related options together for better organization.
 */
public class ConfigurationSection {
    private final String name;
    private final Component displayName;
    private final Component description;
    private final List<ConfigurationOption> options;
    private final Map<String, ConfigurationOption> optionMap;
    
    public ConfigurationSection(String name, Component displayName, Component description) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.options = new ArrayList<>();
        this.optionMap = new HashMap<>();
    }
    
    public ConfigurationSection(String name, Component displayName, Component description, List<ConfigurationOption> options) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.options = new ArrayList<>(options);
        this.optionMap = new HashMap<>();
        
        // Build option map for quick lookup
        for (ConfigurationOption option : options) {
            this.optionMap.put(option.getName(), option);
        }
    }
    
    /**
     * Gets the section name (used as identifier)
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the display name for this section
     */
    public Component getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the description for this section
     */
    public Component getDescription() {
        return description;
    }
    
    /**
     * Gets all options in this section
     */
    public List<ConfigurationOption> getOptions() {
        return new ArrayList<>(options);
    }
    
    /**
     * Gets an option by name
     */
    public ConfigurationOption getOption(String name) {
        return optionMap.get(name);
    }
    
    /**
     * Adds an option to this section
     */
    public void addOption(ConfigurationOption option) {
        if (!options.contains(option)) {
            options.add(option);
            optionMap.put(option.getName(), option);
        }
    }
    
    /**
     * Removes an option from this section
     */
    public void removeOption(String name) {
        ConfigurationOption option = optionMap.remove(name);
        if (option != null) {
            options.remove(option);
        }
    }
    
    /**
     * Checks if this section contains an option with the given name
     */
    public boolean hasOption(String name) {
        return optionMap.containsKey(name);
    }
    
    /**
     * Gets the number of options in this section
     */
    public int getOptionCount() {
        return options.size();
    }
}
