package io.github.dkjsiogu.arsenalgraft.api.v3.component.impl;

import io.github.dkjsiogu.arsenalgraft.api.v3.component.IModificationComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 属性修改组件
 * 
 * 用于修改玩家的属性，如血量、攻击力、速度等
 */
public class AttributeModificationComponent implements IModificationComponent {
    
    private static final String COMPONENT_TYPE = "attribute_modification";
    
    private boolean active = false;
    private final Map<Attribute, AttributeModifier> modifiers = new HashMap<>();
    private final Map<Attribute, UUID> appliedModifierIds = new HashMap<>();
    
    public AttributeModificationComponent() {}
    
    /**
     * 添加属性修改器
     */
    public void addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        modifiers.put(attribute, modifier);
    }
    
    /**
     * 移除属性修改器
     */
    public void removeAttributeModifier(Attribute attribute) {
        modifiers.remove(attribute);
    }
    
    @Override
    public String getComponentType() {
        return COMPONENT_TYPE;
    }
    
    @Override
    public boolean isActive() {
        return active;
    }
    
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public void onInstall(Player player) {
        if (!active) return;
        
        // 应用所有属性修改器
        for (Map.Entry<Attribute, AttributeModifier> entry : modifiers.entrySet()) {
            AttributeInstance attributeInstance = player.getAttribute(entry.getKey());
            if (attributeInstance != null) {
                AttributeModifier modifier = entry.getValue();
                
                // 确保修改器尚未应用
                if (!attributeInstance.hasModifier(modifier)) {
                    attributeInstance.addPermanentModifier(modifier);
                    appliedModifierIds.put(entry.getKey(), modifier.getId());
                    System.out.println("[AttributeModificationComponent] 应用属性修改器: " + ForgeRegistries.ATTRIBUTES.getKey(entry.getKey()) + " -> " + modifier.getAmount());
                }
            }
        }
    }
    
    @Override
    public void onUninstall(Player player) {
        // 移除所有属性修改器
        for (Map.Entry<Attribute, UUID> entry : appliedModifierIds.entrySet()) {
            AttributeInstance attributeInstance = player.getAttribute(entry.getKey());
            if (attributeInstance != null) {
                attributeInstance.removeModifier(entry.getValue());
                System.out.println("[AttributeModificationComponent] 移除属性修改器: " + ForgeRegistries.ATTRIBUTES.getKey(entry.getKey()));
            }
        }
        appliedModifierIds.clear();
    }
    
    @Override
    public void tick(Player player) {
        // 属性修改器不需要持续更新
    }
    
    @Override
    public IModificationComponent copy() {
        AttributeModificationComponent copy = new AttributeModificationComponent();
        copy.active = this.active;
        copy.modifiers.putAll(this.modifiers);
        return copy;
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("componentType", COMPONENT_TYPE);
        tag.putBoolean("active", active);
        
        // 序列化修改器
        CompoundTag modifiersTag = new CompoundTag();
        for (Map.Entry<Attribute, AttributeModifier> entry : modifiers.entrySet()) {
            String attributeName = ForgeRegistries.ATTRIBUTES.getKey(entry.getKey()).toString();
            AttributeModifier modifier = entry.getValue();
            
            CompoundTag modifierTag = new CompoundTag();
            modifierTag.putString("name", modifier.getName());
            modifierTag.putDouble("amount", modifier.getAmount());
            modifierTag.putInt("operation", modifier.getOperation().ordinal());
            modifierTag.putString("uuid", modifier.getId().toString());
            
            modifiersTag.put(attributeName, modifierTag);
        }
        tag.put("modifiers", modifiersTag);
        
        return tag;
    }
    
    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.active = tag.getBoolean("active");
        
        // 反序列化修改器
        if (tag.contains("modifiers")) {
            CompoundTag modifiersTag = tag.getCompound("modifiers");
            modifiers.clear();
            
            for (String attributeName : modifiersTag.getAllKeys()) {
                Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(net.minecraft.resources.ResourceLocation.tryParse(attributeName));
                if (attribute != null) {
                    CompoundTag modifierTag = modifiersTag.getCompound(attributeName);
                    
                    String name = modifierTag.getString("name");
                    double amount = modifierTag.getDouble("amount");
                    AttributeModifier.Operation operation = AttributeModifier.Operation.values()[modifierTag.getInt("operation")];
                    UUID uuid = UUID.fromString(modifierTag.getString("uuid"));
                    
                    AttributeModifier modifier = new AttributeModifier(uuid, name, amount, operation);
                    modifiers.put(attribute, modifier);
                }
            }
        }
    }
}
