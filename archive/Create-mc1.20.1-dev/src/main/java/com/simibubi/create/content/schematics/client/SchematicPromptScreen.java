package com.simibubi.create.content.schematics.client;

import org.lwjgl.glfw.GLFW;

import com.simibubi.create.AllItems;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SchematicPromptScreen extends AbstractSimiScreen {

	private AllGuiTextures background;

	private final Component convertLabel = CreateLang.translateDirect("schematicAndQuill.convert");
	private final Component abortLabel = CreateLang.translateDirect("action.discard");
	private final Component confirmLabel = CreateLang.translateDirect("action.saveToFile");

	private EditBox nameField;
	private IconButton confirm;
	private IconButton abort;
	private IconButton convert;

	public SchematicPromptScreen() {
		super(CreateLang.translateDirect("schematicAndQuill.title"));
		background = AllGuiTextures.SCHEMATIC_PROMPT;
	}

	@Override
	public void init() {
		setWindowSize(background.getWidth(), background.getHeight());
		super.init();

		int x = guiLeft;
		int y = guiTop + 2;

		nameField = new EditBox(font, x + 49, y + 26, 131, 10, CommonComponents.EMPTY);
		nameField.setTextColor(-1);
		nameField.setTextColorUneditable(-1);
		nameField.setBordered(false);
		nameField.setMaxLength(35);
		nameField.setFocused(true);
		setFocused(nameField);
		addRenderableWidget(nameField);

		abort = new IconButton(x + 7, y + 53, AllIcons.I_TRASH);
		abort.withCallback(() -> {
			CreateClient.SCHEMATIC_AND_QUILL_HANDLER.discard();
			onClose();
		});
		abort.setToolTip(abortLabel);
		addRenderableWidget(abort);

		confirm = new IconButton(x + 158, y + 53, AllIcons.I_CONFIRM);
		confirm.withCallback(() -> {
			confirm(false);
		});
		confirm.setToolTip(confirmLabel);
		addRenderableWidget(confirm);

		convert = new IconButton(x + 180, y + 53, AllIcons.I_SCHEMATIC);
		convert.withCallback(() -> {
			confirm(true);
		});
		convert.setToolTip(convertLabel);
		addRenderableWidget(convert);
	}

	@Override
	protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		int x = guiLeft;
		int y = guiTop;

		background.render(graphics, x, y);
		graphics.drawString(font, title, x + (background.getWidth() - 8 - font.width(title)) / 2, y + 4, 0x505050, false);

		GuiGameElement.of(AllItems.SCHEMATIC.asStack())
			.at(x + 22, y + 24, 0)
			.render(graphics);

		GuiGameElement.of(AllItems.SCHEMATIC_AND_QUILL.asStack())
			.scale(3)
			.at(x + background.getWidth() + 6, y + background.getHeight() - 38, -200)
			.render(graphics);
	}

	@Override
	public boolean keyPressed(int keyCode, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (keyCode == GLFW.GLFW_KEY_ENTER) {
			confirm(false);
			return true;
		}
		if (keyCode == 256 && this.shouldCloseOnEsc()) {
			this.onClose();
			return true;
		}
		return nameField.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_);
	}

	private void confirm(boolean convertImmediately) {
		CreateClient.SCHEMATIC_AND_QUILL_HANDLER.saveSchematic(nameField.getValue(), convertImmediately);
		onClose();
	}
}
