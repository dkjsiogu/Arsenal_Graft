package com.simibubi.create.foundation.data.recipe;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.api.data.recipe.CompactingRecipeGen;
import com.simibubi.create.api.data.recipe.PressingRecipeGen;

import com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

/**
 * Create's own Data Generation for Pressing recipes
 * @see PressingRecipeGen
 */
@SuppressWarnings("unused")
public final class CreatePressingRecipeGen extends PressingRecipeGen {

	GeneratedRecipe

	SUGAR_CANE = create(() -> Items.SUGAR_CANE, b -> b.output(Items.PAPER)),

	PATH = create("path", b -> b.require(Ingredient.of(Items.GRASS_BLOCK, Items.DIRT, Items.COARSE_DIRT, Items.ROOTED_DIRT))
		.output(Items.DIRT_PATH)),

	IRON = create("iron_ingot", b -> b.require(CreateRecipeProvider.I.iron())
		.output(AllItems.IRON_SHEET.get())),
		GOLD = create("gold_ingot", b -> b.require(CreateRecipeProvider.I.gold())
			.output(AllItems.GOLDEN_SHEET.get())),
		COPPER = create("copper_ingot", b -> b.require(CreateRecipeProvider.I.copper())
			.output(AllItems.COPPER_SHEET.get())),
		BRASS = create("brass_ingot", b -> b.require(CreateRecipeProvider.I.brass())
			.output(AllItems.BRASS_SHEET.get())),

	CARDBOARD = create("cardboard", b -> b.require(AllItems.PULP)
		.output(AllItems.CARDBOARD.get())),

	// Atmospheric
	ATM = moddedPaths(Mods.ATM, "crustose"),

	// Better End Forge

	BEF = moddedPaths(Mods.BEF, "amber_moss", "cave_moss", "chorus_nylium", "crystal_moss",
		"end_moss", "end_mycelium", "jungle_moss", "pink_moss", "shadow_grass"),
	// Environmental
	ENV_MYCELIUM = create("compat/environmental/mycelium_path", b -> b.require(Blocks.MYCELIUM)
		.output(Mods.ENV, "mycelium_path")
		.whenModLoaded(Mods.ENV.getId())),

	ENV_PODZOL = create("compat/environmental/podzol_path", b -> b.require(Blocks.PODZOL)
		.output(Mods.ENV, "podzol_path")
		.whenModLoaded(Mods.ENV.getId())),

	// Oh The Biomes You'll Go

	BYG = moddedPaths(Mods.BYG, "lush_grass"),

	//Infernal Expansion
	IX_CRIMSON_PATH = create(Mods.IX.recipeId("crimson_nylium_path"), b -> b.require(Blocks.CRIMSON_NYLIUM)
		.output(Mods.IX, "crimson_nylium_path")
		.whenModLoaded(Mods.IX.getId())),

	IX_WARPED_PATH = create(Mods.IX.recipeId("warped_nylium_path"), b -> b.require(Blocks.WARPED_NYLIUM)
		.output(Mods.IX, "warped_nylium_path")
		.whenModLoaded(Mods.IX.getId())),
		IX_SOUL_PATH = create(Mods.IX.recipeId("soul_soil_path"), b -> b.require(Blocks.SOUL_SOIL)
			.output(Mods.IX, "soul_soil_path")
			.whenModLoaded(Mods.IX.getId())),

	// Aether

	AET_DIRT_PATH = create("aether_dirt_path", b -> b.require(Mods.AET, "aether_dirt")
		.output(Mods.AET, "aether_dirt_path")
		.whenModLoaded(Mods.AET.getId())),

	AET_DIRT_PATH_GRASS = create("aether_dirt_path_from_grass", b -> b.require(Mods.AET, "aether_grass_block")
		.output(Mods.AET, "aether_dirt_path")
		.whenModLoaded(Mods.AET.getId())),

	// Regions Unexplored

	RU_PEAT_PATH = create("peat_dirt_path", b -> b.require(Mods.RU, "peat_dirt")
		.output(Mods.RU, "peat_dirt_path")
		.whenModLoaded(Mods.RU.getId())),

	RU_PEAT_PATH_GRASS = create("peat_dirt_path_from_grass", b -> b.require(Mods.RU, "peat_grass_block")
		.output(Mods.RU, "peat_dirt_path")
		.whenModLoaded(Mods.RU.getId())),

	RU_SILT_PATH = create("silt_dirt_path", b -> b.require(Mods.RU, "silt_dirt")
		.output(Mods.RU, "silt_dirt_path")
		.whenModLoaded(Mods.RU.getId())),

	RU_SILT_PATH_GRASS = create("silt_dirt_path_from_grass", b -> b.require(Mods.RU, "silt_grass_block")
		.output(Mods.RU, "silt_dirt_path")
		.whenModLoaded(Mods.RU.getId())),

	// IE

	IE_PLATES = iePlates("aluminum", "lead", "silver", "nickel", "uranium", "constantan", "electrum", "steel"),

	// Vampirism

	VMP_CURSED_PATH = moddedPaths(Mods.VMP, "cursed_earth"),

	VMP_CURSED_PATH_GRASS = create("cursed_earth_path_from_grass", b -> b.require(Mods.VMP, "cursed_grass")
		.output(Mods.VMP, "cursed_earth_path")
		.whenModLoaded(Mods.VMP.getId()))

	;

	public CreatePressingRecipeGen(PackOutput output) {
		super(output, Create.ID);
	}

	private GeneratedRecipe moddedPaths(Mods mod, String... blocks) {
		for(String block : blocks) {
			moddedCompacting(mod, block, block + "_path");
		}
		return null;
	}

	private GeneratedRecipe iePlates(String... metals) {
		for (String metal : metals)
			create(Mods.IE.recipeId("plate_" + metal), b -> b.require(AllTags.forgeItemTag("ingots/" + metal))
				.output(Mods.IE, "plate_" + metal)
				.whenModLoaded(Mods.IE.getId()));
		return null;
	}

	GeneratedRecipe moddedCompacting(Mods mod, String input, String output) {
		return create("compat/" + mod.getId() + "/" + output, b -> b.require(mod, input)
				.output(mod, output)
				.whenModLoaded(mod.getId()));
	}
}
