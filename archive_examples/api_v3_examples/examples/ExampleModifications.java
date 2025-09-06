package io.github.dkjsiogu.arsenalgraft.api.v3.examples;

import io.github.dkjsiogu.arsenalgraft.api.v3.ArsenalGraftAPI;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationTemplate;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.impl.InventoryComponentImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * 示例改造模板注册
 * 
 * 展示如何通过代码创建和注册改造模板，这与KubeJS的工作方式相同。
 */
public class ExampleModifications {
    
    /**
     * 注册所有示例改造
     */
    public static void registerExamples() {
        registerBasicHandModification();
        registerAdvancedArmModification();
        registerUtilityBackpackModification();
    }
    
    /**
     * 基础手部改造 - 兼容原有的普通手功能
     */
    private static void registerBasicHandModification() {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("arsenalgraft", "basic_hand");
        
        ModificationTemplate template = new ModificationTemplate.Builder(id)
                .displayName(Component.literal("基础额外手"))
                .description(List.of(
                        Component.literal("提供一个额外的手部插槽"),
                        Component.literal("可以握持任何物品"),
                        Component.literal("兼容现有的手部系统")
                ))
                .slotType("hand") // 用于GUI分类
                .addComponent("inventory", new InventoryComponentImpl(1, "hand"))
                .maxInstallCount(2) // 最多2只手
                .hasConfiguration(true)
                .build();
        
        ArsenalGraftAPI.registerModificationTemplate(id, template);
    }
    
    /**
     * 高级手臂改造 - 多槽位，可配置
     */
    private static void registerAdvancedArmModification() {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("arsenalgraft", "advanced_arm");
        
        ModificationTemplate template = new ModificationTemplate.Builder(id)
                .displayName(Component.literal("高级机械臂"))
                .description(List.of(
                        Component.literal("提供多个物品槽位"),
                        Component.literal("支持快速切换"),
                        Component.literal("可自定义配置")
                ))
                .slotType("arm")
                .addComponent("inventory", new InventoryComponentImpl(4, "arm"))
                .maxInstallCount(1) // 只能有一个
                .hasConfiguration(true)
                .build();
        
        ArsenalGraftAPI.registerModificationTemplate(id, template);
    }
    
    /**
     * 实用背包改造 - 展示不同槽位类型
     */
    private static void registerUtilityBackpackModification() {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("arsenalgraft", "utility_backpack");
        
        ModificationTemplate template = new ModificationTemplate.Builder(id)
                .displayName(Component.literal("实用背包"))
                .description(List.of(
                        Component.literal("提供额外的存储空间"),
                        Component.literal("专门用于工具和材料"),
                        Component.literal("自动整理功能")
                ))
                .slotType("backpack")
                .addComponent("inventory", new InventoryComponentImpl(9, "utility"))
                .maxInstallCount(1)
                .hasConfiguration(false)
                .build();
        
        ArsenalGraftAPI.registerModificationTemplate(id, template);
    }
}
