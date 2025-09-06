package com.simibubi.create.compat.jei.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.CreateLang;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

@ParametersAreNonnullByDefault
public class SequencedAssemblyCategory extends CreateRecipeCategory<SequencedAssemblyRecipe> {

	Map<ResourceLocation, SequencedAssemblySubCategory> subCategories = new HashMap<>();

	public SequencedAssemblyCategory(Info<SequencedAssemblyRecipe> info) {
		super(info);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, SequencedAssemblyRecipe recipe, IFocusGroup focuses) {
		boolean noRandomOutput = recipe.getOutputChance() == 1;
		int xOffset = noRandomOutput ? 0 : -7;

		builder
				.addSlot(RecipeIngredientRole.INPUT, 27 + xOffset, 91)
				.setBackground(getRenderedSlot(), -1, -1)
				.addItemStacks(List.of(recipe.getIngredient().getItems()));
		builder
				.addSlot(RecipeIngredientRole.OUTPUT, 132 + xOffset, 91)
				.setBackground(getRenderedSlot(recipe.getOutputChance()), -1 , -1)
				.addItemStack(getResultItem(recipe))
				.addTooltipCallback((recipeSlotView, tooltip) -> {
					if (noRandomOutput)
						return;

					float chance = recipe.getOutputChance();
					tooltip.add(1, chanceComponent(chance));
				});

		int width = 0;
		int margin = 3;
		for (SequencedRecipe<?> sequencedRecipe : recipe.getSequence())
			width += getSubCategory(sequencedRecipe).getWidth() + margin;
		width -= margin;
		int x = width / -2 + getBackground().getWidth() / 2;

		for (SequencedRecipe<?> sequencedRecipe : recipe.getSequence()) {
			SequencedAssemblySubCategory subCategory = getSubCategory(sequencedRecipe);
			subCategory.setRecipe(builder, sequencedRecipe, focuses, x);
			x += subCategory.getWidth() + margin;
		}

		for (int i = 1; i < recipe.getLoops(); i++) {
			for (SequencedRecipe<?> sequencedRecipe : recipe.getSequence()) {
				NonNullList<Ingredient> sequencedIngredients = sequencedRecipe.getRecipe()
					.getIngredients();
				for (Ingredient ingredient : sequencedIngredients.subList(1, sequencedIngredients.size()))
					builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
						.addIngredients(ingredient);
				for (FluidIngredient fluidIngredient : sequencedRecipe.getRecipe()
					.getFluidIngredients())
					builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
						.addIngredients(ForgeTypes.FLUID_STACK, fluidIngredient.getMatchingFluidStacks());
			}
		}
	}

	private SequencedAssemblySubCategory getSubCategory(SequencedRecipe<?> sequencedRecipe) {
		return subCategories.computeIfAbsent(CatnipServices.REGISTRIES.getKeyOrThrow(sequencedRecipe.getRecipe()
						.getSerializer()),
			rl -> sequencedRecipe.getAsAssemblyRecipe()
				.getJEISubCategory()
				.get()
				.get());

	}

	final String[] romans = { "I", "II", "III", "IV", "V", "VI", "-" };

	@Override
	public void draw(SequencedAssemblyRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		Font font = Minecraft.getInstance().font;

		PoseStack matrixStack = graphics.pose();
		matrixStack.pushPose();

		matrixStack.pushPose();
		matrixStack.translate(0, 15, 0);
		boolean singleOutput = recipe.getOutputChance() == 1;
		int xOffset = singleOutput ? 0 : -7;
		AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52 + xOffset, 79);
		if (!singleOutput) {
			AllGuiTextures.JEI_CHANCE_SLOT.render(graphics, 150 + xOffset, 75);
            Component component = Component.literal("?").withStyle(ChatFormatting.BOLD);
			graphics.drawString(font, component, font.width(component) / -2 + 8 + 150 + xOffset, 2 + 78,
				0xefefef);
		}

