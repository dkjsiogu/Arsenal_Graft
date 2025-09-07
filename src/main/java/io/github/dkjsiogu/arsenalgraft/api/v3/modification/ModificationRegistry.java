package io.github.dkjsiogu.arsenalgraft.api.v3.modification;

import io.github.dkjsiogu.arsenalgraft.api.v3.ArsenalGraftAPI;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.impl.InventoryComponentImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * 改造注册表
 * 
 * 负责注册内置的改造模板。
 * 展示如何使用统一API创建改造。
 */
public class ModificationRegistry {
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    /**
     * 注册所有内置改造
     */
    public static void registerBuiltinModifications() {
        LOGGER.info("开始注册内置改造模板...");
        
        try {
            registerExtraHand();
            registerNormalHand();
            registerStoragePouch();
            
            LOGGER.info("内置改造模板注册完成");
        } catch (Exception e) {
            LOGGER.error("注册内置改造模板失败", e);
        }
    }
    
    /**
     * 注册额外手部改造
     */
    private static void registerExtraHand() {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("arsenalgraft", "extra_hand");
        
        ModificationTemplate.Builder builder = new ModificationTemplate.Builder(id)
            .displayName(Component.literal("额外手部"))
            .description(List.of(
                Component.literal("为玩家提供额外的手部存储空间"),
                Component.literal("可以存放各种物品")
            ))
            .slotType("hand")
            .maxInstallCount(2);
        
        // 添加物品栏组件
        InventoryComponentImpl inventoryComponent = new InventoryComponentImpl(9, "hand");
        builder.addComponent("inventory", inventoryComponent);
        
        ModificationTemplate template = builder.build();
        ArsenalGraftAPI.registerModificationTemplate(id, template);
        
        LOGGER.debug("注册改造模板: {}", id);
    }
    
    /**
     * 注册存储袋改造
     */
    private static void registerStoragePouch() {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("arsenalgraft", "storage_pouch");
        
        ModificationTemplate.Builder builder = new ModificationTemplate.Builder(id)
            .displayName(Component.literal("存储袋"))
            .description(List.of(
                Component.literal("提供大容量存储空间"),
                Component.literal("可以存放更多物品")
            ))
            .slotType("storage")
            .maxInstallCount(1);
        
        // 添加大容量物品栏组件
        InventoryComponentImpl inventoryComponent = new InventoryComponentImpl(27, "storage");
        builder.addComponent("inventory", inventoryComponent);
        
        ModificationTemplate template = builder.build();
        ArsenalGraftAPI.registerModificationTemplate(id, template);
        
        LOGGER.debug("注册改造模板: {}", id);
    }

    /**
     * 注册"普通手"改造: 安装后提供 1 个额外物品栏槽位（使用 hand 类型，用于演示动态扩展）。
     */
    private static void registerNormalHand() {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("arsenalgraft", "normal_hand");

        ModificationTemplate.Builder builder = new ModificationTemplate.Builder(id)
            .displayName(Component.literal("普通手"))
            .description(List.of(
                Component.literal("提供 1 个普通手槽位"),
                Component.literal("可重复安装以累加槽位 (受最大安装次数限制)")
            ))
            .slotType("hand")
            .maxInstallCount(Integer.MAX_VALUE); // 移除上限：允许大量安装用于协同测试

        // 仅 1 槽位的 hand 类型库存
        InventoryComponentImpl inventoryComponent = new InventoryComponentImpl(1, "hand");
        builder.addComponent("inventory", inventoryComponent);

        ModificationTemplate template = builder.build();
        ArsenalGraftAPI.registerModificationTemplate(id, template);
        LOGGER.debug("注册改造模板: {}", id);
    }
}
