package com.simibubi.create.foundation.gui;

import java.util.function.BiConsumer;

import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.gui.widget.TooltipArea;

import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;

public class ModularGuiLineBuilder {

	private ModularGuiLine target;
	private Font font;
	private int x;
	private int y;

	public ModularGuiLineBuilder(Font font, ModularGuiLine target, int x, int y) {
		this.font = font;
		this.target = target;
		this.x = x;
		this.y = y;
	}

	public ModularGuiLineBuilder addScrollInput(int x, int width, BiConsumer<ScrollInput, Label> inputTransform,
												String dataKey) {
		ScrollInput input = new ScrollInput(x + this.x, y - 4, width, 18);
		addScrollInput(input, inputTransform, dataKey);
		return this;
	}

	public ModularGuiLineBuilder addSelectionScrollInput(int x, int width,
														 BiConsumer<SelectionScrollInput, Label> inputTransform, String dataKey) {
		SelectionScrollInput input = new SelectionScrollInput(x + this.x, y - 4, width, 18);
		addScrollInput(input, inputTransform, dataKey);
		return this;
	}

	public ModularGuiLineBuilder customArea(int x, int width) {
		target.customBoxes.add(Couple.create(x, width));
		return this;
	}

	public ModularGuiLineBuilder speechBubble() {
		target.speechBubble = true;
		return this;
	}

	private <T extends ScrollInput> void addScrollInput(T input, BiConsumer<T, Label> inputTransform, String dataKey) {
		Label label = new Label(input.getX() + 5, y, CommonComponents.EMPTY);
		label.withShadow();
		inputTransform.accept(input, label);
		input.writingTo(label);
		target.add(Pair.of(label, "Dummy"));
		target.add(Pair.of(input, dataKey));
	}

	public ModularGuiLineBuilder addIntegerTextInput(int x, int width, BiConsumer<EditBox, TooltipArea> inputTransform,
													 String dataKey) {
		return addTextInput(x, width, inputTransform.andThen((editBox, $) -> editBox.setFilter(s -> {
			if (s.isEmpty())
				return true;
			try {
				Integer.parseInt(s);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		})), dataKey);
	}

	public ModularGuiLineBuilder addTextInput(int x, int width, BiConsumer<EditBox, TooltipArea> inputTransform,
											  String dataKey) {
		EditBox input = new EditBox(font, x + this.x + 5, y, width - 9, 8, CommonComponents.EMPTY);
		input.setBordered(false);
		input.setTextColor(0xffffff);
		input.setFocused(false);
		input.mouseClicked(0, 0, 0);
		TooltipArea tooltipArea = new TooltipArea(this.x + x, y - 4, width, 18);
		inputTransform.accept(input, tooltipArea);
		target.add(Pair.of(input, dataKey));
		target.add(Pair.of(tooltipArea, "Dummy"));
		return this;
	}

}
