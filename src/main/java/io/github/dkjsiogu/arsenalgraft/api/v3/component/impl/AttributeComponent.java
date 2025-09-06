package io.github.dkjsiogu.arsenalgraft.api.v3.component.impl;

import io.github.dkjsiogu.arsenalgraft.api.v3.component.IModificationComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 属性组件实现
 * 
 * 为玩家提供属性修改功能。
 */
public class AttributeComponent implements IModificationComponent {
    
    private final Map<String, Double> attributes;
    private boolean active = false;
    private final Map<String, UUID> appliedModifiers = new HashMap<>();
    
    public AttributeComponent(Map<String, Double> attributes) {
        this.attributes = new HashMap<>(attributes);
    }
    
    @Override
    public String getComponentType() {
        return "attribute";
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
        setActive(true);
        applyAttributes(player);
    }
    
    @Override
    public void onUninstall(Player player) {
        removeAttributes(player);
        setActive(false);
    }
    
    @Override
    public void tick(Player player) {
        // 属性组件通常不需要每tick处理
    }
    
    private void applyAttributes(Player player) {
        for (Map.Entry<String, Double> entry : attributes.entrySet()) {
            String attrName = entry.getKey();
            double value = entry.getValue();
            
            try {
                var attribute = switch (attrName) {
                    case "max_health" -> Attributes.MAX_HEALTH;
                    case "movement_speed" -> Attributes.MOVEMENT_SPEED;
                    case "attack_damage" -> Attributes.ATTACK_DAMAGE;
                    case "armor" -> Attributes.ARMOR;
                    case "armor_toughness" -> Attributes.ARMOR_TOUGHNESS;
                    case "attack_speed" -> Attributes.ATTACK_SPEED;
                    case "luck" -> Attributes.LUCK;
                    default -> null;
                };
                
                if (attribute != null) {
                    UUID modifierId = UUID.randomUUID();
                    AttributeModifier modifier = new AttributeModifier(
                        modifierId,
                        "Arsenal Graft " + attrName,
                        value,
                        AttributeModifier.Operation.ADDITION
                    );
                    
                    var attrInstance = player.getAttribute(attribute);
                    if (attrInstance != null) {
                        attrInstance.addPermanentModifier(modifier);
                        appliedModifiers.put(attrName, modifierId);
                    }
                }
            } catch (Exception e) {
                // 忽略无效的属性
            }
        }
    }
    
    private void removeAttributes(Player player) {
        for (Map.Entry<String, UUID> entry : appliedModifiers.entrySet()) {
            String attrName = entry.getKey();
            UUID modifierId = entry.getValue();
            
            try {
                var attribute = switch (attrName) {
                    case "max_health" -> Attributes.MAX_HEALTH;
                    case "movement_speed" -> Attributes.MOVEMENT_SPEED;
                    case "attack_damage" -> Attributes.ATTACK_DAMAGE;
                    case "armor" -> Attributes.ARMOR;
                    case "armor_toughness" -> Attributes.ARMOR_TOUGHNESS;
                    case "attack_speed" -> Attributes.ATTACK_SPEED;
                    case "luck" -> Attributes.LUCK;
                    default -> null;
                };
                
                if (attribute != null) {
                    var attrInstance = player.getAttribute(attribute);
                    if (attrInstance != null) {
                        attrInstance.removeModifier(modifierId);
                    }
                }
            } catch (Exception e) {
                // 忽略移除失败
            }
        }
        appliedModifiers.clear();
    }
    
    @Override
    public IModificationComponent copy() {
        return new AttributeComponent(attributes);
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("componentType", getComponentType());
        tag.putBoolean("active", active);
        
        CompoundTag attributesTag = new CompoundTag();
        for (Map.Entry<String, Double> entry : attributes.entrySet()) {
            attributesTag.putDouble(entry.getKey(), entry.getValue());
        }
        tag.put("attributes", attributesTag);
        
        return tag;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.active = nbt.getBoolean("active");
        
        if (nbt.contains("attributes")) {
            CompoundTag attributesTag = nbt.getCompound("attributes");
            attributes.clear();
            
            for (String key : attributesTag.getAllKeys()) {
                attributes.put(key, attributesTag.getDouble(key));
            }
        }
    }
}
