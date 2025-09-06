package io.github.dkjsiogu.arsenalgraft.core;

import io.github.dkjsiogu.arsenalgraft.api.ExtraHandType;
import io.github.dkjsiogu.arsenalgraft.api.IExtraHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;

/**
 * 额外手的基础实现 - 整合普通手管理器
 */
public class ExtraHandImpl implements IExtraHand {
    
    private final ExtraHandType handType;
    private ItemStack heldItem = ItemStack.EMPTY;
    private int cooldown = 0;
    private NormalHandManager normalHandManager; // 添加普通手管理器
    
    public ExtraHandImpl(ExtraHandType handType) {
        this.handType = handType;
        
        // 如果是普通手，初始化管理器
        if (handType == ExtraHandType.NORMAL) {
            this.normalHandManager = new NormalHandManager(this);
        }
    }
    
    @Override
    public ExtraHandType getHandType() {
        return handType;
    }
    
    @Override
    public ItemStack getHeldItem() {
        // 如果是普通手，从管理器获取当前激活的物品
        if (handType == ExtraHandType.NORMAL && normalHandManager != null) {
            return normalHandManager.getActiveItem();
        }
        return heldItem;
    }
    
    @Override
    public void setHeldItem(ItemStack stack) {
        // 如果是普通手，直接设置到第一个槽位（避免插入逻辑的复杂性）
        if (handType == ExtraHandType.NORMAL && normalHandManager != null) {
            // 直接设置到激活槽位，保持原始数量
            ItemStack stackCopy = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
            normalHandManager.getInventory().setStackInSlot(normalHandManager.getActiveSlot(), stackCopy);
        } else {
            // 确保复制时保持原始数量
            this.heldItem = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        }
    }
    
    @Override
    public boolean canHold(ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        
        switch (handType) {
            case NORMAL:
                return true; // 普通手可以拿任何东西
            case COMBAT:
                return stack.getItem() instanceof SwordItem || 
                       stack.getItem() instanceof BowItem || 
                       stack.getItem() instanceof CrossbowItem ||
                       stack.isDamageableItem(); // 可损坏的物品通常是工具/武器
            case INTELLIGENT:
                return stack.getItem() instanceof ShieldItem || stack.getItem() instanceof SwordItem;
            case SUPPORT:
                return true; // 托举手可以拿任何有被动效果的物品
            default:
                return false;
        }
    }
    
    @Override
    public boolean performExtraAttack(LivingEntity attacker, LivingEntity target) {
        if (handType != ExtraHandType.COMBAT || heldItem.isEmpty() || cooldown > 0) {
            return false;
        }
        
        // 如果拿着武器，执行额外攻击
        if (heldItem.getItem() instanceof SwordItem || heldItem.isDamageableItem()) {
            // 模拟使用主手攻击
            if (attacker instanceof Player) {
                // 设置冷却时间
                cooldown = 20; // 1秒冷却
                
                // TODO: 实现额外攻击逻辑
                // 可以在这里添加额外伤害、特殊效果等
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean performAutoDefense(LivingEntity defender, float attackDirection) {
        if (handType != ExtraHandType.INTELLIGENT || heldItem.isEmpty()) {
            return false;
        }
        
        // 如果拿着盾牌，自动防御
        if (heldItem.getItem() instanceof ShieldItem) {
            if (defender instanceof Player player) {
                // 自动举盾
                player.startUsingItem(InteractionHand.OFF_HAND);
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public float getPassiveEffectMultiplier() {
        if (handType == ExtraHandType.SUPPORT && !heldItem.isEmpty()) {
            // 托举手增强被动效果
            return 2.0f; // 双倍效果
        }
        return 1.0f;
    }
    
    @Override
    public void tick(LivingEntity entity) {
        // 减少冷却时间
        if (cooldown > 0) {
            cooldown--;
        }
        
        // 普通手的管理器tick
        if (handType == ExtraHandType.NORMAL && normalHandManager != null) {
            normalHandManager.tick(entity);
        }
        
        // 普通手的基本功能：自动使用消耗品
        if (handType == ExtraHandType.NORMAL && !getHeldItem().isEmpty()) {
            tickNormalHandBehavior(entity);
        }
        
        // 托举手的被动效果
        if (handType == ExtraHandType.SUPPORT && !getHeldItem().isEmpty()) {
            tickSupportHandBehavior(entity);
        }
    }
    
    /**
     * 普通手的tick行为
     */
    private void tickNormalHandBehavior(LivingEntity entity) {
        // 例子：自动吃食物
        if (entity instanceof Player player && player.getFoodData().needsFood()) {
            if (heldItem.isEdible()) {
                // 每5秒检查一次是否需要吃食物
                if (entity.tickCount % 100 == 0) {
                    // TODO: 实现自动进食逻辑
                }
            }
        }
        
        // 例子：自动使用治疗药剂
        if (entity.getHealth() < entity.getMaxHealth() * 0.5f) {
            if (heldItem.getItem() == Items.POTION) {
                // TODO: 检查是否为治疗药剂并自动使用
            }
        }
    }
    
    /**
     * 托举手的tick行为
     */
    private void tickSupportHandBehavior(LivingEntity entity) {
        // 增强持有物品的被动效果
        // TODO: 实现被动效果增强
        // 比如独眼巨人之眼的虚弱效果增强
    }
    
    /**
     * 获取冷却时间
     */
    public int getCooldown() {
        return cooldown;
    }
    
    /**
     * 设置冷却时间
     */
    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }
    
    /**
     * 获取普通手管理器（仅限普通手类型）
     */
    public NormalHandManager getNormalHandManager() {
        return normalHandManager;
    }
    
    /**
     * 切换到下一个物品（仅限普通手）
     */
    public void switchToNextItem() {
        if (handType == ExtraHandType.NORMAL && normalHandManager != null) {
            normalHandManager.switchToNextItem();
        }
    }
}
