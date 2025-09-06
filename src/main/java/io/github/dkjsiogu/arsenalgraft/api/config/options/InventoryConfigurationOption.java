package io.github.dkjsiogu.arsenalgraft.api.config.options;

import io.github.dkjsiogu.arsenalgraft.api.config.ConfigurationOption;
import io.github.dkjsiogu.arsenalgraft.api.config.ConfigurationOptionType;
import io.github.dkjsiogu.arsenalgraft.api.config.ValidationResult;
import net.minecraft.network.chat.Component;
import java.util.Optional;

/**
 * 物品栏配置选项
 */
public class InventoryConfigurationOption extends ConfigurationOption {
    private final int slotCount;
    private final boolean allowEmpty;
    
    public InventoryConfigurationOption(String id, Component displayName, 
                                      Optional<Component> description, 
                                      boolean required, int slotCount, 
                                      boolean allowEmpty) {
        super(id, displayName, description, null, required);
        this.slotCount = slotCount;
        this.allowEmpty = allowEmpty;
    }
    
    // 为了兼容性，提供额外的构造函数
    public InventoryConfigurationOption(String id, Component displayName, 
                                      Component description, 
                                      int slotCount, boolean allowEmpty) {
        super(id, displayName, Optional.ofNullable(description), null, false);
        this.slotCount = slotCount;
        this.allowEmpty = allowEmpty;
    }
    
    @Override
    public ValidationResult validate(Object value) {
        // 验证物品栏数据的逻辑
        return ValidationResult.success();
    }
    
    @Override
    public ConfigurationOptionType getType() {
        return ConfigurationOptionType.INVENTORY;
    }
    
    public int getSlotCount() { return slotCount; }
    public boolean isAllowEmpty() { return allowEmpty; }
}
