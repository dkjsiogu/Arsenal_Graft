package io.github.dkjsiogu.arsenalgraft.api.config.options;

import io.github.dkjsiogu.arsenalgraft.api.config.ConfigurationOption;
import io.github.dkjsiogu.arsenalgraft.api.config.ConfigurationOptionType;
import io.github.dkjsiogu.arsenalgraft.api.config.ValidationResult;
import net.minecraft.network.chat.Component;
import java.util.Optional;

/**
 * 整数配置选项
 */
public class IntegerConfigurationOption extends ConfigurationOption {
    private final int minValue;
    private final int maxValue;
    
    public IntegerConfigurationOption(String id, Component displayName, 
                                    Optional<Component> description, 
                                    int defaultValue, boolean required,
                                    int minValue, int maxValue) {
        super(id, displayName, description, defaultValue, required);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    // 为了兼容性，提供额外的构造函数
    public IntegerConfigurationOption(String id, Component displayName, 
                                    Optional<Component> description, 
                                    int defaultValue, boolean required,
                                    int minValue, int maxValue, boolean enableSlider) {
        super(id, displayName, description, defaultValue, required);
        this.minValue = minValue;
        this.maxValue = maxValue;
        // enableSlider 参数可以用于 UI 控制，但这里我们简化处理
    }
    
    @Override
    public ValidationResult validate(Object value) {
        if (!(value instanceof Integer)) {
            return ValidationResult.failure("值必须是整数类型");
        }
        int intValue = (Integer) value;
        if (intValue < minValue || intValue > maxValue) {
            return ValidationResult.failure("值必须在 " + minValue + " 到 " + maxValue + " 之间");
        }
        return ValidationResult.success();
    }
    
    @Override
    public ConfigurationOptionType getType() {
        return ConfigurationOptionType.INTEGER;
    }
    
    public int getMinValue() { return minValue; }
    public int getMaxValue() { return maxValue; }
    
    /**
     * 获取默认整数值
     */
    public int getDefaultIntValue() {
        return (Integer) getDefaultValue();
    }
}
