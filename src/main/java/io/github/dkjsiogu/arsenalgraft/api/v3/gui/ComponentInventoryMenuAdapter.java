package io.github.dkjsiogu.arsenalgraft.api.v3.gui;

import io.github.dkjsiogu.arsenalgraft.api.v3.component.IInventoryComponent;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.impl.InventoryComponentImpl;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * 组件库存菜单适配器
 * 将IInventoryComponent适配为Minecraft容器菜单系统
 */
public class ComponentInventoryMenuAdapter extends AbstractContainerMenu {
    
    protected final IInventoryComponent inventoryComponent;
    protected final Player player;
    // 直接使用组件暴露的Container或ItemStackHandler (如果可用)
    
    public ComponentInventoryMenuAdapter(MenuType<?> menuType, int containerId, 
                                       Inventory playerInventory, 
                                       IInventoryComponent inventoryComponent) {
        super(menuType, containerId);
        this.inventoryComponent = inventoryComponent;
        this.player = playerInventory.player;

    // 添加组件槽位
        addComponentSlots();

        // 添加玩家库存槽位
        addPlayerInventorySlots(playerInventory);
    }
    
    /**
     * 添加组件槽位
     */
    protected void addComponentSlots() {
        if (inventoryComponent != null && inventoryComponent.getSlotCount() > 0) {
            int slotCount = inventoryComponent.getSlotCount();
            int slotsPerRow = Math.min(slotCount, 9);
            InventoryComponentImpl impl = inventoryComponent instanceof InventoryComponentImpl ic ? ic : null;
            for (int i = 0; i < slotCount; i++) {
                int row = i / slotsPerRow;
                int col = i % slotsPerRow;
                int x = 8 + col * 18;
                int y = 18 + row * 18;
                if (impl != null) {
                    // 使用Forge内置 SlotItemHandler，避免自定义同步错误
                    addSlot(new net.minecraftforge.items.SlotItemHandler(impl.getHandler(), i, x, y));
                } else {
                    addSlot(new ComponentSlot(inventoryComponent, i, x, y));
                }
            }
        }
    }
    
    /**
     * 添加玩家库存槽位
     */
    protected void addPlayerInventorySlots(Inventory playerInventory) {
        // 主要库存（9x3）
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 
                               8 + col * 18, 84 + row * 18));
            }
        }
        
        // 快捷栏（9x1）
        for (int col = 0; col < 9; ++col) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }
    
    @Override
    public boolean stillValid(@Nonnull Player player) {
        return this.player == player;
    }
    
    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull Player player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);
        
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();
            
            int componentSlots = inventoryComponent != null ? inventoryComponent.getSlotCount() : 0;
            
            if (slotIndex < componentSlots) {
                // 从组件槽位移动到玩家库存
                if (!moveItemStackTo(slotStack, componentSlots, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家库存移动到组件槽位
                if (!moveItemStackTo(slotStack, 0, componentSlots, false)) {
                    return ItemStack.EMPTY;
                }
            }
            
            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            
            if (slotStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            
            slot.onTake(player, slotStack);
        }
        
        return itemStack;
    }
    
    /** 组件槽位实现 */
    public static class ComponentSlot extends Slot {
        private final IInventoryComponent component;
        private final int slotIndex;
        public ComponentSlot(IInventoryComponent component, int slotIndex, int x, int y) {
            super(component.getContainer(), slotIndex, x, y);
            this.component = component;
            this.slotIndex = slotIndex;
        }
        @Override public boolean mayPlace(@Nonnull ItemStack stack) { return component.isItemValid(slotIndex, stack); }
        @Override public int getMaxStackSize() { return component.getSlotLimit(slotIndex); }
    }
}
