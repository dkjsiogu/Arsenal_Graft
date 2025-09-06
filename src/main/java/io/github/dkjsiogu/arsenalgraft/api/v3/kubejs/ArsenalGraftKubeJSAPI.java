package io.github.dkjsiogu.arsenalgraft.api.v3.kubejs;

import io.github.dkjsiogu.arsenalgraft.api.v3.ArsenalGraftAPI;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationTemplate;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.impl.InventoryComponentImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.ArrayList;

/**
 * KubeJS集成API
 * 
 * 这个类展示了如何为KubeJS提供JavaScript友好的API。
 * 重要的是：这些方法最终都会调用相同的底层逻辑，
 * 实现了"单一数据后端"的设计原则。
 */
public class ArsenalGraftKubeJSAPI {
    
    /**
     * 创建改造构建器（KubeJS风格的链式调用）
     * 
     * JavaScript示例:
     * ArsenalGraft.createModification('my_pack:scripted_arm')
     *     .displayName('脚本化机械臂')
     *     .slotType('arm')
     *     .addInventoryComponent(2, 'arm')
     *     .maxInstallCount(1)
     *     .register();
     */
    public static ModificationBuilder createModification(String idString) {
        ResourceLocation id = ResourceLocation.tryParse(idString);
        if (id == null) {
            throw new IllegalArgumentException("Invalid modification ID: " + idString);
        }
        return new ModificationBuilder(id);
    }
    
    /**
     * 构建器类 - 提供JavaScript友好的链式API
     */
    public static class ModificationBuilder {
        private final ModificationTemplate.Builder templateBuilder;
        private final ResourceLocation id;
        
        public ModificationBuilder(ResourceLocation id) {
            this.id = id;
            this.templateBuilder = new ModificationTemplate.Builder(id);
        }
        
        /**
         * 设置显示名称
         */
        public ModificationBuilder displayName(String displayName) {
            templateBuilder.displayName(Component.literal(displayName));
            return this;
        }
        
        /**
         * 设置描述（单行）
         */
        public ModificationBuilder description(String description) {
            templateBuilder.description(List.of(Component.literal(description)));
            return this;
        }
        
        /**
         * 设置描述（多行）
         */
        public ModificationBuilder description(String[] descriptions) {
            List<Component> components = new ArrayList<>();
            for (String desc : descriptions) {
                components.add(Component.literal(desc));
            }
            templateBuilder.description(components);
            return this;
        }
        
        /**
         * 设置槽位类型
         */
        public ModificationBuilder slotType(String slotType) {
            templateBuilder.slotType(slotType);
            return this;
        }
        
        /**
         * 添加物品栏组件
         */
        public ModificationBuilder addInventoryComponent(int slotCount, String slotType) {
            InventoryComponentImpl component = new InventoryComponentImpl(slotCount, slotType);
            templateBuilder.addComponent("inventory", component);
            return this;
        }
        
        /**
         * 添加简单的物品栏组件（使用默认槽位类型）
         */
        public ModificationBuilder addInventoryComponent(int slotCount) {
            return addInventoryComponent(slotCount, "default");
        }
        
        /**
         * 设置最大安装数量
         */
        public ModificationBuilder maxInstallCount(int count) {
            templateBuilder.maxInstallCount(count);
            return this;
        }
        
        /**
         * 设置是否有配置选项
         */
        public ModificationBuilder hasConfiguration(boolean hasConfig) {
            templateBuilder.hasConfiguration(hasConfig);
            return this;
        }
        
        /**
         * 注册改造（这是链式调用的终点）
         */
        public void register() {
            try {
                ModificationTemplate template = templateBuilder.build();
                ArsenalGraftAPI.registerModificationTemplate(id, template);
                
                System.out.println("[KubeJS] 注册改造: " + id);
                
            } catch (Exception e) {
                System.err.println("[KubeJS] 注册改造失败: " + id + " - " + e.getMessage());
                throw new RuntimeException("Failed to register modification: " + id, e);
            }
        }
    }
    
