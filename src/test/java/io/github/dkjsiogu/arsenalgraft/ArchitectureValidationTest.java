package io.github.dkjsiogu.arsenalgraft;

import io.github.dkjsiogu.arsenalgraft.core.service.ServiceRegistry;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.ComponentRegistry;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.IModificationComponent;

/**
 * 验证Arsenal Graft 3.0架构的简单测试
 * 
 * 此类用于验证重构后的架构是否正常工作
 */
public class ArchitectureValidationTest {
    
    public static void main(String[] args) {
        System.out.println("开始验证Arsenal Graft 3.0架构...");
        
        try {
            // 测试1: 服务注册表初始化
            testServiceRegistryInitialization();
            System.out.println("✓ 服务注册表初始化测试通过");
            
            // 测试2: 组件注册表功能
            testComponentRegistryFunctionality();
            System.out.println("✓ 组件注册表功能测试通过");
            
            // 测试3: 组件创建
            testComponentCreation();
            System.out.println("✓ 组件创建测试通过");
            
            System.out.println("\n🎉 所有架构验证测试都通过了！");
            System.out.println("Arsenal Graft 3.0 架构重构成功完成");
            
        } catch (Exception e) {
            System.err.println("❌ 架构验证失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testServiceRegistryInitialization() {
        // 验证服务注册表可以正常初始化
        ServiceRegistry registry = ServiceRegistry.getInstance();
        if (registry == null) {
            throw new RuntimeException("服务注册表不应该为null");
        }
        
        // 初始化核心服务
        registry.initializeCoreServices();
        System.out.println("  - 服务注册表已成功初始化");
    }
    
    private static void testComponentRegistryFunctionality() {
        // 验证组件注册表包含预期的组件类型
        String[] expectedComponents = {
            "attribute_modification",
            "skill", 
            "effect",
            "inventory"
        };
        
        for (String componentType : expectedComponents) {
            if (!componentExists(componentType)) {
                throw new RuntimeException("缺少组件类型: " + componentType);
            }
        }
        System.out.println("  - 所有预期的组件类型都已注册");
    }
    
    private static void testComponentCreation() {
        // 验证可以创建组件实例
        IModificationComponent attributeComp = ComponentRegistry.createComponent("attribute_modification");
        if (attributeComp == null) {
            throw new RuntimeException("应该能创建属性修改组件");
        }
        
        IModificationComponent skillComp = ComponentRegistry.createComponent("skill");
        if (skillComp == null) {
            throw new RuntimeException("应该能创建技能组件");
        }
        
        System.out.println("  - 组件实例创建成功");
    }
    
    private static boolean componentExists(String componentType) {
        try {
            IModificationComponent component = ComponentRegistry.createComponent(componentType);
            return component != null;
        } catch (Exception e) {
            return false;
        }
    }
}
