package io.github.dkjsiogu.arsenalgraft.api.config;

import net.minecraft.network.chat.Component;
import java.util.Optional;

/**
 * 配置选项 - 单个配置项的定义
 */
public abstract class ConfigurationOption {
    protected final String id;
    protected final Component displayName;
    protected final Optional<Component> description;
    protected final Object defaultValue;
    protected final boolean required;
    
    protected ConfigurationOption(String id, Component displayName, 
                                Optional<Component> description, 
                                Object defaultValue, boolean required) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.defaultValue = defaultValue;
        this.required = required;
    }
    
    public String getId() { return id; }
    public String getName() { return id; } // Alias for compatibility
    public Component getDisplayName() { return displayName; }
    public Optional<Component> getDescription() { return description; }
    public Object getDefaultValue() { return defaultValue; }
    public boolean isRequired() { return required; }
    
    /**
     * 验证值是否有效
     */
    public abstract ValidationResult validate(Object value);
    
    /**
     * 获取配置选项类型
     */
    public abstract ConfigurationOptionType getType();
}