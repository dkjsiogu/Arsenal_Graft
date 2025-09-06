package io.github.dkjsiogu.arsenalgraft.api;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 额外手功能接口
 */
public interface IExtraHand {
    
    /**
     * 获取额外手的类型
     */
    ExtraHandType getHandType();
    
    /**
     * 获取当前持有的物品
     */
    ItemStack getHeldItem();
    
    /**
     * 设置持有的物品
     */
    void setHeldItem(ItemStack stack);
    
    /**
     * 检查是否可以持有指定物品
     */
    boolean canHold(ItemStack stack);
    
    /**
     * 执行额外攻击（用于战斗手）
     * @param attacker 攻击者
     * @param target 目标（可能为null，表示对空气攻击）
     * @return 是否执行了额外攻击
     */
    default boolean performExtraAttack(LivingEntity attacker, LivingEntity target) {
        return false;
    }
    
    /**
     * 执行自动防御（用于智能手）
     * @param defender 防御者
     * @param attackDirection 攻击方向（可以根据这个判断是否需要举盾）
     * @return 是否执行了防御
     */
    default boolean performAutoDefense(LivingEntity defender, float attackDirection) {
        return false;
    }
    
    /**
     * 获取被动效果增强倍数（用于托举手）
     * @return 效果增强倍数，1.0表示正常，2.0表示双倍效果
     */
    default float getPassiveEffectMultiplier() {
        return 1.0f;
    }
    
    /**
     * 每tick调用，用于持续效果
     */
    default void tick(LivingEntity entity) {
        // 默认不做任何事
    }
}
