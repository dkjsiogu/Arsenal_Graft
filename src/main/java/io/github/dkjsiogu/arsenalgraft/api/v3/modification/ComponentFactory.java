package io.github.dkjsiogu.arsenalgraft.api.v3.modification;

import io.github.dkjsiogu.arsenalgraft.api.v3.component.IModificationComponent;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.impl.InventoryComponentImpl;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.impl.AttributeComponent;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.impl.SkillComponent;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.impl.EffectComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * 组件工厂
 * 
 * 负责根据配置创建各种类型的改造组件。
 * 统一了JSON和KubeJS的组件创建流程。
 * 提供完整的错误处理和降级机制。
 */
public class ComponentFactory {
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    // 默认值常量
    private static final int DEFAULT_INVENTORY_SIZE = 9;
    private static final int MAX_INVENTORY_SIZE = 54; // 防止过大的库存
    private static final String DEFAULT_SLOT_TYPE = "general";
    private static final int DEFAULT_COOLDOWN = 60; // 60 ticks = 3秒
    private static final int DEFAULT_MANA_COST = 10;
    
    /**
     * 创建物品栏组件（带完整错误处理）
     * 
     * @param props 组件属性，允许为null
     * @return 物品栏组件实例，失败时返回默认实例而非null
     */
    @Nullable
    public static IModificationComponent createInventoryComponent(@Nullable Map<String, Object> props) {
        try {
            // 处理null输入
            if (props == null) {
                LOGGER.warn("物品栏组件属性为null，使用默认值");
                return createDefaultInventoryComponent();
            }
            
            // 安全提取size
            int size = extractSafeInteger(props, "size", DEFAULT_INVENTORY_SIZE);
            if (size <= 0) {
                LOGGER.warn("无效的库存大小: {}，使用默认值: {}", size, DEFAULT_INVENTORY_SIZE);
                size = DEFAULT_INVENTORY_SIZE;
            } else if (size > MAX_INVENTORY_SIZE) {
                LOGGER.warn("库存大小过大: {}，限制为: {}", size, MAX_INVENTORY_SIZE);
                size = MAX_INVENTORY_SIZE;
            }
            
            // 安全提取slotType
            String slotType = extractSafeString(props, "slotType", DEFAULT_SLOT_TYPE);
            if (slotType.trim().isEmpty()) {
                LOGGER.warn("槽位类型为空，使用默认值: {}", DEFAULT_SLOT_TYPE);
                slotType = DEFAULT_SLOT_TYPE;
            }
            
            IModificationComponent component = new InventoryComponentImpl(size, slotType);
            LOGGER.debug("成功创建物品栏组件: size={}, slotType={}", size, slotType);
            return component;
            
        } catch (ClassCastException e) {
            LOGGER.error("物品栏组件属性类型错误: {}", props, e);
            return createDefaultInventoryComponent();
        } catch (Exception e) {
            LOGGER.error("创建物品栏组件时发生未预期错误", e);
            return createDefaultInventoryComponent();
        }
    }
    
    /**
     * 创建属性组件（带完整错误处理）
     * 
     * @param props 组件属性
     * @return 属性组件实例
     */
    @Nullable
    public static IModificationComponent createAttributeComponent(@Nullable Map<String, Object> props) {
        try {
            if (props == null) {
                LOGGER.warn("属性组件属性为null，创建空属性组件");
                return new AttributeComponent(Map.of());
            }
            
            // 安全提取属性映射
            Object attributesObj = props.get("attributes");
            Map<String, Double> attributes;
            
            if (attributesObj == null) {
                LOGGER.debug("未指定属性，创建空属性组件");
                attributes = Map.of();
            } else if (attributesObj instanceof Map<?, ?> rawMap) {
                // 安全转换属性映射
                attributes = convertToAttributeMap(rawMap);
            } else {
                LOGGER.warn("属性格式错误，期望Map类型，实际: {}", attributesObj.getClass().getSimpleName());
                attributes = Map.of();
            }
            
            AttributeComponent component = new AttributeComponent(attributes);
            LOGGER.debug("成功创建属性组件，属性数量: {}", attributes.size());
            return component;
            
        } catch (Exception e) {
            LOGGER.error("创建属性组件失败", e);
            return new AttributeComponent(Map.of()); // 返回空属性组件而不是null
        }
    }
    
