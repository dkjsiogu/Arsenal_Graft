package io.github.dkjsiogu.arsenalgraft.api.v3.component.impl;

import io.github.dkjsiogu.arsenalgraft.ArsenalGraft;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.IInventoryComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

/**
 * 物品栏组件的默认实现
 * 
 * 基于现有的ArsenalInventoryImpl逻辑，但重构为组件形式。
 */
public class InventoryComponentImpl implements IInventoryComponent {
    
    private final ItemStackHandler handler;
    private final Container container;
    private String slotType = "default";
    private boolean active = true;
    
    public InventoryComponentImpl(int slotCount) {
        this.handler = new ItemStackHandler(slotCount);
        // 创建一个简单的容器来包装ItemStackHandler
        this.container = new SimpleContainer(slotCount) {
            @Override
            public ItemStack getItem(int slot) {
                return handler.getStackInSlot(slot);
            }
            
            @Override
            public void setItem(int slot, @Nonnull ItemStack stack) {
                handler.setStackInSlot(slot, stack);
            }
            
            @Override
            public int getContainerSize() {
                return handler.getSlots();
            }
            
            @Override
            public boolean isEmpty() {
                for (int i = 0; i < handler.getSlots(); i++) {
                    if (!handler.getStackInSlot(i).isEmpty()) {
                        return false;
                    }
                }
                return true;
            }
        };
    }
    
    public InventoryComponentImpl(int slotCount, String slotType) {
        this(slotCount);
        this.slotType = slotType;
    }
    
    @Override
    public String getComponentType() {
        return COMPONENT_TYPE;
    }
    
    @Override
    public boolean isActive() {
        return active;
    }
    
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public void onInstall(Player player) {
        setActive(true);
        ArsenalGraft.LOGGER.info("[InventoryComponent] 物品栏组件已安装，玩家: {}, 槽位数量: {}", 
                               player.getName().getString(), getSlotCount());
    }
    
    @Override
    public void onUninstall(Player player) {
        try {
            // 清空物品栏并掉落物品
            for (int i = 0; i < getSlotCount(); i++) {
                ItemStack stack = getItem(i);
                if (!stack.isEmpty()) {
                    player.drop(stack, false);
                    setItem(i, ItemStack.EMPTY);
                }
            }
            setActive(false);
            ArsenalGraft.LOGGER.info("[InventoryComponent] 物品栏组件已卸载，玩家: {}", 
                                   player.getName().getString());
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("[InventoryComponent] 卸载组件时发生错误，玩家: {}", 
                                    player.getName().getString(), e);
        }
    }
    
    @Override
    public Container getContainer() {
        return container;
    }
    
    @Override
    public int getContainerSize() {
        return handler.getSlots();
    }
    
    @Override
    public int getSlotCount() {
        return handler.getSlots();
    }
    
    @Override
    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= getSlotCount()) {
            ArsenalGraft.LOGGER.warn("[InventoryComponent] 尝试访问无效槽位: {}, 有效范围: 0-{}", 
                                   slot, getSlotCount() - 1);
            return ItemStack.EMPTY;
        }
        try {
            return handler.getStackInSlot(slot);
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("[InventoryComponent] 获取物品时发生错误，槽位: {}", slot, e);
            return ItemStack.EMPTY;
        }
    }
    
    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= getSlotCount()) {
            ArsenalGraft.LOGGER.warn("[InventoryComponent] 尝试设置无效槽位: {}, 有效范围: 0-{}", 
                                   slot, getSlotCount() - 1);
            return;
        }
        if (stack == null) {
            ArsenalGraft.LOGGER.warn("[InventoryComponent] 尝试设置null物品到槽位: {}", slot);
            stack = ItemStack.EMPTY;
        }
        try {
            handler.setStackInSlot(slot, stack);
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("[InventoryComponent] 设置物品时发生错误，槽位: {}", slot, e);
        }
    }
    
    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return handler.isItemValid(slot, stack);
    }
    
    @Override
    public String getSlotType() {
        return slotType;
    }
    
    @Override
    public void setSlotType(String slotType) {
        this.slotType = slotType;
    }
    
    @Override
    public boolean canExtract(int slot) {
        return true; // 默认允许提取
    }
    
    @Override
    public boolean canInsert(int slot) {
        return true; // 默认允许插入
    }
    
    @Override
    public int getSlotLimit(int slot) {
        return handler.getSlotLimit(slot);
    }
    
    @Override
    public IInventoryComponent copy() {
        InventoryComponentImpl copy = new InventoryComponentImpl(getSlotCount(), slotType);
        copy.active = this.active;
        
        // 复制物品（创建新的ItemStack实例）
        for (int i = 0; i < getSlotCount(); i++) {
            ItemStack stack = getItem(i);
            if (!stack.isEmpty()) {
                copy.setItem(i, stack.copy());
            }
        }
        
        return copy;
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("inventory", handler.serializeNBT());
        tag.putString("slotType", slotType);
        tag.putBoolean("active", active);
        return tag;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("inventory")) {
            handler.deserializeNBT(nbt.getCompound("inventory"));
        }
        
        this.slotType = nbt.getString("slotType");
        this.active = nbt.getBoolean("active");
    }
    
    /**
     * 获取底层的ItemStackHandler（用于兼容现有代码）
     */
    public ItemStackHandler getHandler() {
        return handler;
    }
}
