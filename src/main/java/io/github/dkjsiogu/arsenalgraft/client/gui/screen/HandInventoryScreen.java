package io.github.dkjsiogu.arsenalgraft.client.gui.screen;

import io.github.dkjsiogu.arsenalgraft.api.v3.modification.InstalledSlot;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.impl.InventoryComponentImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * 展示单个 hand 改造的库存（上方一行 / 若组件多槽则全展示）+ 玩家背包。
 * 这是一个最小功能 GUI，不含拖拽逻辑（只显示）。后续可替换为 Container 菜单。
 */
public class HandInventoryScreen extends Screen {

    private final Player player;
    private final InstalledSlot slot;
    private InventoryComponentImpl invComponent; // 如果存在 inventory 组件

    private static final int SLOT_SIZE = 18;

    public HandInventoryScreen(Player player, InstalledSlot slot) {
        super(Component.literal("手部库存"));
        this.player = player;
        this.slot = slot;
    }

    @Override
    protected void init() {
        super.init();
        // 获取组件
        invComponent = slot.getComponent("inventory", InventoryComponentImpl.class);
        addRenderableWidget(Button.builder(Component.literal("返回"), b -> Minecraft.getInstance().setScreen(new HandModificationScreen(player)))
            .bounds(10, 10, 50, 20).build());
    }

    @Override
    public void render(@Nonnull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        g.drawCenteredString(this.font, this.title, this.width / 2, 25, 0xFFFFFF);
        super.render(g, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int topY = 60;

        if (invComponent != null) {
            int slots = invComponent.getSlotCount();
            int totalWidth = slots * SLOT_SIZE;
            int startX = centerX - totalWidth / 2;
            for (int i = 0; i < slots; i++) {
                int x = startX + i * SLOT_SIZE;
                int y = topY;
                // 槽背景
                g.fill(x, y, x + 16, y + 16, 0xFF444444);
                ItemStack stack = invComponent.getItem(i);
                if (!stack.isEmpty()) {
                    g.renderItem(stack, x + 1, y + 1);
                    g.renderItemDecorations(this.font, stack, x + 1, y + 1);
                }
            }
        } else {
            g.drawCenteredString(this.font, Component.literal("此改造没有库存组件"), centerX, topY, 0xFFAAAAAA);
        }

        // 玩家背包简单显示（前 9 个快捷栏）
        int hotbarY = topY + 40;
        int hotbarStartX = centerX - (9 * SLOT_SIZE) / 2;
        for (int i = 0; i < 9; i++) {
            int x = hotbarStartX + i * SLOT_SIZE;
            int y = hotbarY;
            g.fill(x, y, x + 16, y + 16, 0xFF222222);
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                g.renderItem(stack, x + 1, y + 1);
                g.renderItemDecorations(this.font, stack, x + 1, y + 1);
            }
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
