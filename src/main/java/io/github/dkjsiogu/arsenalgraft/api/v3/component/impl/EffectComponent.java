package io.github.dkjsiogu.arsenalgraft.api.v3.component.impl;

import io.github.dkjsiogu.arsenalgraft.api.v3.component.IModificationComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

/**
 * 特效组件
 * 
 * 管理药水效果、视觉特效、音效等
 */
public class EffectComponent implements IModificationComponent {
    
    private static final String COMPONENT_TYPE = "effect";
    
    private boolean active = false;
    private final Map<MobEffect, EffectData> effects = new HashMap<>();
    private final Map<String, Object> visualEffects = new HashMap<>();
    
    public EffectComponent() {}
    
    /**
     * 特效数据类
     */
    public static class EffectData {
        public final int amplifier;
        public final int duration;
        public final boolean ambient;
        public final boolean visible;
        
        public EffectData(int amplifier, int duration, boolean ambient, boolean visible) {
            this.amplifier = amplifier;
            this.duration = duration;
            this.ambient = ambient;
            this.visible = visible;
        }
    }
    
    /**
     * 添加药水效果
     */
    public void addPotionEffect(MobEffect effect, int amplifier, int duration, boolean ambient, boolean visible) {
        effects.put(effect, new EffectData(amplifier, duration, ambient, visible));
    }
    
    /**
     * 添加永久药水效果（用于改造）
     */
    public void addPermanentPotionEffect(MobEffect effect, int amplifier) {
        addPotionEffect(effect, amplifier, Integer.MAX_VALUE, true, false);
    }
    
    /**
     * 移除药水效果
     */
    public void removePotionEffect(MobEffect effect) {
        effects.remove(effect);
    }
    
    /**
     * 设置视觉特效
     */
    public void setVisualEffect(String effectName, Object data) {
        visualEffects.put(effectName, data);
    }
    
    /**
     * 移除视觉特效
     */
    public void removeVisualEffect(String effectName) {
        visualEffects.remove(effectName);
    }
    
    /**
     * 应用所有效果
     */
    private void applyEffects(Player player) {
        for (Map.Entry<MobEffect, EffectData> entry : effects.entrySet()) {
            MobEffect effect = entry.getKey();
            EffectData data = entry.getValue();
            
            MobEffectInstance effectInstance = new MobEffectInstance(
                effect, 
                data.duration, 
                data.amplifier, 
                data.ambient, 
                data.visible
            );
            
            player.addEffect(effectInstance);
        }
    }
    
    /**
     * 移除所有效果
     */
    private void removeEffects(Player player) {
        for (MobEffect effect : effects.keySet()) {
            player.removeEffect(effect);
        }
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
        if (active) {
            applyEffects(player);
            System.out.println("[EffectComponent] 为 " + player.getName().getString() + " 应用特效，效果数量: " + effects.size());
        }
    }
    
    @Override
    public void onUninstall(Player player) {
        removeEffects(player);
        System.out.println("[EffectComponent] 为 " + player.getName().getString() + " 移除特效");
    }
    
    @Override
    public void tick(Player player) {
        if (!active) return;
        
        // 检查永久效果是否需要续期
        for (Map.Entry<MobEffect, EffectData> entry : effects.entrySet()) {
            MobEffect effect = entry.getKey();
            EffectData data = entry.getValue();
            
            // 如果是永久效果且即将到期，重新应用
            if (data.duration == Integer.MAX_VALUE) {
                MobEffectInstance currentEffect = player.getEffect(effect);
                if (currentEffect == null || currentEffect.getDuration() < 100) {
                    MobEffectInstance newEffect = new MobEffectInstance(
                        effect, 
                        Integer.MAX_VALUE, 
                        data.amplifier, 
                        data.ambient, 
                        data.visible
                    );
                    player.addEffect(newEffect);
                }
            }
        }
    }
    
    @Override
    public IModificationComponent copy() {
        EffectComponent copy = new EffectComponent();
        copy.active = this.active;
        copy.effects.putAll(this.effects);
        copy.visualEffects.putAll(this.visualEffects);
        return copy;
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("componentType", COMPONENT_TYPE);
        tag.putBoolean("active", active);
        
        // 序列化药水效果
        CompoundTag effectsTag = new CompoundTag();
        for (Map.Entry<MobEffect, EffectData> entry : effects.entrySet()) {
            String effectName = ForgeRegistries.MOB_EFFECTS.getKey(entry.getKey()).toString();
            EffectData data = entry.getValue();
            
            CompoundTag effectTag = new CompoundTag();
            effectTag.putInt("amplifier", data.amplifier);
            effectTag.putInt("duration", data.duration);
            effectTag.putBoolean("ambient", data.ambient);
            effectTag.putBoolean("visible", data.visible);
            
            effectsTag.put(effectName, effectTag);
        }
        tag.put("effects", effectsTag);
        
        // 序列化视觉特效
        CompoundTag visualTag = new CompoundTag();
        for (Map.Entry<String, Object> entry : visualEffects.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                visualTag.putString(entry.getKey(), (String) value);
            } else if (value instanceof Integer) {
                visualTag.putInt(entry.getKey(), (Integer) value);
            } else if (value instanceof Double) {
                visualTag.putDouble(entry.getKey(), (Double) value);
            } else if (value instanceof Boolean) {
                visualTag.putBoolean(entry.getKey(), (Boolean) value);
            }
        }
        tag.put("visualEffects", visualTag);
        
        return tag;
    }
    
    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.active = tag.getBoolean("active");
        
        // 反序列化药水效果
        if (tag.contains("effects")) {
            CompoundTag effectsTag = tag.getCompound("effects");
            effects.clear();
            
            for (String effectName : effectsTag.getAllKeys()) {
                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(
                    net.minecraft.resources.ResourceLocation.tryParse(effectName)
                );
                if (effect != null) {
                    CompoundTag effectTag = effectsTag.getCompound(effectName);
                    
                    int amplifier = effectTag.getInt("amplifier");
                    int duration = effectTag.getInt("duration");
                    boolean ambient = effectTag.getBoolean("ambient");
                    boolean visible = effectTag.getBoolean("visible");
                    
                    effects.put(effect, new EffectData(amplifier, duration, ambient, visible));
                }
            }
        }
        
        // 反序列化视觉特效
        if (tag.contains("visualEffects")) {
            CompoundTag visualTag = tag.getCompound("visualEffects");
            visualEffects.clear();
            
            for (String key : visualTag.getAllKeys()) {
                net.minecraft.nbt.Tag valueTag = visualTag.get(key);
                
                if (valueTag instanceof net.minecraft.nbt.StringTag) {
                    visualEffects.put(key, visualTag.getString(key));
                } else if (valueTag instanceof net.minecraft.nbt.IntTag) {
                    visualEffects.put(key, visualTag.getInt(key));
                } else if (valueTag instanceof net.minecraft.nbt.DoubleTag) {
                    visualEffects.put(key, visualTag.getDouble(key));
                } else if (valueTag instanceof net.minecraft.nbt.ByteTag) {
                    visualEffects.put(key, visualTag.getBoolean(key));
                }
            }
        }
    }
}
