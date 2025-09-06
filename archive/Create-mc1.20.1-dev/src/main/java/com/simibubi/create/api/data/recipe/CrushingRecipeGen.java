package com.simibubi.create.api.data.recipe;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;

import com.simibubi.create.foundation.data.recipe.CompatMetals;
import net.createmod.catnip.lang.Lang;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;

/**
 * The base class for Crushing recipe generation.
 * Addons should extend this and use the {@link ProcessingRecipeGen#create} methods
 * or the helper methods contained in this class to make recipes.
 * For an example of how you might do this, see Create's implementation: {@link com.simibubi.create.foundation.data.recipe.CreateCrushingRecipeGen}.
 * Needs to be added to a registered recipe provider to do anything, see {@link com.simibubi.create.foundation.data.recipe.CreateRecipeProvider}
 */
public abstract class CrushingRecipeGen extends ProcessingRecipeGen {

	protected GeneratedRecipe mineralRecycling(AllPaletteStoneTypes type, Supplier<ItemLike> crushed,
																		  Supplier<ItemLike> nugget, float chance) {
		return mineralRecycling(type, b -> b.duration(250)
			.output(chance, crushed.get(), 1)
			.output(chance, nugget.get(), 1));
	}

	protected GeneratedRecipe mineralRecycling(AllPaletteStoneTypes type,
																		  UnaryOperator<ProcessingRecipeBuilder<ProcessingRecipe<?>>> transform) {
		create(Lang.asId(type.name()) + "_recycling", b -> transform.apply(b.require(type.materialTag)));
		return create(type.getBaseBlock()::get, transform);
	}

	protected GeneratedRecipe stoneOre(Supplier<ItemLike> ore, Supplier<ItemLike> raw, float expectedAmount,
																  int duration) {
		return ore(Blocks.COBBLESTONE, ore, raw, expectedAmount, duration);
	}

	protected GeneratedRecipe deepslateOre(Supplier<ItemLike> ore, Supplier<ItemLike> raw, float expectedAmount,
																	  int duration) {
		return ore(Blocks.COBBLED_DEEPSLATE, ore, raw, expectedAmount, duration);
	}

	protected GeneratedRecipe netherOre(Supplier<ItemLike> ore, Supplier<ItemLike> raw, float expectedAmount,
																   int duration) {
		return ore(Blocks.NETHERRACK, ore, raw, expectedAmount, duration);
	}

	protected GeneratedRecipe ore(ItemLike stoneType, Supplier<ItemLike> ore, Supplier<ItemLike> raw,
								  float expectedAmount, int duration) {
		return create(ore, b -> {
			b.duration(duration)
				.output(raw.get(), Mth.floor(expectedAmount));
			float extra = expectedAmount - Mth.floor(expectedAmount);
			if (extra > 0)
				b.output(extra, raw.get(), 1);
			b.output(.75f, AllItems.EXP_NUGGET.get(), raw.get() == AllItems.CRUSHED_GOLD.get() ? 2 : 1);
			return b.output(.125f, stoneType);
		});
	}

	protected GeneratedRecipe moddedOre(CompatMetals metal, Supplier<ItemLike> result) {
		String name = metal.getName();
		return create(name + "_ore", b -> {
			String prefix = "ores/";
			return b.duration(400)
				.withCondition(new NotCondition(new TagEmptyCondition("forge", prefix + name)))
				.require(AllTags.forgeItemTag(prefix + name))
				.output(result.get(), 1)
				.output(.75f, result.get(), 1)
				.output(.75f, AllItems.EXP_NUGGET.get());
		});
	}

	protected GeneratedRecipe rawOre(String metalName, Supplier<TagKey<Item>> input, Supplier<ItemLike> result, int xpMult) {
		return rawOre(metalName, input, result, false, xpMult);
	}

	protected GeneratedRecipe rawOreBlock(String metalName, Supplier<TagKey<Item>> input, Supplier<ItemLike> result, int xpMult) {
		return rawOre(metalName, input, result, true, xpMult);
	}

	protected GeneratedRecipe rawOre(String metalName, Supplier<TagKey<Item>> input, Supplier<ItemLike> result, boolean block, int xpMult) {
		return create("raw_" + metalName + (block ? "_block" : ""), b -> {
			int amount = block ? 9 : 1;
			return b.duration(400)
				.require(input.get())
				.output(result.get(), amount)
				.output(.75f, AllItems.EXP_NUGGET.get(), amount * xpMult);
		});
	}

	protected GeneratedRecipe moddedRawOre(CompatMetals metal, Supplier<ItemLike> result) {
		return moddedRawOre(metal, result, false);
	}

	protected GeneratedRecipe moddedRawOreBlock(CompatMetals metal, Supplier<ItemLike> result) {
		return moddedRawOre(metal, result, true);
	}

	protected GeneratedRecipe moddedRawOre(CompatMetals metal, Supplier<ItemLike> result, boolean block) {
		String name = metal.getName();
		return create("raw_" + name + (block ? "_block" : ""), b -> {
			int amount = block ? 9 : 1;
			String tagPath = (block ? "storage_blocks/raw_" : "raw_materials/") + name;
			return b.duration(400)
				.withCondition(new NotCondition(new TagEmptyCondition("forge", tagPath)))
				.require(AllTags.forgeItemTag(tagPath))
				.output(result.get(), amount)
				.output(.75f, AllItems.EXP_NUGGET.get(), amount);
		});
	}

	public CrushingRecipeGen(PackOutput generator, String defaultNamespace) {
		super(generator, defaultNamespace);
	}

	@Override
	protected AllRecipeTypes getRecipeType() {
		return AllRecipeTypes.CRUSHING;
	}

}
