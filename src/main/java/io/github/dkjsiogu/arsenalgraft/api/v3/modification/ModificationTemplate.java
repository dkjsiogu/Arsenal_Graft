package io.github.dkjsiogu.arsenalgraft.api.v3.modification;

import io.github.dkjsiogu.arsenalgraft.api.v3.component.IModificationComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;
import java.util.Map;

/**
 * 改造模板
 * 
 * 改造模板定义了一种改造的所有属性和组件。
 * 它是从JSON文件或KubeJS脚本加载的不可变数据结构。
 */
public class ModificationTemplate implements INBTSerializable<CompoundTag> {
    
    private final ResourceLocation id;
    private final Component displayName;
    private final List<Component> description;
    private final String slotType;
    private final Map<String, IModificationComponent> components;
    private final int maxInstallCount;
    private final boolean hasConfiguration;
    
    public ModificationTemplate(ResourceLocation id, 
                              Component displayName,
                              List<Component> description,
                              String slotType,
                              Map<String, IModificationComponent> components,
                              int maxInstallCount,
                              boolean hasConfiguration) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.slotType = slotType;
        this.components = components;
        this.maxInstallCount = maxInstallCount;
        this.hasConfiguration = hasConfiguration;
    }
    
    /**
     * 获取改造ID
     */
    public ResourceLocation getId() {
        return id;
    }
    
    /**
     * 获取显示名称
     */
    public Component getDisplayName() {
        return displayName;
    }
    
    /**
     * 获取描述
     */
    public List<Component> getDescription() {
        return description;
    }
    
    /**
     * 获取槽位类型
     */
    public String getSlotType() {
        return slotType;
    }
    
    /**
     * 获取所有组件
     */
    public Map<String, IModificationComponent> getComponents() {
        return components;
    }
    
    /**
     * 获取指定类型的组件
     */
    @SuppressWarnings("unchecked")
    public <T extends IModificationComponent> T getComponent(String componentType, Class<T> clazz) {
        IModificationComponent component = components.get(componentType);
        if (clazz.isInstance(component)) {
            return (T) component;
        }
        return null;
    }
    
    /**
     * 检查是否有指定类型的组件
     */
    public boolean hasComponent(String componentType) {
        return components.containsKey(componentType);
    }
    
    /**
     * 获取最大安装数量
     */
    public int getMaxInstallCount() {
        return maxInstallCount;
    }
    
    /**
     * 是否有配置选项
     */
    public boolean hasConfiguration() {
        return hasConfiguration;
    }
    
    /**
     * 创建模板的副本（用于实例化）
     */
    public ModificationTemplate copy() {
        Map<String, IModificationComponent> copiedComponents = new java.util.HashMap<>();
        for (Map.Entry<String, IModificationComponent> entry : components.entrySet()) {
            copiedComponents.put(entry.getKey(), entry.getValue().copy());
        }
        
        return new ModificationTemplate(
            id, displayName, description, slotType, 
            copiedComponents, maxInstallCount, hasConfiguration
        );
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id.toString());
        tag.putString("displayName", Component.Serializer.toJson(displayName));
        tag.putString("slotType", slotType);
        tag.putInt("maxInstallCount", maxInstallCount);
        tag.putBoolean("hasConfiguration", hasConfiguration);
        
        // 序列化描述
        CompoundTag descTag = new CompoundTag();
        for (int i = 0; i < description.size(); i++) {
            descTag.putString("line" + i, Component.Serializer.toJson(description.get(i)));
        }
        tag.put("description", descTag);
        
        // 序列化组件
        CompoundTag componentsTag = new CompoundTag();
        for (Map.Entry<String, IModificationComponent> entry : components.entrySet()) {
            componentsTag.put(entry.getKey(), entry.getValue().serializeNBT());
        }
        tag.put("components", componentsTag);
        
        return tag;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        // 模板是不可变的，不支持反序列化
        throw new UnsupportedOperationException("ModificationTemplate is immutable and cannot be deserialized");
    }
    
    /**
     * 模板构建器
     */
    public static class Builder {
        private ResourceLocation id;
        private Component displayName;
        private List<Component> description = List.of();
        private String slotType = "default";
        private Map<String, IModificationComponent> components = new java.util.HashMap<>();
        private int maxInstallCount = 1;
        private boolean hasConfiguration = false;
        
        public Builder(ResourceLocation id) {
            this.id = id;
        }
        
        public Builder displayName(Component displayName) {
            this.displayName = displayName;
            return this;
        }
        
        public Builder description(List<Component> description) {
            this.description = description;
            return this;
        }
        
        public Builder slotType(String slotType) {
            this.slotType = slotType;
            return this;
        }
        
        public Builder addComponent(String type, IModificationComponent component) {
            this.components.put(type, component);
            return this;
        }
        
        public Builder maxInstallCount(int maxInstallCount) {
            this.maxInstallCount = maxInstallCount;
            return this;
        }
        
        public Builder hasConfiguration(boolean hasConfiguration) {
            this.hasConfiguration = hasConfiguration;
            return this;
        }
        
        public ModificationTemplate build() {
            if (displayName == null) {
                throw new IllegalStateException("displayName is required");
            }
            return new ModificationTemplate(id, displayName, description, slotType, 
                                          components, maxInstallCount, hasConfiguration);
        }
    }
}
