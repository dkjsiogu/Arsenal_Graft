package io.github.dkjsiogu.arsenalgraft.client.gui.screen;

import io.github.dkjsiogu.arsenalgraft.menu.impl.HandInventoryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import javax.annotation.Nonnull;

/**
 * 手部改造库存容器界面 (标准菜单Screen)
 */
public class HandInventoryMenuScreen extends AbstractContainerScreen<HandInventoryMenu> {

    public HandInventoryMenuScreen(HandInventoryMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = 176;
    this.imageHeight = 184; // 增高以容纳下移的玩家物品栏
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 背景: 浅灰 + 轻微渐变 (顶部稍亮)
        graphics.fillGradient(x, y, x + imageWidth, y + imageHeight, 0xF0252528, 0xF018181C);

        // 绘制 hand 槽位区域的半透明暗框，帮助区分与玩家背包
        int handCount = this.menu.getHandSlotCount();
        if (handCount > 0) {
            // 计算第一个 hand 槽位位置 (匹配菜单 layout 中逻辑)
            // 我们需要遍历 slots 找到 index 0 的 hand 槽位像素 (它在 slots 列表的顺序为0..size-1)
            if (!this.menu.slots.isEmpty()) {
                var first = this.menu.slots.get(0);
                int hx = leftPos + first.x - 4;
                int hy = topPos + first.y - 4;
                int w = handCount * 18 + 8;
                int h = 18 + 8;
                graphics.fillGradient(hx, hy, hx + w, hy + h, 0x40FFFFFF, 0x40101010);
                // 边框
                int borderColor = 0x80FFFFFF;
                graphics.fill(hx, hy, hx + w, hy + 1, borderColor);
                graphics.fill(hx, hy + h - 1, hx + w, hy + h, borderColor);
                graphics.fill(hx, hy, hx + 1, hy + h, borderColor);
                graphics.fill(hx + w - 1, hy, hx + w, hy + h, borderColor);
            }
        }
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 6, 0xFFFFFF, false);
    graphics.drawString(font, playerInventoryTitle, 8, 76, 0xFFFFFF, false);
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
