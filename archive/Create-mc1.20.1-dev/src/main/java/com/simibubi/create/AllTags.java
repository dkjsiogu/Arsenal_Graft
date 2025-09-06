package com.simibubi.create;

import static com.simibubi.create.AllTags.NameSpace.CURIOS;
import static com.simibubi.create.AllTags.NameSpace.FORGE;
import static com.simibubi.create.AllTags.NameSpace.MOD;
import static com.simibubi.create.AllTags.NameSpace.QUARK;
import static com.simibubi.create.AllTags.NameSpace.TIC;

import java.util.Collections;

import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.registry.CreateRegistries;

import net.createmod.catnip.lang.Lang;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class AllTags {
	public static <T> TagKey<T> optionalTag(IForgeRegistry<T> registry,
											ResourceLocation id) {
		return registry.tags()
			.createOptionalTagKey(id, Collections.emptySet());
	}

	public static <T> TagKey<T> forgeTag(IForgeRegistry<T> registry, String path) {
		return optionalTag(registry, new ResourceLocation("forge", path));
	}

	public static TagKey<Block> forgeBlockTag(String path) {
		return forgeTag(ForgeRegistries.BLOCKS, path);
	}

	public static TagKey<Item> forgeItemTag(String path) {
		return forgeTag(ForgeRegistries.ITEMS, path);
	}

	public static TagKey<Fluid> forgeFluidTag(String path) {
		return forgeTag(ForgeRegistries.FLUIDS, path);
	}

	public enum NameSpace {

		MOD(Create.ID, false, true),
		FORGE("forge"),
		TIC("tconstruct"),
		QUARK("quark"),
		GS("galosphere"),
		CURIOS("curios");

		public final String id;
		public final boolean optionalDefault;
		public final boolean alwaysDatagenDefault;

		NameSpace(String id) {
			this(id, true, false);
		}

		NameSpace(String id, boolean optionalDefault, boolean alwaysDatagenDefault) {
			this.id = id;
			this.optionalDefault = optionalDefault;
			this.alwaysDatagenDefault = alwaysDatagenDefault;
		}
	}

	public enum AllBlockTags {

		BRITTLE,
		CASING,
		COPYCAT_ALLOW,
		COPYCAT_DENY,
		FAN_PROCESSING_CATALYSTS_BLASTING(MOD, "fan_processing_catalysts/blasting"),
		FAN_PROCESSING_CATALYSTS_HAUNTING(MOD, "fan_processing_catalysts/haunting"),
		FAN_PROCESSING_CATALYSTS_SMOKING(MOD, "fan_processing_catalysts/smoking"),
		FAN_PROCESSING_CATALYSTS_SPLASHING(MOD, "fan_processing_catalysts/splashing"),
		FAN_TRANSPARENT,
		GIRDABLE_TRACKS,
		MOVABLE_EMPTY_COLLIDER,
		NON_MOVABLE,
		NON_BREAKABLE,
		ORE_OVERRIDE_STONE,
		PASSIVE_BOILER_HEATERS,
		SAFE_NBT,
		SEATS,
		POSTBOXES,
		TABLE_CLOTHS,
		TOOLBOXES,
		TRACKS,
		TREE_ATTACHMENTS,
		VALVE_HANDLES,
		WINDMILL_SAILS,
		WRENCH_PICKUP,
		CHEST_MOUNTED_STORAGE,
		SIMPLE_MOUNTED_STORAGE,
		FALLBACK_MOUNTED_STORAGE_BLACKLIST,
		ROOTS,
		SUGAR_CANE_VARIANTS,
		NON_HARVESTABLE,
		SINGLE_BLOCK_INVENTORIES,

		CORALS,

		RELOCATION_NOT_SUPPORTED(FORGE),
		WG_STONE(FORGE),

		SLIMY_LOGS(TIC),
		NON_DOUBLE_DOOR(QUARK),

		;

		public final TagKey<Block> tag;
		public final boolean alwaysDatagen;

		AllBlockTags() {
			this(MOD);
		}

		AllBlockTags(NameSpace namespace) {
			this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		AllBlockTags(NameSpace namespace, String path) {
			this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		AllBlockTags(NameSpace namespace, boolean optional, boolean alwaysDatagen) {
			this(namespace, null, optional, alwaysDatagen);
		}

		AllBlockTags(NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
			ResourceLocation id = new ResourceLocation(namespace.id, path == null ? Lang.asId(name()) : path);
			if (optional) {
				tag = optionalTag(ForgeRegistries.BLOCKS, id);
			} else {
				tag = BlockTags.create(id);
			}
			this.alwaysDatagen = alwaysDatagen;
		}

		@SuppressWarnings("deprecation")
		public boolean matches(Block block) {
			return block.builtInRegistryHolder()
				.is(tag);
		}

		public boolean matches(ItemStack stack) {
			return stack != null && stack.getItem() instanceof BlockItem blockItem && matches(blockItem.getBlock());
		}

		public boolean matches(BlockState state) {
			return state.is(tag);
		}

		private static void init() {
		}

	}

	public enum AllItemTags {

		BLAZE_BURNER_FUEL_REGULAR(MOD, "blaze_burner_fuel/regular"),
		BLAZE_BURNER_FUEL_SPECIAL(MOD, "blaze_burner_fuel/special"),
		CASING,
		CONTRAPTION_CONTROLLED,
		CREATE_INGOTS,
		CRUSHED_RAW_MATERIALS,
		INVALID_FOR_TRACK_PAVING,
		DEPLOYABLE_DRINK,
		MODDED_STRIPPED_LOGS,
		MODDED_STRIPPED_WOOD,
		PRESSURIZED_AIR_SOURCES,
		SANDPAPER,
		SEATS,
		POSTBOXES,
		TABLE_CLOTHS,
		DYED_TABLE_CLOTHS,
		PULPIFIABLE,
		SLEEPERS,
		TOOLBOXES,
		PACKAGES,
		CHAIN_RIDEABLE,
		TRACKS,
		UPRIGHT_ON_BELT,
		NOT_UPRIGHT_ON_BELT,
		NOT_POTION,
		VALVE_HANDLES,
		VANILLA_STRIPPED_LOGS,
		VANILLA_STRIPPED_WOOD,
		DISPENSE_BEHAVIOR_WRAP_BLACKLIST,

		STRIPPED_LOGS(FORGE),
		STRIPPED_WOOD(FORGE),
		PLATES(FORGE),
		OBSIDIAN_DUST(FORGE, "dusts/obsidian"),
		WRENCH(FORGE, "tools/wrench"),

		ALLURITE(MOD, "stone_types/galosphere/allurite"),
		AMETHYST(MOD, "stone_types/galosphere/amethyst"),
		LUMIERE(MOD, "stone_types/galosphere/lumiere"),

		UA_CORAL(MOD, "upgrade_aquatic/coral"),
		CURIOS_HEAD(CURIOS, "head");

		public final TagKey<Item> tag;
		public final boolean alwaysDatagen;

		AllItemTags() {
			this(MOD);
		}

		AllItemTags(NameSpace namespace) {
			this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		AllItemTags(NameSpace namespace, String path) {
			this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		AllItemTags(NameSpace namespace, boolean optional, boolean alwaysDatagen) {
			this(namespace, null, optional, alwaysDatagen);
		}

		AllItemTags(NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
			ResourceLocation id = new ResourceLocation(namespace.id, path == null ? Lang.asId(name()) : path);
			if (optional) {
				tag = optionalTag(ForgeRegistries.ITEMS, id);
			} else {
				tag = ItemTags.create(id);
			}
			this.alwaysDatagen = alwaysDatagen;
		}

		@SuppressWarnings("deprecation")
		public boolean matches(Item item) {
			return item.builtInRegistryHolder()
				.is(tag);
		}

		public boolean matches(ItemStack stack) {
			return stack.is(tag);
		}

		private static void init() {
		}

	}

	public enum AllFluidTags {

		BOTTOMLESS_ALLOW(MOD, "bottomless/allow"),
		BOTTOMLESS_DENY(MOD, "bottomless/deny"),
		FAN_PROCESSING_CATALYSTS_BLASTING(MOD, "fan_processing_catalysts/blasting"),
		FAN_PROCESSING_CATALYSTS_HAUNTING(MOD, "fan_processing_catalysts/haunting"),
		FAN_PROCESSING_CATALYSTS_SMOKING(MOD, "fan_processing_catalysts/smoking"),
		FAN_PROCESSING_CATALYSTS_SPLASHING(MOD, "fan_processing_catalysts/splashing"),

		HONEY(FORGE);

		public final TagKey<Fluid> tag;
		public final boolean alwaysDatagen;

		AllFluidTags() {
			this(MOD);
		}

		AllFluidTags(NameSpace namespace) {
			this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		AllFluidTags(NameSpace namespace, String path) {
			this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		AllFluidTags(NameSpace namespace, boolean optional, boolean alwaysDatagen) {
			this(namespace, null, optional, alwaysDatagen);
		}

		AllFluidTags(NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
			ResourceLocation id = new ResourceLocation(namespace.id, path == null ? Lang.asId(name()) : path);
			if (optional) {
				tag = optionalTag(ForgeRegistries.FLUIDS, id);
			} else {
				tag = FluidTags.create(id);
			}
			this.alwaysDatagen = alwaysDatagen;
		}

		@SuppressWarnings("deprecation")
		public boolean matches(Fluid fluid) {
			return fluid.is(tag);
		}

		public boolean matches(FluidState state) {
			return state.is(tag);
		}

		private static void init() {
		}

	}

	public enum AllEntityTags {

		BLAZE_BURNER_CAPTURABLE,
		IGNORE_SEAT,

		;

		public final TagKey<EntityType<?>> tag;
		public final boolean alwaysDatagen;

		AllEntityTags() {
			this(MOD);
		}

		AllEntityTags(NameSpace namespace) {
			this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		AllEntityTags(NameSpace namespace, String path) {
			this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		AllEntityTags(NameSpace namespace, boolean optional, boolean alwaysDatagen) {
			this(namespace, null, optional, alwaysDatagen);
		}

		AllEntityTags(NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
			ResourceLocation id = new ResourceLocation(namespace.id, path == null ? Lang.asId(name()) : path);
			if (optional) {
				tag = optionalTag(ForgeRegistries.ENTITY_TYPES, id);
			} else {
				tag = TagKey.create(Registries.ENTITY_TYPE, id);
			}
			this.alwaysDatagen = alwaysDatagen;
		}

		public boolean matches(EntityType<?> type) {
			return type.is(tag);
		}

		public boolean matches(Entity entity) {
			return matches(entity.getType());
		}

		private static void init() {
		}

	}

	public enum AllRecipeSerializerTags {

		AUTOMATION_IGNORE,

		;

		public final TagKey<RecipeSerializer<?>> tag;
		public final boolean alwaysDatagen;

		AllRecipeSerializerTags() {
			this(MOD);
		}

		AllRecipeSerializerTags(NameSpace namespace) {
			this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		AllRecipeSerializerTags(NameSpace namespace, String path) {
			this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
		}

		AllRecipeSerializerTags(NameSpace namespace, boolean optional, boolean alwaysDatagen) {
			this(namespace, null, optional, alwaysDatagen);
		}

		AllRecipeSerializerTags(NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
			ResourceLocation id = new ResourceLocation(namespace.id, path == null ? Lang.asId(name()) : path);
			if (optional) {
				tag = optionalTag(ForgeRegistries.RECIPE_SERIALIZERS, id);
			} else {
				tag = TagKey.create(Registries.RECIPE_SERIALIZER, id);
			}
			this.alwaysDatagen = alwaysDatagen;
		}

		public boolean matches(RecipeSerializer<?> recipeSerializer) {
			return ForgeRegistries.RECIPE_SERIALIZERS.getHolder(recipeSerializer).orElseThrow().is(tag);
		}

		private static void init() {
		}
	}

	public enum AllContraptionTypeTags {
		OPENS_CONTROLS,
		REQUIRES_VEHICLE_FOR_RENDER;

		public final TagKey<ContraptionType> tag;
		public final boolean alwaysDatagen;

		AllContraptionTypeTags() {
			ResourceLocation tagId = Create.asResource(Lang.asId(this.name()));
			this.tag = TagKey.create(CreateRegistries.CONTRAPTION_TYPE, tagId);
			this.alwaysDatagen = true;
		}

		public boolean matches(ContraptionType type) {
			return type.is(this.tag);
		}

		private static void init() {
		}
	}

	public enum AllMountedItemStorageTypeTags {
		INTERNAL,
		FUEL_BLACKLIST;

		public final TagKey<MountedItemStorageType<?>> tag;
		public final boolean alwaysDatagen;

		AllMountedItemStorageTypeTags() {
			ResourceLocation tagId = Create.asResource(Lang.asId(this.name()));
			this.tag = TagKey.create(CreateRegistries.MOUNTED_ITEM_STORAGE_TYPE, tagId);
			this.alwaysDatagen = true;
		}

		public boolean matches(MountedItemStorage storage) {
			return this.matches(storage.type);
		}

		public boolean matches(MountedItemStorageType<?> type) {
			return type.is(this.tag);
		}

		private static void init() {
		}
	}

	public static void init() {
		AllBlockTags.init();
		AllItemTags.init();
		AllFluidTags.init();
		AllEntityTags.init();
		AllRecipeSerializerTags.init();
		AllContraptionTypeTags.init();
		AllMountedItemStorageTypeTags.init();
	}
}
