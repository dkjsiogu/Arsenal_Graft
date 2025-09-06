package io.github.dkjsiogu.arsenalgraft.api.v3.examples;

import io.github.dkjsiogu.arsenalgraft.api.v3.ArsenalGraftAPI;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationTemplate;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.impl.InventoryComponentImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * 实体背包改造
 * 
 * 功能：
 * - 9格物品栏（3x3）
 * - 槽位类型：chest
 * - 大容量存储
 */
public class EntityBackpackModification {
    
    public static final String MODIFICATION_ID = "arsenalgraft:entity_backpack";
    
    /**
     * 注册实体背包改造
     */
    public static void register() {
        ModificationTemplate entityBackpack = new ModificationTemplate.Builder(
                ResourceLocation.fromNamespaceAndPath("arsenalgraft", "entity_backpack")
            )
            .displayName(Component.literal("实体背包"))
            .description(List.of(
                Component.literal("提供9格额外的物品栏空间"),
                Component.literal("高容量背包，采用实体技术"),
                Component.literal("适合长期探险和大量物品收集")
            ))
            .slotType("chest")
            .addComponent("inventory", new InventoryComponentImpl(9))
            .maxInstallCount(1)
            .hasConfiguration(true)
            .build();
        
        // 注册到管理器
        ArsenalGraftAPI.registerModificationTemplate(
            ResourceLocation.fromNamespaceAndPath("arsenalgraft", "entity_backpack"), 
            entityBackpack
        );
        
        System.out.println("[EntityBackpackModification] 已注册实体背包改造：" + MODIFICATION_ID);
    }
    
    /**
     * 获取改造模板
     */
    public static ModificationTemplate getTemplate() {
        return ArsenalGraftAPI.getModificationTemplate(
            ResourceLocation.fromNamespaceAndPath("arsenalgraft", "entity_backpack")
        );
    }
    
    /**
     * 检查玩家是否有此改造
     */
    public static boolean hasModification(net.minecraft.world.entity.player.Player player) {
        return ArsenalGraftAPI.hasModification(player, 
            ResourceLocation.fromNamespaceAndPath("arsenalgraft", "entity_backpack"));
    }
    
    /**
     * 为玩家授予此改造
     */
    public static boolean grantToPlayer(net.minecraft.world.entity.player.Player player) {
        return ArsenalGraftAPI.grantModification(player, 
            ResourceLocation.fromNamespaceAndPath("arsenalgraft", "entity_backpack"));
    }
    
    /**
     * 从玩家移除此改造
     */
    public static boolean removeFromPlayer(net.minecraft.world.entity.player.Player player) {
        return ArsenalGraftAPI.removeModification(player, 
            ResourceLocation.fromNamespaceAndPath("arsenalgraft", "entity_backpack"));
    }
}
