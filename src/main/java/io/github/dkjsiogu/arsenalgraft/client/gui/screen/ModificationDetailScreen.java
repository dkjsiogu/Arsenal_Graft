package io.github.dkjsiogu.arsenalgraft.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

/**
 * 简单的改造详情占位界面（最小实现以便从主界面跳转）
 */
public class ModificationDetailScreen extends Screen {

	public ModificationDetailScreen() {
		super(Component.translatable("gui.arsenalgraft.modification_detail.title"));
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

