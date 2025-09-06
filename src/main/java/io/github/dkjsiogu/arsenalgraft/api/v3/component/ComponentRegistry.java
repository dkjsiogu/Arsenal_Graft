package io.github.dkjsiogu.arsenalgraft.api.v3.component;

import io.github.dkjsiogu.arsenalgraft.api.v3.component.impl.AttributeModificationComponent;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.impl.EffectComponent;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.impl.InventoryComponentImpl;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.impl.SkillComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 组件工厂和注册器
 * 
 * 管理所有可用的组件类型及其创建
 */
public class ComponentRegistry {
    
    private static final Map<String, Supplier<IModificationComponent>> componentFactories = new HashMap<>();
    private static final Map<String, ComponentInfo> componentInfo = new HashMap<>();
    
    static {
        registerDefaultComponents();
    }
    
    /**
     * 注册默认组件类型
     */
    private static void registerDefaultComponents() {
        // 属性修改组件
        registerComponent(
            "attribute_modification",
            AttributeModificationComponent::new,
            new ComponentInfo(
                "属性修改",
                "修改玩家的基础属性，如血量、攻击力、速度等",
                ComponentCategory.ENHANCEMENT
            )
        );
        
        // 技能组件
        registerComponent(
            "skill",
            SkillComponent::new,
            new ComponentInfo(
                "技能系统",
                "提供特殊技能和能力，如双重跳跃、冲刺、隐身等",
                ComponentCategory.ABILITY
            )
        );
        
        // 特效组件
        registerComponent(
            "effect",
            EffectComponent::new,
            new ComponentInfo(
                "特效系统",
                "管理药水效果、视觉特效、音效等",
                ComponentCategory.VISUAL
            )
        );
        
        // 物品栏组件
        registerComponent(
            "inventory",
            () -> new InventoryComponentImpl(9), // 默认9个槽位
            new ComponentInfo(
                "物品栏扩展",
                "提供额外的物品栏空间和特殊储存功能",
                ComponentCategory.UTILITY
            )
        );
    }
    
    /**
     * 注册组件类型
     */
    public static void registerComponent(String type, Supplier<IModificationComponent> factory, ComponentInfo info) {
        componentFactories.put(type, factory);
        componentInfo.put(type, info);
        System.out.println("[ComponentRegistry] 注册组件类型: " + type + " (" + info.displayName + ")");
    }
    
    /**
     * 创建组件实例
     */
    public static IModificationComponent createComponent(String type) {
        Supplier<IModificationComponent> factory = componentFactories.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown component type: " + type);
        }
        return factory.get();
    }
    
    /**
     * 检查组件类型是否已注册
     */
    public static boolean isComponentRegistered(String type) {
        return componentFactories.containsKey(type);
    }
    
    /**
     * 获取所有已注册的组件类型
     */
    public static Map<String, ComponentInfo> getAllComponentTypes() {
        return new HashMap<>(componentInfo);
    }
    
    /**
     * 获取组件信息
     */
    public static ComponentInfo getComponentInfo(String type) {
        return componentInfo.get(type);
    }
    
    /**
     * 组件信息类
     */
    public static class ComponentInfo {
        public final String displayName;
        public final String description;
        public final ComponentCategory category;
        
        public ComponentInfo(String displayName, String description, ComponentCategory category) {
            this.displayName = displayName;
            this.description = description;
            this.category = category;
        }
    }
    
    /**
     * 组件分类
     */
    public enum ComponentCategory {
        ENHANCEMENT("增强类"),
        ABILITY("能力类"),
        VISUAL("视觉类"),
        UTILITY("实用类"),
        STORAGE("存储类"),
        COMMUNICATION("通信类"),
        DEFENSIVE("防御类"),
        OFFENSIVE("攻击类");
        
        public final String displayName;
        
        ComponentCategory(String displayName) {
            this.displayName = displayName;
        }
    }
}