    /**
     * 直接授予改造给玩家（用于KubeJS事件脚本）
     * 
     * JavaScript示例:
     * ArsenalGraft.grantModification(player, 'my_pack:reward_arm');
     */
    public static boolean grantModification(Object playerObj, String modificationIdString) {
        try {
            // 在实际实现中，这里需要处理KubeJS的Player对象转换
            // 现在仅作为示例展示API结构
            
            ResourceLocation modificationId = ResourceLocation.tryParse(modificationIdString);
            if (modificationId == null) {
                System.err.println("[KubeJS] 无效的改造ID: " + modificationIdString);
                return false;
            }
            
            // 3.0版本：转换KubeJS Player对象为Minecraft Player
            net.minecraft.world.entity.player.Player mcPlayer = convertKubeJSPlayer(playerObj);
            if (mcPlayer == null) {
                System.err.println("[KubeJS] 无法转换玩家对象: " + playerObj);
                return false;
            }
            
            return ArsenalGraftAPI.grantModification(mcPlayer, modificationId);
            
        } catch (Exception e) {
            System.err.println("[KubeJS] 授予改造失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 检查玩家是否有指定改造
     */
    public static boolean hasModification(Object playerObj, String modificationIdString) {
        try {
            ResourceLocation modificationId = ResourceLocation.tryParse(modificationIdString);
            if (modificationId == null) {
                return false;
            }
            
            // 3.0版本：转换玩家对象并检查改造
            net.minecraft.world.entity.player.Player mcPlayer = convertKubeJSPlayer(playerObj);
            if (mcPlayer == null) {
                System.err.println("[KubeJS] 无法转换玩家对象: " + playerObj);
                return false;
            }
            
            return ArsenalGraftAPI.hasModification(mcPlayer, modificationId);
            
        } catch (Exception e) {
            System.err.println("[KubeJS] 检查改造失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 3.0版本：将KubeJS Player对象转换为Minecraft Player
     */
    private static net.minecraft.world.entity.player.Player convertKubeJSPlayer(Object playerObj) {
        if (playerObj == null) {
            return null;
        }
        
        // 如果已经是Minecraft Player对象
        if (playerObj instanceof net.minecraft.world.entity.player.Player mcPlayer) {
            return mcPlayer;
        }
        
        // 处理KubeJS的Player包装器
        try {
            // 尝试反射获取底层的Minecraft Player
            var playerClass = playerObj.getClass();
            
            // 常见的KubeJS Player包装器字段名
            String[] possibleFields = {"player", "minecraftPlayer", "entity", "mcPlayer"};
            
            for (String fieldName : possibleFields) {
                try {
                    var field = playerClass.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object fieldValue = field.get(playerObj);
                    
                    if (fieldValue instanceof net.minecraft.world.entity.player.Player mcPlayer) {
                        return mcPlayer;
                    }
                } catch (NoSuchFieldException | IllegalAccessException ignored) {
                    // 继续尝试下一个字段
                }
            }
            
            System.err.println("[KubeJS] 无法从KubeJS Player对象提取Minecraft Player: " + playerClass.getName());
            return null;
            
        } catch (Exception e) {
            System.err.println("[KubeJS] 转换Player对象失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 提供一些预设的改造模板（方便脚本使用）
     */
    public static class Presets {
        
        /**
         * 创建基础手部改造
         */
        public static ModificationBuilder basicHand(String id) {
            return createModification(id)
                .displayName("基础额外手")
                .description("提供一个额外的手部槽位")
                .slotType("hand")
                .addInventoryComponent(1, "hand")
                .maxInstallCount(2)
                .hasConfiguration(true);
        }
        
        /**
         * 创建工具腰带改造
         */
        public static ModificationBuilder toolBelt(String id) {
            return createModification(id)
                .displayName("工具腰带")
                .description(new String[]{
                    "提供多个工具槽位",
                    "快速访问常用工具",
                    "冒险者的好伙伴"
                })
                .slotType("belt")
                .addInventoryComponent(6, "tool")
                .maxInstallCount(1)
                .hasConfiguration(false);
        }
        
        /**
         * 创建存储背包改造
         */
        public static ModificationBuilder storageBackpack(String id) {
            return createModification(id)
                .displayName("存储背包")
                .description("大容量的额外存储空间")
                .slotType("backpack")
                .addInventoryComponent(27, "storage")
                .maxInstallCount(1)
                .hasConfiguration(true);
        }
    }
}
