package io.github.dkjsiogu.arsenalgraft.api.v3.modification;

import io.github.dkjsiogu.arsenalgraft.api.v3.component.IModificationComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;
import java.util.UUID;

/**
 * 已安装插槽
 * 
 * 代表玩家身上一个已经安装的改造插槽实例。
 * 它包含了从模板复制的组件，以及运行时的状态数据。
 */
public class InstalledSlot implements INBTSerializable<CompoundTag> {
    
    private final UUID slotId;
    private final ModificationTemplate template;
    private final Map<String, IModificationComponent> components;
    private boolean installed;
    
    public InstalledSlot(ModificationTemplate template) {
        this.slotId = UUID.randomUUID();
        this.template = template;
        this.installed = false;
        
        // 从模板复制组件
        this.components = new java.util.HashMap<>();
        for (Map.Entry<String, IModificationComponent> entry : template.getComponents().entrySet()) {
            this.components.put(entry.getKey(), entry.getValue().copy());
        }
    }
    
    /**
     * 从NBT加载的构造函数
     */
    public InstalledSlot(UUID slotId, ModificationTemplate template, Map<String, IModificationComponent> components, boolean installed) {
        this.slotId = slotId;
        this.template = template;
        this.components = components;
        this.installed = installed;
    }
    
    /**
     * 获取插槽唯一ID
     */
    public UUID getSlotId() {
        return slotId;
    }
    
    /**
     * 获取改造模板
     */
    public ModificationTemplate getTemplate() {
        return template;
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
     * 是否已安装
     */
    public boolean isInstalled() {
        return installed;
    }
    
    /**
     * 设置安装状态
     */
    public void setInstalled(boolean installed) {
        this.installed = installed;
    }
    
    /**
     * 安装插槽
     */
    public void install(Player player) {
        if (installed) {
            return;
        }
        
        setInstalled(true);
        
        // 安装所有组件
        for (IModificationComponent component : components.values()) {
            component.onInstall(player);
        }
    }
    
    /**
     * 卸载插槽
     */
    public void uninstall(Player player) {
        if (!installed) {
            return;
        }
        
        // 卸载所有组件
        for (IModificationComponent component : components.values()) {
            component.onUninstall(player);
        }
        
        setInstalled(false);
    }
    
    /**
     * 每tick更新
     */
    public void tick(Player player) {
        if (!installed) {
            return;
        }
        
        for (IModificationComponent component : components.values()) {
            if (component.isActive()) {
                component.tick(player);
            }
        }
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("slotId", slotId.toString());
        tag.putString("templateId", template.getId().toString());
        tag.putBoolean("installed", installed);
        
        // 序列化组件
        CompoundTag componentsTag = new CompoundTag();
        for (Map.Entry<String, IModificationComponent> entry : components.entrySet()) {
            CompoundTag componentTag = entry.getValue().serializeNBT();
            componentTag.putString("componentType", entry.getValue().getComponentType());
            componentsTag.put(entry.getKey(), componentTag);
        }
        tag.put("components", componentsTag);
        
        return tag;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        // 插槽的基本属性在构造时设置，这里只恢复组件状态
        if (nbt.contains("components")) {
            CompoundTag componentsTag = nbt.getCompound("components");
            
            for (String componentKey : componentsTag.getAllKeys()) {
                CompoundTag componentTag = componentsTag.getCompound(componentKey);
                IModificationComponent component = components.get(componentKey);
                if (component != null) {
                    component.deserializeNBT(componentTag);
                }
            }
        }
        
        this.installed = nbt.getBoolean("installed");
    }
}
