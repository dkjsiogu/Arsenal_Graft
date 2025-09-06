package io.github.dkjsiogu.arsenalgraft.api.v3.examples;

import io.github.dkjsiogu.arsenalgraft.api.v3.ArsenalGraftAPI;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationTemplate;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.impl.InventoryComponentImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * 纯净的3.0版本：普通手改造
 * 
 * 这是Arsenal Graft 3.0的最简实现：
 * - 功能：1格物品栏
 * - 槽位类型：hand
 * - 无复杂的GUI类型系统
 * - 完全基于组件的设计
 */
public class SimpleHandModification {
    
    public static final String MODIFICATION_ID = "arsenalgraft:simple_hand";
    
    /**
     * 注册普通手改造
     */
    public static void register() {
        // 3.0版本：使用纯净的ModificationTemplate构建器
        ModificationTemplate simpleHand = new ModificationTemplate.Builder(
                ResourceLocation.fromNamespaceAndPath("arsenalgraft", "simple_hand")
            )
            .displayName(Component.literal("普通手改造"))
            .description(List.of(
                Component.literal("提供一格额外的物品栏空间"),
                Component.literal("这是Arsenal Graft 3.0的最简实现")
            ))
            .slotType("hand")
            .addComponent("inventory", new InventoryComponentImpl(1))
            .maxInstallCount(1)
            .hasConfiguration(false)
            .build();
        
        // 注册到管理器
        ArsenalGraftAPI.registerModificationTemplate(
            ResourceLocation.fromNamespaceAndPath("arsenalgraft", "simple_hand"), 
            simpleHand
        );
        
        System.out.println("[SimpleHandModification] 已注册普通手改造：" + MODIFICATION_ID);
    }
    
    /**
     * 获取改造模板（用于外部引用）
     */
    public static ModificationTemplate getTemplate() {
        return ArsenalGraftAPI.getModificationTemplate(
            ResourceLocation.fromNamespaceAndPath("arsenalgraft", "simple_hand")
        );
    }
    
    /**
     * 检查玩家是否有此改造
     */
    public static boolean hasModification(net.minecraft.world.entity.player.Player player) {
        return ArsenalGraftAPI.hasModification(player, 
            ResourceLocation.fromNamespaceAndPath("arsenalgraft", "simple_hand"));
    }
    
    /**
     * 为玩家授予此改造
     */
    public static boolean grantToPlayer(net.minecraft.world.entity.player.Player player) {
        return ArsenalGraftAPI.grantModification(player, 
            ResourceLocation.fromNamespaceAndPath("arsenalgraft", "simple_hand"));
    }
    
    /**
     * 从玩家移除此改造
     */
    public static boolean removeFromPlayer(net.minecraft.world.entity.player.Player player) {
        return ArsenalGraftAPI.removeModification(player, 
            ResourceLocation.fromNamespaceAndPath("arsenalgraft", "simple_hand"));
    }
    
    /**
     * 创建用于测试的物品解锁器
     */
    public static void createTestUnlocker() {
        System.out.println("[SimpleHandModification] 使用以下代码创建解锁器物品:");
        System.out.println("ArsenalGraftAPI.grantModification(player, " + 
            "ResourceLocation.fromNamespaceAndPath(\"arsenalgraft\", \"simple_hand\"));");
    }
    
    /**
     * 创建JSON配置示例
     */
    public static void printJsonExample() {
        System.out.println("[SimpleHandModification] JSON配置示例:");
        System.out.println("""
            {
              "id": "arsenalgraft:simple_hand",
              "display_name": "普通手改造",
              "description": [
                "提供一格额外的物品栏空间",
                "这是Arsenal Graft 3.0的最简实现"
              ],
              "slot_type": "hand",
              "components": {
                "inventory": {
                  "slot_count": 1,
                  "slot_type": "default"
                }
              },
              "max_install_count": 1,
              "has_configuration": false
            }
            """);
    }
    
    /**
     * 创建KubeJS脚本示例
     */
    public static void printKubeJSExample() {
        System.out.println("[SimpleHandModification] KubeJS脚本示例:");
        System.out.println("""
            // 在 kubejs/startup_scripts/arsenalgraft.js 中添加:
            ArsenalGraft.createModification('arsenalgraft:simple_hand')
                .displayName('普通手改造')
                .description('提供一格额外的物品栏空间')
                .slotType('hand')
                .addInventoryComponent(1)
                .maxInstallCount(1)
                .register();
            """);
    }
}
