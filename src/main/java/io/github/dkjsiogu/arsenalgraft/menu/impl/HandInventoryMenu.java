package io.github.dkjsiogu.arsenalgraft.menu.impl;

import io.github.dkjsiogu.arsenalgraft.api.v3.component.IInventoryComponent;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.InstalledSlot;
import io.github.dkjsiogu.arsenalgraft.api.v3.ArsenalGraftAPI;
import io.github.dkjsiogu.arsenalgraft.menu.ArsenalMenus;
import io.github.dkjsiogu.arsenalgraft.inventory.HandInventoryData;
import io.github.dkjsiogu.arsenalgraft.network.HandInventorySyncPacket;
import io.github.dkjsiogu.arsenalgraft.network.NetworkHandler;
import io.github.dkjsiogu.arsenalgraft.client.ClientHandInventoryCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nonnull;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;

import java.util.UUID;

/**
 * 手部改造库存菜单: 复用通用组件菜单适配器
 */
public class HandInventoryMenu extends AbstractContainerMenu {

    private final UUID slotId;
    private final HandInventoryData handler; // 服务端真实 handler / 客户端镜像 handler
    private final int size;
    private final Player player;

    // 服务器端构造
    public HandInventoryMenu(int containerId, Inventory playerInventory, InstalledSlot slot) {
        super(ArsenalMenus.HAND_INVENTORY.get(), containerId);
        this.player = playerInventory.player;
        this.slotId = slot != null ? slot.getSlotId() : new UUID(0,0);
        IInventoryComponent comp = slot != null ? slot.getComponent("inventory", IInventoryComponent.class) : null;
        int count = comp != null ? comp.getSlotCount() : 0;
        this.handler = new HandInventoryData(count);
        this.size = count;
        // 初始快照（服务端）
        if (!player.level().isClientSide && comp != null) {
            for (int i=0;i<count;i++) handler.setStackInSlot(i, comp.getItem(i).copy());
            // 注册监听: 增量同步 (当前仍用全量, TODO: 增量包)
            handler.setChangeListener(slotIndex -> pushFullSync());
            pushFullSync();
        } else if (player.level().isClientSide) {
            // 客户端标记打开, 等待服务器快照
            ClientHandInventoryCache.markOpen(slotId);
        }
        layout(playerInventory);
    }

    // 备用：通过 slotId 在当前玩家数据中查找（客户端读取Buffer时使用 ArsenalMenus 中的工厂）
    public HandInventoryMenu(int containerId, Inventory playerInventory, UUID slotId) {
        this(containerId, playerInventory, findSlot(playerInventory.player, slotId));
    }

    private static InstalledSlot findSlot(Player player, UUID id) {
        if (player == null) return null;
        return ArsenalGraftAPI.getAllModifications(player).stream()
                .filter(s -> s.getSlotId().equals(id))
                .findFirst().orElse(null);
    }

    private void layout(Inventory playerInv) {
        // hand 区域（单行，<=9 时水平居中）
        int baseX;
        if (size > 0) {
            if (size <= 9) {
                int rowWidth = size * 18;
                baseX = (176 - rowWidth) / 2; // 176 屏幕逻辑宽度
            } else {
                baseX = 8; // 太长就左对齐
            }
            for (int i=0;i<size;i++) addSlot(new SlotItemHandler(handler, i, baseX + i*18, 20));
        }
        // 玩家背包: 下移一点让出空间 (原 50 改 54)
    int playerInvY = 84; // 与原版容器对齐, label 在 76 上方 8px 区域
    for (int r=0;r<3;r++) for (int c=0;c<9;c++) addSlot(new Slot(playerInv, c + r*9 + 9, 8 + c*18, playerInvY + r*18));
    for (int c=0;c<9;c++) addSlot(new Slot(playerInv, c, 8 + c*18, playerInvY + 58));
    }

    private void pushFullSync() {
    if (player instanceof ServerPlayer sp) {
            java.util.List<ItemStack> list = new java.util.ArrayList<>();
            for (int i=0;i<size;i++) list.add(handler.getStackInSlot(i).copy());
        NetworkHandler.sendToPlayer(new HandInventorySyncPacket(slotId, list), sp);
        }
    }

    @Override
    public void removed(@Nonnull Player p) {
        super.removed(p);
        if (!p.level().isClientSide) {
            // 写回真实组件
            InstalledSlot slot = findSlot(p, slotId);
            if (slot != null) {
                IInventoryComponent comp = slot.getComponent("inventory", IInventoryComponent.class);
                if (comp != null) {
                    for (int i=0;i<size;i++) {
                        comp.setItem(i, handler.getStackInSlot(i).copy());
                    }
                }
            }
        } else {
            ClientHandInventoryCache.markClosed(slotId);
        }
    }

    @Override public boolean stillValid(@Nonnull Player p) { return p == player; }

    @Override @Nonnull public ItemStack quickMoveStack(@Nonnull Player p, int index) {
        // shift 左键快速转移
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stackInSlot = slot.getItem();
        ItemStack original = stackInSlot.copy();

        int handStart = 0;
        int handEnd = handStart + size; // 不包含 handEnd
        int playerStart = handEnd;
        int playerEnd = playerStart + 27; // 背包 3x9
        int hotbarStart = playerEnd;
        int hotbarEnd = hotbarStart + 9; // 热栏

        if (index < handEnd) {
            // 从 hand -> 玩家 (优先主背包+热栏, 逆向以合并堆叠)
            if (!moveItemStackTo(stackInSlot, playerStart, hotbarEnd, true)) return ItemStack.EMPTY;
        } else {
            // 从玩家 -> hand (正向放入)
            if (!moveItemStackTo(stackInSlot, handStart, handEnd, false)) return ItemStack.EMPTY;
        }

        if (stackInSlot.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();

        if (stackInSlot.getCount() == original.getCount()) return ItemStack.EMPTY; // 没有移动成功

        slot.onTake(p, stackInSlot);
        return original;
    }

    public UUID getSlotId() { return slotId; }

    public int getHandSlotCount() { return size; }
}
