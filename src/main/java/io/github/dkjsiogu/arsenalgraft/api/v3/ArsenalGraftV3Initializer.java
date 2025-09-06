package io.github.dkjsiogu.arsenalgraft.api.v3;

import io.github.dkjsiogu.arsenalgraft.core.service.ServiceRegistry;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationManager;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.impl.ModificationManagerImpl;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.ComponentRegistry;
// ...existing code...

/**
 * Arsenal Graft 3.0 系统初始化器
 * 
 * 负责初始化整个3.0框架系统
 */
public class ArsenalGraftV3Initializer {
    
    private static boolean initialized = false;
    
    /**
     * 初始化3.0系统
     * 
     * 这个方法应该在模组的初始化阶段调用
     */
    public static void initialize() {
        if (initialized) {
            System.out.println("[ArsenalGraftV3] 系统已经初始化过了");
            return;
        }
        
        System.out.println("[ArsenalGraftV3] 开始初始化3.0系统...");
        
        try {
            // 1. 初始化服务注册中心
            ServiceRegistry.getInstance().initializeCoreServices();
            System.out.println("[ArsenalGraftV3] 服务注册中心初始化完成");
            
            // 2. 初始化组件系统
            System.out.println("[ArsenalGraftV3] 组件系统初始化完成，可用组件类型: " + ComponentRegistry.getAllComponentTypes().size());
            ComponentRegistry.getAllComponentTypes().forEach((type, info) -> {
                System.out.println("[ArsenalGraftV3]   - " + type + ": " + info.displayName + " (" + info.category.displayName + ")");
            });
            
            // 3. 获取管理器实例
            ModificationManager manager = ServiceRegistry.getModificationManager();
            System.out.println("[ArsenalGraftV3] 改造管理器初始化完成");
            
            // 4. 尝试注册示例改造（如果示例存在就加载，不存在则忽略）
            try {
                Class<?> cls = Class.forName("io.github.dkjsiogu.arsenalgraft.api.v3.examples.ExampleModifications");
                var method = cls.getMethod("registerExamples");
                method.invoke(null);
                System.out.println("[ArsenalGraftV3] 示例改造注册完成 (通过反射)");
            } catch (ClassNotFoundException cnf) {
                System.out.println("[ArsenalGraftV3] 示例改造类未找到，跳过示例注册");
            }
            
            // 5. 标记为已初始化
            initialized = true;
            
            System.out.println("[ArsenalGraftV3] 3.0系统初始化完成！");
            System.out.println("[ArsenalGraftV3] 统一API入口: ArsenalGraftAPI.grantModification()");
            
            // 打印可用的改造模板
            if (manager instanceof ModificationManagerImpl impl) {
                System.out.println("[ArsenalGraftV3] 可用改造模板数量: " + impl.getAllTemplates().size());
                impl.getAllTemplates().forEach((id, template) -> {
                    System.out.println("[ArsenalGraftV3]   - " + id + " (" + template.getSlotType() + ")");
                });
            }
            
        } catch (Exception e) {
            System.err.println("[ArsenalGraftV3] 初始化失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Arsenal Graft 3.0", e);
        }
    }
    
    /**
     * 检查系统是否已初始化
     */
    public static boolean isInitialized() {
        return initialized;
    }
    
    /**
     * 获取管理器（仅在初始化后可用）
     */
    public static ModificationManager getManager() {
        if (!initialized) {
            throw new IllegalStateException("Arsenal Graft 3.0 not initialized! Call initialize() first.");
        }
        return ServiceRegistry.getModificationManager();
    }
}
