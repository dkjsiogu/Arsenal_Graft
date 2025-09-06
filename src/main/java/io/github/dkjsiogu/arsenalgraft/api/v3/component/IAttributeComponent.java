package io.github.dkjsiogu.arsenalgraft.api.v3.component;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.Map;
import java.util.UUID;

/**
 * 属性组件接口
 * 
 * 为改造提供属性修改功能，如攻击力、防御力、移动速度等。
 */
public interface IAttributeComponent extends IModificationComponent {
    
    /**
     * 组件类型常量
     */
    String COMPONENT_TYPE = "attribute";
    
    @Override
    default String getComponentType() {
        return COMPONENT_TYPE;
    }
    
    /**
     * 获取所有属性修饰符
     */
    Map<Attribute, AttributeModifier> getAttributeModifiers();
    
    /**
     * 添加属性修饰符
     */
    void addAttributeModifier(Attribute attribute, AttributeModifier modifier);
    
    /**
     * 移除属性修饰符
     */
    void removeAttributeModifier(Attribute attribute, UUID modifierId);
    
    /**
     * 检查是否有指定属性的修饰符
     */
    boolean hasModifier(Attribute attribute);
    
    /**
     * 获取指定属性的修饰符
     */
    AttributeModifier getModifier(Attribute attribute);
}
