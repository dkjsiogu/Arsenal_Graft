package io.github.dkjsiogu.arsenalgraft.api.v3.examples;

import io.github.dkjsiogu.arsenalgraft.api.v3.ArsenalGraftAPI;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationTemplate;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.impl.InventoryComponentImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * 高级机械臂改造
 * 
 * 功能：
 * - 4格物品栏
 * - 槽位类型：right_arm
 * - 具有配置选项
 */
public class AdvancedMechanicalArmModification {
    
    public static final String MODIFICATION_ID = "arsenalgraft:advanced_mechanical_arm";
    
    /**
     * 注册高级机械臂改造
     */
    public static void register() {
        ModificationTemplate advancedArm = new ModificationTemplate.Builder(
                ResourceLocation.fromNamespaceAndPath("arsenalgraft", "advanced_mechanical_arm")
            )
            .displayName(Component.literal("高级机械臂"))
            .description(List.of(
                Component.literal("提供4格额外的物品栏空间"),
                Component.literal("高科技机械臂，具有智能抓取功能"),
                Component.literal("适合需要大量工具的专业工作")
            ))
            .slotType("right_arm")
            .addComponent("inventory", new InventoryComponentImpl(4))
            .maxInstallCount(1)
            .hasConfiguration(true)
            .build();
        
        // 注册到管理器
        ArsenalGraftAPI.registerModificationTemplate(
            ResourceLocation.fromNamespaceAndPath("arsenalgraft", "advanced_mechanical_arm"), 
            advancedArm
        );
        
        System.out.println("[AdvancedMechanicalArmModification] 已注册高级机械臂改造：" + MODIFICATION_ID);
    }
    
    /**
     * 获取改造模板
     */
    public static ModificationTemplate getTemplate() {
        return ArsenalGraftAPI.getModificationTemplate(
            ResourceLocation.fromNamespaceAndPath("arsenalgraft", "advanced_mechanical_arm")
        );
    }
    
    /**
     * 检查玩家是否有此改造
     */
    public static boolean hasModification(net.minecraft.world.entity.player.Player player) {
        return ArsenalGraftAPI.hasModification(player, 
            ResourceLocation.fromNamespaceAndPath("arsenalgraft", "advanced_mechanical_arm"));
    }
    
    /**
     * 为玩家授予此改造
     */
    public static boolean grantToPlayer(net.minecraft.world.entity.player.Player player) {
        return ArsenalGraftAPI.grantModification(player, 
            ResourceLocation.fromNamespaceAndPath("arsenalgraft", "advanced_mechanical_arm"));
    }
    
    /**
     * 从玩家移除此改造
     */
    public static boolean removeFromPlayer(net.minecraft.world.entity.player.Player player) {
        return ArsenalGraftAPI.removeModification(player, 
            ResourceLocation.fromNamespaceAndPath("arsenalgraft", "advanced_mechanical_arm"));
    }
}
