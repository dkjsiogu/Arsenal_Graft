package io.github.dkjsiogu.arsenalgraft.test;

import io.github.dkjsiogu.arsenalgraft.core.service.ServiceRegistry;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.ComponentRegistry;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.IModificationComponent;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationManager;

/**
 * 架构验证器 - 用于验证Arsenal Graft 3.0架构改进
 * 
 * 这个类在模组启动时运行，验证所有架构改进是否正常工作
 */
public class ArchitectureValidator {
    
    public static boolean validateArchitecture() {
        try {
            System.out.println("=== Arsenal Graft 3.0 架构验证开始 ===");
            
            // 测试1: 验证服务注册表
            boolean serviceRegistryTest = testServiceRegistry();
            System.out.println("服务注册表测试: " + (serviceRegistryTest ? "✓ 通过" : "✗ 失败"));
            
            // 测试2: 验证组件注册表
            boolean componentRegistryTest = testComponentRegistry();
            System.out.println("组件注册表测试: " + (componentRegistryTest ? "✓ 通过" : "✗ 失败"));
            
            // 测试3: 验证线程安全性
            boolean threadSafetyTest = testThreadSafety();
            System.out.println("线程安全测试: " + (threadSafetyTest ? "✓ 通过" : "✗ 失败"));
            
            // 测试4: 验证组件创建和序列化
            boolean componentSerializationTest = testComponentSerialization();
            System.out.println("组件序列化测试: " + (componentSerializationTest ? "✓ 通过" : "✗ 失败"));
            
            boolean allPassed = serviceRegistryTest && componentRegistryTest && 
                              threadSafetyTest && componentSerializationTest;
            
            System.out.println("=== 架构验证完成: " + (allPassed ? "所有测试通过 🎉" : "部分测试失败 ❌") + " ===");
            
            return allPassed;
            
        } catch (Exception e) {
            System.err.println("架构验证过程中发生异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static boolean testServiceRegistry() {
        try {
            ServiceRegistry registry = ServiceRegistry.getInstance();
            
            // 测试服务注册
            registry.initializeCoreServices();
            
            // 验证服务可以正常获取
            ModificationManager manager = registry.getService(ModificationManager.class);
            if (manager == null) {
                System.err.println("  - 无法获取ModificationManager服务");
                return false;
            }
            
            // 验证便捷方法
            ModificationManager manager2 = ServiceRegistry.getModificationManager();
            if (manager2 != manager) {
                System.err.println("  - 便捷方法返回的服务实例不一致");
                return false;
            }
            
            System.out.println("  - 服务注册和获取正常");
            return true;
            
        } catch (Exception e) {
            System.err.println("  - 服务注册表测试异常: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testComponentRegistry() {
        try {
            // 测试预期组件类型
            String[] expectedComponents = {
                "attribute_modification",
                "skill", 
                "effect",
                "inventory"
            };
            
            for (String componentType : expectedComponents) {
                if (!ComponentRegistry.isComponentRegistered(componentType)) {
                    System.err.println("  - 组件类型未注册: " + componentType);
                    return false;
                }
                
                // 测试组件创建
                IModificationComponent component = ComponentRegistry.createComponent(componentType);
                if (component == null) {
                    System.err.println("  - 无法创建组件: " + componentType);
                    return false;
                }
                
                // 验证组件类型
                if (!componentType.equals(component.getComponentType())) {
                    System.err.println("  - 组件类型不匹配: 期望=" + componentType + ", 实际=" + component.getComponentType());
                    return false;
                }
            }
            
            System.out.println("  - 所有组件类型注册和创建正常");
            return true;
            
        } catch (Exception e) {
            System.err.println("  - 组件注册表测试异常: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testThreadSafety() {
        try {
            ServiceRegistry registry = ServiceRegistry.getInstance();
            
            // 简单的并发测试
            Thread[] threads = new Thread[5];
            boolean[] results = new boolean[5];
            
            for (int i = 0; i < 5; i++) {
                final int index = i;
                threads[i] = new Thread(() -> {
                    try {
                        // 并发访问服务注册表
                        for (int j = 0; j < 10; j++) {
                            ModificationManager manager = registry.getService(ModificationManager.class);
                            if (manager == null) {
                                results[index] = false;
                                return;
                            }
                        }
                        results[index] = true;
                    } catch (Exception e) {
                        results[index] = false;
                    }
                });
                threads[i].start();
            }
            
            // 等待所有线程完成
            for (Thread thread : threads) {
                thread.join(1000); // 最多等待1秒
            }
            
            // 检查结果
            for (boolean result : results) {
                if (!result) {
                    System.err.println("  - 线程安全测试失败");
                    return false;
                }
            }
            
            System.out.println("  - 线程安全测试通过");
            return true;
            
        } catch (Exception e) {
            System.err.println("  - 线程安全测试异常: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testComponentSerialization() {
        try {
            // 测试各种组件的序列化和反序列化
            String[] componentTypes = {"attribute_modification", "skill", "effect", "inventory"};
            
            for (String type : componentTypes) {
                IModificationComponent original = ComponentRegistry.createComponent(type);
                
                // 设置组件为激活状态
                original.setActive(true);
                
                // 序列化
                net.minecraft.nbt.CompoundTag nbt = original.serializeNBT();
                if (nbt == null || nbt.isEmpty()) {
                    System.err.println("  - 组件序列化失败: " + type);
                    return false;
                }
                
                // 创建新实例并反序列化
                IModificationComponent copy = ComponentRegistry.createComponent(type);
                copy.deserializeNBT(nbt);
                
                // 验证状态
                if (original.isActive() != copy.isActive()) {
                    System.err.println("  - 组件状态反序列化不匹配: " + type);
                    return false;
                }
                
                if (!original.getComponentType().equals(copy.getComponentType())) {
                    System.err.println("  - 组件类型反序列化不匹配: " + type);
                    return false;
                }
            }
            
            System.out.println("  - 组件序列化测试通过");
            return true;
            
        } catch (Exception e) {
            System.err.println("  - 组件序列化测试异常: " + e.getMessage());
            return false;
        }
    }
}
