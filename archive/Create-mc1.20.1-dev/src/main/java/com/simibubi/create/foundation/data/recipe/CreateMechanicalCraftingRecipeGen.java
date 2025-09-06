package com.simibubi.create.foundation.data.recipe;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.api.data.recipe.CompactingRecipeGen;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeGen;

import com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen;

import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider.I;

import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;

import net.minecraftforge.common.Tags;

/**
 * Create's own Data Generation for Mechanical Crafting recipes
 * @see MechanicalCraftingRecipeGen
 */
@SuppressWarnings("unused")
public final class CreateMechanicalCraftingRecipeGen extends MechanicalCraftingRecipeGen {

	GeneratedRecipe

	CRUSHING_WHEEL = create(AllBlocks.CRUSHING_WHEEL::get).returns(2)
		.recipe(b -> b.key('P', Ingredient.of(ItemTags.PLANKS))
			.key('S', Ingredient.of(I.stone()))
			.key('A', I.andesiteAlloy())
			.patternLine(" AAA ")
			.patternLine("AAPAA")
			.patternLine("APSPA")
			.patternLine("AAPAA")
			.patternLine(" AAA ")
			.disallowMirrored()),

	WAND_OF_SYMMETRY =
		create(AllItems.WAND_OF_SYMMETRY::get).recipe(b -> b.key('E', Ingredient.of(Tags.Items.ENDER_PEARLS))
			.key('G', Ingredient.of(Tags.Items.GLASS))
			.key('P', I.precisionMechanism())
			.key('O', Ingredient.of(Tags.Items.OBSIDIAN))
			.key('B', Ingredient.of(I.brass()))
			.patternLine(" G ")
			.patternLine("GEG")
			.patternLine(" P ")
			.patternLine(" B ")
			.patternLine(" O ")),

	EXTENDO_GRIP = create(AllItems.EXTENDO_GRIP::get).returns(1)
		.recipe(b -> b.key('L', Ingredient.of(I.brass()))
			.key('R', I.precisionMechanism())
			.key('H', AllItems.BRASS_HAND.get())
			.key('S', Ingredient.of(Tags.Items.RODS_WOODEN))
			.patternLine(" L ")
			.patternLine(" R ")
			.patternLine("SSS")
			.patternLine("SSS")
			.patternLine(" H ")
			.disallowMirrored()),

	POTATO_CANNON = create(AllItems.POTATO_CANNON::get).returns(1)
		.recipe(b -> b.key('L', I.andesiteAlloy())
			.key('R', I.precisionMechanism())
			.key('S', AllBlocks.FLUID_PIPE.get())
			.key('C', Ingredient.of(I.copper()))
			.patternLine("LRSSS")
			.patternLine("CC   "))

	;


	public CreateMechanicalCraftingRecipeGen(PackOutput output) {
		super(output, Create.ID);
	}
}