		if (recipe.getLoops() > 1) {
			matrixStack.pushPose();
			matrixStack.translate(15, 9, 0);
			AllIcons.I_SEQ_REPEAT.render(graphics, 50 + xOffset, 75);
            Component repeat = Component.literal("x" + recipe.getLoops());
			graphics.drawString(font, repeat, 66 + xOffset, 80, 0x888888, false);
			matrixStack.popPose();
		}

		matrixStack.popPose();

		int width = 0;
		int margin = 3;
		for (SequencedRecipe<?> sequencedRecipe : recipe.getSequence())
			width += getSubCategory(sequencedRecipe).getWidth() + margin;
		width -= margin;
		matrixStack.translate(width / -2 + getBackground().getWidth() / 2, 0, 0);

		matrixStack.pushPose();
		List<SequencedRecipe<?>> sequence = recipe.getSequence();
		for (int i = 0; i < sequence.size(); i++) {
			SequencedRecipe<?> sequencedRecipe = sequence.get(i);
			SequencedAssemblySubCategory subCategory = getSubCategory(sequencedRecipe);
			int subWidth = subCategory.getWidth();
            MutableComponent component = Component.literal("" + romans[Math.min(i, 6)]);
			graphics.drawString(font, component, font.width(component) / -2 + subWidth / 2, 2, 0x888888, false);
			subCategory.draw(sequencedRecipe, graphics, mouseX, mouseY, i);
			matrixStack.translate(subWidth + margin, 0, 0);
		}
		matrixStack.popPose();

		matrixStack.popPose();
	}

	@Override
	@NotNull
	public List<Component> getTooltipStrings(SequencedAssemblyRecipe recipe, IRecipeSlotsView iRecipeSlotsView, double mouseX, double mouseY) {
		List<Component> tooltip = new ArrayList<>();

		MutableComponent junk = CreateLang.translateDirect("recipe.assembly.junk");

		boolean singleOutput = recipe.getOutputChance() == 1;
		boolean willRepeat = recipe.getLoops() > 1;

		int xOffset = -7;
		int minX = 150 + xOffset;
		int maxX = minX + 18;
		int minY = 90;
		int maxY = minY + 18;
		if (!singleOutput && mouseX >= minX && mouseX < maxX && mouseY >= minY && mouseY < maxY) {
			float chance = recipe.getOutputChance();
			tooltip.add(junk);
			tooltip.add(chanceComponent(1 - chance));
			return tooltip;
		}

		minX = 55 + xOffset;
		maxX = minX + 65;
		minY = 92;
		maxY = minY + 24;
		if (willRepeat && mouseX >= minX && mouseX < maxX && mouseY >= minY && mouseY < maxY) {
			tooltip.add(CreateLang.translateDirect("recipe.assembly.repeat", recipe.getLoops()));
			return tooltip;
		}

		if (mouseY > 5 && mouseY < 84) {
			int width = 0;
			int margin = 3;
			for (SequencedRecipe<?> sequencedRecipe : recipe.getSequence())
				width += getSubCategory(sequencedRecipe).getWidth() + margin;
			width -= margin;
			xOffset = width / 2 + getBackground().getWidth() / -2;

			double relativeX = mouseX + xOffset;
			List<SequencedRecipe<?>> sequence = recipe.getSequence();
			for (int i = 0; i < sequence.size(); i++) {
				SequencedRecipe<?> sequencedRecipe = sequence.get(i);
				SequencedAssemblySubCategory subCategory = getSubCategory(sequencedRecipe);
				if (relativeX >= 0 && relativeX < subCategory.getWidth()) {
					tooltip.add(CreateLang.translateDirect("recipe.assembly.step", i + 1));
					tooltip.add(sequencedRecipe.getAsAssemblyRecipe()
						.getDescriptionForAssembly()
						.plainCopy()
						.withStyle(ChatFormatting.DARK_GREEN));
					return tooltip;
				}
				relativeX -= subCategory.getWidth() + margin;
			}
		}

		return tooltip;
	}

	protected MutableComponent chanceComponent(float chance) {
		String number = chance < 0.01 ? "<1" : chance > 0.99 ? ">99" : String.valueOf(Math.round(chance * 100));
		return CreateLang.translateDirect("recipe.processing.chance", number)
			.withStyle(ChatFormatting.GOLD);
	}
}
