package io.github.dkjsiogu.arsenalgraft.api.config;

/**
 * Enumeration of configuration option types supported by the framework.
 * Each type corresponds to a specific widget type in the GUI.
 */
public enum ConfigurationOptionType {
    /**
     * Boolean configuration option (checkbox)
     */
    BOOLEAN,
    
    /**
     * Integer configuration option (number input)
     */
    INTEGER,
    
    /**
     * Inventory configuration option (slot selection)
     */
    INVENTORY,
    
    /**
     * String configuration option (text input)
     */
    STRING,
    
    /**
     * Enum configuration option (dropdown)
     */
    ENUM,
    
    /**
     * List configuration option (multi-select)
     */
    LIST
}
