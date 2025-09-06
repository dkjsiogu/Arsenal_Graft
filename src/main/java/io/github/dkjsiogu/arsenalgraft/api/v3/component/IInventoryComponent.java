package io.github.dkjsiogu.arsenalgraft.api.v3.component;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * 物品栏组件接口
 * 
 * 提供统一的物品栏访问方式。
 * 任何改造插槽只要实现了这个组件，就可以拥有物品栏功能。
 * 
 * 设计原则：
 * - 不存在"手改造"或"脚改造"的特殊代码
 * - 只要加载了物品栏组件，插槽就能拿东西
 * - 现有的TypeFilteredArsenalScreen可以直接与此接口交互
 */
public interface IInventoryComponent extends IModificationComponent {
    
    /**
     * 组件类型常量
     */
    String COMPONENT_TYPE = "inventory";
    
    @Override
    default String getComponentType() {
        return COMPONENT_TYPE;
    }
    
    /**
     * 获取底层的Container实例
     * 这允许现有的Menu系统直接与组件交互
     * 
     * @return Container实例
     */
    Container getContainer();
    
    /**
     * 获取物品栏大小
     * 
     * @return 插槽数量
     */
    int getContainerSize();
    
    /**
     * 获取指定插槽的物品
     * 
     * @param slot 插槽索引
     * @return 物品堆叠
     */
    ItemStack getItem(int slot);
    
    /**
     * 设置指定插槽的物品
     * 
     * @param slot 插槽索引
     * @param stack 物品堆叠
     */
    void setItem(int slot, ItemStack stack);
    
    /**
     * 移除指定插槽的物品
     * 
     * @param slot 插槽索引
     * @param count 移除数量
     * @return 被移除的物品
     */
    default ItemStack removeItem(int slot, int count) {
        ItemStack itemstack = getItem(slot);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else if (itemstack.getCount() <= count) {
            setItem(slot, ItemStack.EMPTY);
            return itemstack;
        } else {
            ItemStack itemstack1 = itemstack.split(count);
            if (itemstack.isEmpty()) {
                setItem(slot, ItemStack.EMPTY);
            }
            return itemstack1;
        }
    }
    
    /**
     * 移除指定插槽的所有物品
     * 
     * @param slot 插槽索引
     * @return 被移除的物品
     */
    default ItemStack removeItemNoUpdate(int slot) {
        ItemStack itemstack = getItem(slot);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            setItem(slot, ItemStack.EMPTY);
            return itemstack;
        }
    }
    
    /**
     * 检查物品是否可以放入指定插槽
     * 
     * @param slot 插槽索引
     * @param stack 要放入的物品
     * @return 是否可以放入
     */
    default boolean canPlaceItem(int slot, ItemStack stack) {
        return true;
    }
    
    /**
     * 检查物品栏是否为空
     * 
     * @return 是否为空
     */
    default boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); i++) {
            if (!getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 清空物品栏
     */
    default void clearContent() {
        for (int i = 0; i < getContainerSize(); i++) {
            setItem(i, ItemStack.EMPTY);
        }
    }
    
    /**
     * 标记物品栏已更改
     */
    default void setChanged() {
        // 默认实现为空，子类可以重写
    }
    
    /**
     * 检查玩家是否可以使用此物品栏
     * 
     * @param player 玩家
     * @return 是否可以使用
     */
    default boolean stillValid(Player player) {
        return true; // 默认总是可用，子类可以重写实现距离检查等逻辑
    }
    
    /**
     * 获取槽位类型 (用于GUI过滤)
     */
    String getSlotType();
    
    /**
     * 设置槽位类型
     */
    void setSlotType(String slotType);
    
    /**
     * 检查槽位是否可以从外部提取
     */
    boolean canExtract(int slot);
    
    /**
     * 检查槽位是否可以从外部插入
     */
    boolean canInsert(int slot);
    
    /**
     * 获取槽位堆叠上限
     */
    int getSlotLimit(int slot);
    
    /**
     * 查找第一个匹配的物品插槽
     * 
     * @param stack 要查找的物品
     * @return 插槽索引，如果没找到返回-1
     */
    default int findSlotMatchingItem(ItemStack stack) {
        for (int i = 0; i < getContainerSize(); i++) {
            ItemStack slotStack = getItem(i);
            if (!slotStack.isEmpty() && ItemStack.isSameItemSameTags(slotStack, stack)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * 查找第一个空插槽
     * 
     * @return 插槽索引，如果没找到返回-1
     */
    default int findFirstEmptySlot() {
        for (int i = 0; i < getContainerSize(); i++) {
            if (getItem(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * 尝试添加物品到物品栏
     * 
     * @param stack 要添加的物品
     * @return 剩余的物品（如果物品栏满了）
     */
    default ItemStack addItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        
        ItemStack remaining = stack.copy();
        
        // 首先尝试添加到现有堆叠
        for (int i = 0; i < getContainerSize() && !remaining.isEmpty(); i++) {
            ItemStack slotStack = getItem(i);
            if (!slotStack.isEmpty() && ItemStack.isSameItemSameTags(slotStack, remaining)) {
                int maxStack = Math.min(slotStack.getMaxStackSize(), getSlotLimit(i));
                int canAdd = maxStack - slotStack.getCount();
                if (canAdd > 0) {
                    int toAdd = Math.min(canAdd, remaining.getCount());
                    slotStack.grow(toAdd);
                    remaining.shrink(toAdd);
                    setChanged();
                }
            }
        }
        
        // 然后尝试添加到空插槽
        for (int i = 0; i < getContainerSize() && !remaining.isEmpty(); i++) {
            if (getItem(i).isEmpty() && canPlaceItem(i, remaining)) {
                int maxStack = Math.min(remaining.getMaxStackSize(), getSlotLimit(i));
                int toAdd = Math.min(maxStack, remaining.getCount());
                
                ItemStack newStack = remaining.copy();
                newStack.setCount(toAdd);
                setItem(i, newStack);
                remaining.shrink(toAdd);
                setChanged();
            }
        }
        
        return remaining.isEmpty() ? ItemStack.EMPTY : remaining;
    }
    
    /**
     * 兼容性方法：获取槽位数量（委托给getContainerSize）
     */
    default int getSlotCount() {
        return getContainerSize();
    }
    
    /**
     * 兼容性方法：检查物品是否有效（委托给canPlaceItem）
     */
    default boolean isItemValid(int slot, ItemStack stack) {
        return canPlaceItem(slot, stack);
    }
}
