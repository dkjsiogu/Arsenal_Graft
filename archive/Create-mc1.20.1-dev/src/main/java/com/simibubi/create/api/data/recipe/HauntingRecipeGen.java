package com.simibubi.create.api.data.recipe;

import java.util.function.Supplier;

import com.simibubi.create.AllRecipeTypes;

import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/**
 * The base class for Haunting recipe generation.
 * Addons should extend this and use the {@link ProcessingRecipeGen#create} methods
 * or the helper methods contained in this class to make recipes.
 * For an example of how you might do this, see Create's implementation: {@link com.simibubi.create.foundation.data.recipe.CreateHauntingRecipeGen}.
 * Needs to be added to a registered recipe provider to do anything, see {@link com.simibubi.create.foundation.data.recipe.CreateRecipeProvider}
 */
public abstract class HauntingRecipeGen extends ProcessingRecipeGen {

	public GeneratedRecipe convert(ItemLike input, ItemLike result) {
		return convert(() -> Ingredient.of(input), () -> result);
	}

	public GeneratedRecipe convert(Supplier<Ingredient> input, Supplier<ItemLike> result) {
		return create(asResource(CatnipServices.REGISTRIES.getKeyOrThrow(result.get()
								.asItem())
			.getPath()),
			p -> p.withItemIngredients(input.get())
				.output(result.get()));
	}

	public HauntingRecipeGen(PackOutput output, String defaultNamespace) {
		super(output, defaultNamespace);
	}

	@Override
	protected AllRecipeTypes getRecipeType() {
		return AllRecipeTypes.HAUNTING;
	}

}
