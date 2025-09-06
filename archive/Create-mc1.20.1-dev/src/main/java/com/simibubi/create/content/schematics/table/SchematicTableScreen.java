package com.simibubi.create.content.schematics.table;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;
import static com.simibubi.create.foundation.gui.AllGuiTextures.SCHEMATIC_TABLE_PROGRESS;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.schematics.client.ClientSchematicLoader;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.CreatePaths;

import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class SchematicTableScreen extends AbstractSimiContainerScreen<SchematicTableMenu> {

	private final Component uploading = CreateLang.translateDirect("gui.schematicTable.uploading");
	private final Component finished = CreateLang.translateDirect("gui.schematicTable.finished");
	private final Component refresh = CreateLang.translateDirect("gui.schematicTable.refresh");
	private final Component folder = CreateLang.translateDirect("gui.schematicTable.open_folder");
	private final Component noSchematics = CreateLang.translateDirect("gui.schematicTable.noSchematics");
	private final Component availableSchematicsTitle = CreateLang.translateDirect("gui.schematicTable.availableSchematics");

	protected AllGuiTextures background;

	private ScrollInput schematicsArea;
	private IconButton confirmButton;
	private IconButton folderButton;
	private IconButton refreshButton;
	private Label schematicsLabel;

	private float progress;
	private float chasingProgress;
	private float lastChasingProgress;

	private final ItemStack renderedItem = AllBlocks.SCHEMATIC_TABLE.asStack();

	private List<Rect2i> extraAreas = Collections.emptyList();

	public SchematicTableScreen(SchematicTableMenu menu, Inventory playerInventory,
								Component title) {
		super(menu, playerInventory, title);
		background = AllGuiTextures.SCHEMATIC_TABLE;
	}

	@Override
	protected void init() {
		setWindowSize(background.getWidth(), background.getHeight() + 4 + AllGuiTextures.PLAYER_INVENTORY.getHeight());
		setWindowOffset(-11, 8);
		super.init();

		CreateClient.SCHEMATIC_SENDER.refresh();
		List<Component> availableSchematics = CreateClient.SCHEMATIC_SENDER.getAvailableSchematics();

		int x = leftPos;
		int y = topPos + 2;

		schematicsLabel = new Label(x + 51, y + 26, CommonComponents.EMPTY).withShadow();
		schematicsLabel.text = CommonComponents.EMPTY;
		if (!availableSchematics.isEmpty()) {
			schematicsArea =
				new SelectionScrollInput(x + 45, y + 21, 139, 18).forOptions(availableSchematics)
					.titled(availableSchematicsTitle.plainCopy())
					.writingTo(schematicsLabel);
			addRenderableWidget(schematicsArea);
			addRenderableWidget(schematicsLabel);
		}

		confirmButton = new IconButton(x + 44, y + 56, AllIcons.I_CONFIRM);
		confirmButton.withCallback(() -> {
			if (menu.canWrite() && schematicsArea != null) {
				ClientSchematicLoader schematicSender = CreateClient.SCHEMATIC_SENDER;
				lastChasingProgress = chasingProgress = progress = 0;
				List<Component> availableSchematics1 = schematicSender.getAvailableSchematics();
				Component schematic = availableSchematics1.get(schematicsArea.getState());
				schematicSender.startNewUpload(schematic.getString());
			}
		});

		folderButton = new IconButton(x + 20, y + 21, AllIcons.I_OPEN_FOLDER);
		folderButton.withCallback(() -> {
			Util.getPlatform().openFile(CreatePaths.SCHEMATICS_DIR.toFile());
		});
		folderButton.setToolTip(folder);
		refreshButton = new IconButton(x + 206, y + 21, AllIcons.I_REFRESH);
		refreshButton.withCallback(() -> {
			ClientSchematicLoader schematicSender = CreateClient.SCHEMATIC_SENDER;
			schematicSender.refresh();
			List<Component> availableSchematics1 = schematicSender.getAvailableSchematics();
			removeWidget(schematicsArea);

			if (!availableSchematics1.isEmpty()) {
				schematicsArea = new SelectionScrollInput(leftPos + 45, topPos + 21, 139, 18)
					.forOptions(availableSchematics1)
					.titled(availableSchematicsTitle.plainCopy())
					.writingTo(schematicsLabel);
				schematicsArea.onChanged();
				addRenderableWidget(schematicsArea);
			} else {
				schematicsArea = null;
				schematicsLabel.text = CommonComponents.EMPTY;
			}
		});
		refreshButton.setToolTip(refresh);

		addRenderableWidget(confirmButton);
		addRenderableWidget(folderButton);
		addRenderableWidget(refreshButton);

		extraAreas = ImmutableList.of(
			new Rect2i(x + background.getWidth(), y + background.getHeight() - 40, 48, 48),
			new Rect2i(refreshButton.getX(), refreshButton.getY(), refreshButton.getWidth(), refreshButton.getHeight())
		);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		int invX = getLeftOfCentered(PLAYER_INVENTORY.getWidth());
		int invY = topPos + background.getHeight() + 4;
		renderPlayerInventory(graphics, invX, invY);

		int x = leftPos;
		int y = topPos;

		background.render(graphics, x, y);

		Component titleText;
		if (menu.contentHolder.isUploading)
			titleText = uploading;
		else if (menu.getSlot(1)
			.hasItem())
			titleText = finished;
		else
			titleText = title;

		graphics.drawString(font, titleText, x + (background.getWidth() - 8 - font.width(titleText)) / 2, y + 4, 0x505050, false);

		if (schematicsArea == null)
			graphics.drawString(font, noSchematics, x + 54, y + 26, 0xD3D3D3);

		GuiGameElement.of(renderedItem)
			.<GuiGameElement.GuiRenderBuilder>at(x + background.getWidth(), y + background.getHeight() - 40, -200)
			.scale(3)
			.render(graphics);

		int width = (int) (SCHEMATIC_TABLE_PROGRESS.getWidth()
			* Mth.lerp(partialTicks, lastChasingProgress, chasingProgress));
		int height = SCHEMATIC_TABLE_PROGRESS.getHeight();
		graphics.blit(SCHEMATIC_TABLE_PROGRESS.location, x + 70, y + 59, SCHEMATIC_TABLE_PROGRESS.getStartX(),
			SCHEMATIC_TABLE_PROGRESS.getStartY(), width, height);
	}

	@Override
	protected void containerTick() {
		super.containerTick();

		boolean finished = menu.getSlot(1)
			.hasItem();

		if (menu.contentHolder.isUploading || finished) {
			if (finished) {
				chasingProgress = lastChasingProgress = progress = 1;
			} else {
				lastChasingProgress = chasingProgress;
				progress = menu.contentHolder.uploadingProgress;
				chasingProgress += (progress - chasingProgress) * .5f;
			}
			confirmButton.active = false;

			if (schematicsLabel != null) {
				schematicsLabel.colored(0xCCDDFF);
				String uploadingSchematic = menu.contentHolder.uploadingSchematic;
				if (uploadingSchematic == null) {
					schematicsLabel.text = null;
				} else {
					schematicsLabel.text = Component.literal(uploadingSchematic);
				}
			}
			if (schematicsArea != null)
				schematicsArea.visible = false;

		} else {
			progress = 0;
			chasingProgress = lastChasingProgress = 0;
			confirmButton.active = true;

			if (schematicsLabel != null)
				schematicsLabel.colored(0xFFFFFF);
			if (schematicsArea != null) {
				schematicsArea.writingTo(schematicsLabel);
				schematicsArea.visible = true;
			}
		}
	}

	@Override
	public List<Rect2i> getExtraAreas() {
		return extraAreas;
	}

}