    /**
     * 创建技能组件（带完整错误处理）
     * 
     * @param props 组件属性
     * @return 技能组件实例
     */
    @Nullable
    public static IModificationComponent createSkillComponent(@Nullable Map<String, Object> props) {
        try {
            SkillComponent skillComponent = new SkillComponent();
            
            if (props == null) {
                LOGGER.warn("技能组件属性为null，使用默认设置");
                setupDefaultSkill(skillComponent);
                return skillComponent;
            }
            
            // 安全设置技能数据
            String skillId = extractSafeString(props, "skillId", "default_skill");
            int cooldown = extractSafeInteger(props, "cooldown", DEFAULT_COOLDOWN);
            int manaCost = extractSafeInteger(props, "manaCost", DEFAULT_MANA_COST);
            
            // 验证数值范围
            if (cooldown < 0) {
                LOGGER.warn("无效的冷却时间: {}，设置为默认值: {}", cooldown, DEFAULT_COOLDOWN);
                cooldown = DEFAULT_COOLDOWN;
            }
            
            if (manaCost < 0) {
                LOGGER.warn("无效的魔法消耗: {}，设置为默认值: {}", manaCost, DEFAULT_MANA_COST);
                manaCost = DEFAULT_MANA_COST;
            }
            
            skillComponent.setSkillData("skillId", skillId);
            skillComponent.setSkillData("cooldown", cooldown);
            skillComponent.setSkillData("manaCost", manaCost);
            
            LOGGER.debug("成功创建技能组件: skillId={}, cooldown={}, manaCost={}", 
                        skillId, cooldown, manaCost);
            return skillComponent;
            
        } catch (Exception e) {
            LOGGER.error("创建技能组件失败", e);
            // 返回默认技能组件
            SkillComponent fallback = new SkillComponent();
            setupDefaultSkill(fallback);
            return fallback;
        }
    }
    
    /**
     * 创建效果组件（带完整错误处理）
     * 
     * @param props 组件属性
     * @return 效果组件实例
     */
    @Nullable
    public static IModificationComponent createEffectComponent(@Nullable Map<String, Object> props) {
        try {
            EffectComponent component = new EffectComponent();
            
            if (props != null) {
                // 这里可以添加更多的效果配置逻辑
                LOGGER.debug("创建效果组件，属性: {}", props);
            }
            
            return component;
        } catch (Exception e) {
            LOGGER.error("创建效果组件失败", e);
            return new EffectComponent(); // 返回基本效果组件
        }
    }
    
    // =================== 私有辅助方法 ===================
    
    /**
     * 创建默认物品栏组件
     */
    private static IModificationComponent createDefaultInventoryComponent() {
        try {
            return new InventoryComponentImpl(DEFAULT_INVENTORY_SIZE, DEFAULT_SLOT_TYPE);
        } catch (Exception e) {
            LOGGER.error("无法创建默认物品栏组件", e);
            return null; // 这是真正的失败情况
        }
    }
    
    /**
     * 安全提取整数值
     */
    private static int extractSafeInteger(Map<String, Object> props, String key, int defaultValue) {
        try {
            Object value = props.get(key);
            if (value == null) return defaultValue;
            
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof Number) {
                return ((Number) value).intValue();
            } else if (value instanceof String) {
                return Integer.parseInt((String) value);
            } else {
                LOGGER.warn("无法转换为整数: key={}, value={}, type={}", 
                           key, value, value.getClass().getSimpleName());
                return defaultValue;
            }
        } catch (NumberFormatException e) {
            LOGGER.warn("数字格式错误: key={}, value={}", key, props.get(key));
            return defaultValue;
        } catch (Exception e) {
            LOGGER.warn("提取整数值时发生错误: key={}", key, e);
            return defaultValue;
        }
    }
    
    /**
     * 安全提取字符串值
     */
    private static String extractSafeString(Map<String, Object> props, String key, String defaultValue) {
        try {
            Object value = props.get(key);
            if (value == null) return defaultValue;
            
            return value.toString().trim();
        } catch (Exception e) {
            LOGGER.warn("提取字符串值时发生错误: key={}", key, e);
            return defaultValue;
        }
    }
    
    /**
     * 安全转换属性映射
     */
    private static Map<String, Double> convertToAttributeMap(Map<?, ?> rawMap) {
        Map<String, Double> result = new HashMap<>();
        
        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
            try {
                String key = entry.getKey().toString();
                Object valueObj = entry.getValue();
                
                double value;
                if (valueObj instanceof Number) {
                    value = ((Number) valueObj).doubleValue();
                } else if (valueObj instanceof String) {
                    value = Double.parseDouble((String) valueObj);
                } else {
                    LOGGER.warn("无法转换属性值为double: key={}, value={}", key, valueObj);
                    continue;
                }
                
                result.put(key, value);
            } catch (Exception e) {
                LOGGER.warn("转换属性条目失败: {}", entry, e);
            }
        }
        
        return result;
    }
    
    /**
     * 设置默认技能配置
     */
    private static void setupDefaultSkill(SkillComponent skillComponent) {
        skillComponent.setSkillData("skillId", "default_skill");
        skillComponent.setSkillData("cooldown", DEFAULT_COOLDOWN);
        skillComponent.setSkillData("manaCost", DEFAULT_MANA_COST);
    }
}
