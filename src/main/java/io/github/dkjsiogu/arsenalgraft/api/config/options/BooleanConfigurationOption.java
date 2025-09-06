package io.github.dkjsiogu.arsenalgraft.api.config.options;

import io.github.dkjsiogu.arsenalgraft.api.config.ConfigurationOption;
import io.github.dkjsiogu.arsenalgraft.api.config.ConfigurationOptionType;
import io.github.dkjsiogu.arsenalgraft.api.config.ValidationResult;
import net.minecraft.network.chat.Component;
import java.util.Optional;

/**
 * 布尔配置选项
 */
public class BooleanConfigurationOption extends ConfigurationOption {
    
    public BooleanConfigurationOption(String id, Component displayName, 
                                    Optional<Component> description, 
                                    boolean defaultValue, boolean required) {
        super(id, displayName, description, defaultValue, required);
    }
    
    // 为了兼容性，提供额外的构造函数
    public BooleanConfigurationOption(String id, Component displayName, 
                                    Component description, 
                                    boolean defaultValue) {
        super(id, displayName, Optional.ofNullable(description), defaultValue, false);
    }
    
    @Override
    public ValidationResult validate(Object value) {
        if (!(value instanceof Boolean)) {
            return ValidationResult.failure("值必须是布尔类型");
        }
        return ValidationResult.success();
    }
    
    @Override
    public ConfigurationOptionType getType() {
        return ConfigurationOptionType.BOOLEAN;
    }
    
    /**
     * 获取默认布尔值
     */
    public boolean getDefaultBooleanValue() {
        return (Boolean) getDefaultValue();
    }
}
