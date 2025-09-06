package io.github.dkjsiogu.arsenalgraft.api.v3.examples;

import io.github.dkjsiogu.arsenalgraft.api.v3.ArsenalGraftAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

/**
 * 改造解锁物品示例
 * 
 * 展示如何通过物品使用统一的 ArsenalGraftAPI.grantModification 接口
 */
public class ModificationUnlockerItem extends Item {
    
    private final ResourceLocation modificationId;
    
    public ModificationUnlockerItem(Properties properties, ResourceLocation modificationId) {
        super(properties);
        this.modificationId = modificationId;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (!level.isClientSide) {
            // 服务端逻辑：调用统一API
            boolean success = ArsenalGraftAPI.grantModification(player, modificationId);
            
            if (success) {
                player.displayClientMessage(
                    Component.literal("成功解锁改造: " + modificationId), 
                    false
                );
                
                // 消耗物品
                player.getItemInHand(hand).shrink(1);
                
            } else {
                player.displayClientMessage(
                    Component.literal("无法解锁改造: " + modificationId + " (可能已拥有或不满足条件)"), 
                    false
                );
            }
        }
        
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
    
    /**
     * 创建基础手部解锁器
     */
    public static ModificationUnlockerItem createBasicHandUnlocker(Properties properties) {
        return new ModificationUnlockerItem(
            properties, 
            ResourceLocation.fromNamespaceAndPath("arsenalgraft", "basic_hand")
        );
    }
    
    /**
     * 创建高级手臂解锁器
     */
    public static ModificationUnlockerItem createAdvancedArmUnlocker(Properties properties) {
        return new ModificationUnlockerItem(
            properties, 
            ResourceLocation.fromNamespaceAndPath("arsenalgraft", "advanced_arm")
        );
    }
    
    /**
     * 创建实用背包解锁器
     */
    public static ModificationUnlockerItem createUtilityBackpackUnlocker(Properties properties) {
        return new ModificationUnlockerItem(
            properties, 
            ResourceLocation.fromNamespaceAndPath("arsenalgraft", "utility_backpack")
        );
    }
}
