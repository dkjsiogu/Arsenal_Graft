package io.github.dkjsiogu.arsenalgraft.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

/**
 * 占位的物品栏界面（Inventory branch 用于后续实现）
 */
public class InventoryScreen extends Screen {

    public InventoryScreen() {
        super(Component.translatable("gui.arsenalgraft.inventory.title"));
    }

    @Override
    protected void init() {
        super.init();
        // TODO: 添加物品栏相关组件
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
