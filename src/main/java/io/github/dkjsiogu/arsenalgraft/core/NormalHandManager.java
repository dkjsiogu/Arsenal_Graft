package io.github.dkjsiogu.arsenalgraft.core;

import io.github.dkjsiogu.arsenalgraft.api.IExtraHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.items.ItemStackHandler;

/**
 * 普通手管理器 - 专门处理普通手的功能实现
 * 支持3x3物品栏、快速切换、自动使用等高级功能
 */
public class NormalHandManager {
    
    private final ItemStackHandler inventory;
    private final IExtraHand associatedHand;
    private int activeSlot = 0; // 当前激活的槽位
    private int autoUseTimer = 0;
    
    public NormalHandManager(IExtraHand hand) {
        this.associatedHand = hand;
        this.inventory = new ItemStackHandler(9) { // 3x3 = 9槽位
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                updateActiveItem();
            }
        };
    }
    
    /**
     * 获取当前激活的物品
     */
    public ItemStack getActiveItem() {
        return inventory.getStackInSlot(activeSlot);
    }
    
    /**
     * 获取当前激活的槽位索引
     */
    public int getActiveSlot() {
        return activeSlot;
    }
    
    /**
     * 设置激活槽位
     */
    public void setActiveSlot(int slot) {
        if (slot >= 0 && slot < 9) {
            this.activeSlot = slot;
            updateActiveItem();
        }
    }
    
    /**
     * 获取完整的物品栏
     */
    public ItemStackHandler getInventory() {
        return inventory;
    }
    
    /**
     * 快速切换到下一个非空槽位
     */
    public void switchToNextItem() {
        for (int i = 1; i <= 9; i++) {
            int nextSlot = (activeSlot + i) % 9;
            if (!inventory.getStackInSlot(nextSlot).isEmpty()) {
                setActiveSlot(nextSlot);
                return;
            }
        }
        // 如果没有其他物品，保持当前槽位
    }
    
    /**
     * 添加物品到库存
     */
    public ItemStack insertItem(ItemStack stack) {
        ItemStack remaining = stack.copy();
        
        // 首先尝试与现有物品堆叠
        for (int i = 0; i < 9; i++) {
            remaining = inventory.insertItem(i, remaining, false);
            if (remaining.isEmpty()) {
                break;
            }
        }
        
        return remaining;
    }
    
    /**
     * 自动使用物品逻辑
     */
    public void tick(LivingEntity entity) {
        autoUseTimer++;
        
        if (autoUseTimer % 20 == 0) { // 每秒检查一次
            autoUseLogic(entity);
        }
        
        // 更新关联手的物品
        updateActiveItem();
    }
    
    /**
     * 自动使用逻辑
     */
    private void autoUseLogic(LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        
        ItemStack activeItem = getActiveItem();
        if (activeItem.isEmpty()) return;
        
        // 自动吃食物
        if (player.getFoodData().needsFood() && activeItem.isEdible()) {
            // 实现自动进食
            autoEatFood(player, activeItem);
        }
        
        // 自动使用治疗药剂
        if (player.getHealth() < player.getMaxHealth() * 0.5f) {
            if (activeItem.getItem() == Items.POTION) {
                // TODO: 检查药剂类型并使用
                consumeActiveItem();
            }
        }
    }
    
    /**
     * 自动进食逻辑
     */
    private void autoEatFood(Player player, ItemStack foodItem) {
        if (!foodItem.isEdible() || !player.getFoodData().needsFood()) {
            return;
        }
        
        // 检查玩家是否可以吃这个食物
        if (player.canEat(foodItem.getFoodProperties(player).canAlwaysEat())) {
            // 模拟玩家右键使用食物
            ItemStack result = foodItem.finishUsingItem(player.level(), player);
            
            // 应用食物效果
            if (foodItem.getFoodProperties(player) != null) {
                var foodProperties = foodItem.getFoodProperties(player);
                player.getFoodData().eat(foodProperties.getNutrition(), foodProperties.getSaturationModifier());
                
                // 应用食物的状态效果
                foodProperties.getEffects().forEach(pair -> {
                    if (player.level().random.nextFloat() < pair.getSecond()) {
                        player.addEffect(pair.getFirst());
                    }
                });
            }
            
            // 消耗物品
            consumeActiveItem();
        }
    }
    
    /**
     * 消耗当前激活的物品
     */
    private void consumeActiveItem() {
        ItemStack current = inventory.getStackInSlot(activeSlot);
        if (!current.isEmpty()) {
            current.shrink(1);
            inventory.setStackInSlot(activeSlot, current.isEmpty() ? ItemStack.EMPTY : current);
        }
    }
    
    /**
     * 更新关联手的物品显示
     * 注意：不直接调用setHeldItem以避免无限递归
     */
    private void updateActiveItem() {
        // 不需要更新关联手，因为getHeldItem()会直接从这里获取
        // 移除：associatedHand.setHeldItem(getActiveItem());
        // 避免无限递归：setHeldItem -> insertItem -> onContentsChanged -> updateActiveItem -> setHeldItem
    }
    
    /**
     * 序列化到NBT
     */
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("inventory", inventory.serializeNBT());
        tag.putInt("activeSlot", activeSlot);
        return tag;
    }
    
    /**
     * 从NBT反序列化
     */
    public void deserializeNBT(CompoundTag tag) {
        inventory.deserializeNBT(tag.getCompound("inventory"));
        activeSlot = tag.getInt("activeSlot");
        updateActiveItem();
    }
    
    /**
     * 检查是否可以持有指定物品
     */
    public boolean canHoldItem(ItemStack stack) {
        if (stack.isEmpty()) return true;
        
        // 普通手可以持有任何物品
        return true;
    }
    
    /**
     * 获取空槽位数量
     */
    public int getEmptySlots() {
        int count = 0;
        for (int i = 0; i < 9; i++) {
            if (inventory.getStackInSlot(i).isEmpty()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 清空所有物品
     */
    public void clearInventory() {
        for (int i = 0; i < 9; i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
        activeSlot = 0;
        updateActiveItem();
    }
}
