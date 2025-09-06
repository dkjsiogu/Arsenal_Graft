package com.simibubi.create.content.equipment.blueprint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.blueprint.BlueprintEntity.BlueprintCraftingInventory;
import com.simibubi.create.content.equipment.blueprint.BlueprintEntity.BlueprintSection;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.filter.AttributeFilterMenu.WhitelistMode;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.InTagAttribute;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.tableCloth.BlueprintOverlayShopContext;
import com.simibubi.create.content.logistics.tableCloth.ShoppingListItem.ShoppingList;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlockEntity;
import com.simibubi.create.content.trains.track.TrackPlacement.PlacementInfo;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;

// TODO - Split up into specific overlays
public class BlueprintOverlayRenderer {

	public static final IGuiOverlay OVERLAY = BlueprintOverlayRenderer::renderOverlay;

	static boolean active;
	static boolean empty;
	static boolean noOutput;
	static boolean lastSneakState;
	static BlueprintSection lastTargetedSection;
	static BlueprintOverlayShopContext shopContext;

	static Map<ItemStack, ItemStack[]> cachedRenderedFilters = new IdentityHashMap<>();
	static List<Pair<ItemStack, Boolean>> ingredients = new ArrayList<>();
	static List<ItemStack> results = new ArrayList<>();
	static boolean resultCraftable = false;

	public static void tick() {
		Minecraft mc = Minecraft.getInstance();

		BlueprintSection last = lastTargetedSection;
		lastTargetedSection = null;
		active = false;
		noOutput = false;
		shopContext = null;

		if (mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
			return;

		HitResult mouseOver = mc.hitResult;
		if (mouseOver == null)
			return;
		if (mouseOver.getType() != Type.ENTITY)
			return;

		EntityHitResult entityRay = (EntityHitResult) mouseOver;
		if (!(entityRay.getEntity() instanceof BlueprintEntity blueprintEntity))
			return;

		BlueprintSection sectionAt = blueprintEntity.getSectionAt(entityRay.getLocation()
			.subtract(blueprintEntity.position()));

		lastTargetedSection = last;
		active = true;

		boolean sneak = mc.player.isShiftKeyDown();
		if (sectionAt != lastTargetedSection || AnimationTickHolder.getTicks() % 10 == 0 || lastSneakState != sneak)
			rebuild(sectionAt, sneak);

		lastTargetedSection = sectionAt;
		lastSneakState = sneak;
	}

	public static void displayTrackRequirements(PlacementInfo info, ItemStack pavementItem) {
		if (active)
			return;
		prepareCustomOverlay();

		int tracks = info.requiredTracks;
		while (tracks > 0) {
			ingredients.add(
				Pair.of(new ItemStack(info.trackMaterial.getBlock(), Math.min(64, tracks)), info.hasRequiredTracks));
			tracks -= 64;
		}

		int pavement = info.requiredPavement;
		while (pavement > 0) {
			ingredients.add(Pair.of(ItemHandlerHelper.copyStackWithSize(pavementItem, Math.min(64, pavement)),
				info.hasRequiredPavement));
			pavement -= 64;
		}
	}

	public static void displayChainRequirements(Item chainItem, int count, boolean fulfilled) {
		if (active)
			return;
		prepareCustomOverlay();

		int chains = count;
		while (chains > 0) {
			ingredients.add(Pair.of(new ItemStack(chainItem, Math.min(64, chains)), fulfilled));
			chains -= 64;
		}
	}

	public static void displayClothShop(TableClothBlockEntity dce, int alreadyPurchased, ShoppingList list) {
		if (active)
			return;
		prepareCustomOverlay();
		noOutput = false;

		shopContext = new BlueprintOverlayShopContext(false, dce.getStockLevelForTrade(list), alreadyPurchased);

		ingredients.add(Pair.of(dce.getPaymentItem()
				.copyWithCount(dce.getPaymentAmount()),
			!dce.getPaymentItem()
				.isEmpty() && shopContext.stockLevel() > shopContext.purchases()));
		for (BigItemStack entry : dce.requestData.encodedRequest.stacks())
			results.add(entry.stack.copyWithCount(entry.count));
	}

	public static void displayShoppingList(Couple<InventorySummary> bakedList) {
		if (active || bakedList == null)
			return;
		Minecraft mc = Minecraft.getInstance();
		prepareCustomOverlay();
		noOutput = false;

		shopContext = new BlueprintOverlayShopContext(true, 1, 0);

		for (BigItemStack entry : bakedList.getSecond()
			.getStacksByCount()) {
			ingredients.add(Pair.of(entry.stack.copyWithCount(entry.count), canAfford(mc.player, entry)));
		}

		for (BigItemStack entry : bakedList.getFirst()
			.getStacksByCount())
			results.add(entry.stack.copyWithCount(entry.count));
	}

	private static boolean canAfford(Player player, BigItemStack entry) {
		int itemsPresent = 0;
		for (int i = 0; i < player.getInventory().items.size(); i++) {
			ItemStack item = player.getInventory()
				.getItem(i);
			if (item.isEmpty() || !ItemHandlerHelper.canItemStacksStack(item, entry.stack))
				continue;
			itemsPresent += item.getCount();
		}
		return itemsPresent >= entry.count;
	}

	private static void prepareCustomOverlay() {
		active = true;
		empty = false;
		noOutput = true;
		ingredients.clear();
		results.clear();
		shopContext = null;
	}

	public static void rebuild(BlueprintSection sectionAt, boolean sneak) {
		cachedRenderedFilters.clear();
		ItemStackHandler items = sectionAt.getItems();
		boolean empty = true;
		for (int i = 0; i < 9; i++) {
			if (!items.getStackInSlot(i)
				.isEmpty()) {
				empty = false;
				break;
			}
		}

		BlueprintOverlayRenderer.empty = empty;
		BlueprintOverlayRenderer.results.clear();

		if (empty)
			return;

		boolean firstPass = true;
		boolean success = true;
		Minecraft mc = Minecraft.getInstance();
		ItemStackHandler playerInv = new ItemStackHandler(mc.player.getInventory()
			.getContainerSize());
		for (int i = 0; i < playerInv.getSlots(); i++)
			playerInv.setStackInSlot(i, mc.player.getInventory()
				.getItem(i)
				.copy());

		int amountCrafted = 0;
		Optional<CraftingRecipe> recipe = Optional.empty();
		Map<Integer, ItemStack> craftingGrid = new HashMap<>();
		ingredients.clear();
		ItemStackHandler missingItems = new ItemStackHandler(64);
		ItemStackHandler availableItems = new ItemStackHandler(64);
		List<ItemStack> newlyAdded = new ArrayList<>();
		List<ItemStack> newlyMissing = new ArrayList<>();
		boolean invalid = false;

		do {
			craftingGrid.clear();
			newlyAdded.clear();
			newlyMissing.clear();

			Search:
			for (int i = 0; i < 9; i++) {
				FilterItemStack requestedItem = FilterItemStack.of(items.getStackInSlot(i));
				if (requestedItem.isEmpty()) {
					craftingGrid.put(i, ItemStack.EMPTY);
					continue;
				}

				for (int slot = 0; slot < playerInv.getSlots(); slot++) {
					if (!requestedItem.test(mc.level, playerInv.getStackInSlot(slot)))
						continue;
					ItemStack currentItem = playerInv.extractItem(slot, 1, false);
					craftingGrid.put(i, currentItem);
					newlyAdded.add(currentItem);
					continue Search;
				}

				success = false;
				newlyMissing.add(requestedItem.item());
			}

			if (success) {
				CraftingContainer craftingInventory = new BlueprintCraftingInventory(craftingGrid);
				if (!recipe.isPresent())
					recipe = mc.level.getRecipeManager()
						.getRecipeFor(RecipeType.CRAFTING, craftingInventory, mc.level);
				ItemStack resultFromRecipe = recipe.filter(r -> r.matches(craftingInventory, mc.level))
					.map(r -> r.assemble(craftingInventory, mc.level.registryAccess()))
					.orElse(ItemStack.EMPTY);

				if (resultFromRecipe.isEmpty()) {
					if (!recipe.isPresent())
						invalid = true;
					success = false;
				} else if (resultFromRecipe.getCount() + amountCrafted > 64) {
					success = false;
				} else {
					amountCrafted += resultFromRecipe.getCount();
					if (results.isEmpty())
						results.add(resultFromRecipe.copy());
					else
						results.get(0)
							.grow(resultFromRecipe.getCount());
					resultCraftable = true;
					firstPass = false;
				}
			}

			if (success || firstPass) {
				newlyAdded.forEach(s -> ItemHandlerHelper.insertItemStacked(availableItems, s, false));
				newlyMissing.forEach(s -> ItemHandlerHelper.insertItemStacked(missingItems, s, false));
			}

			if (!success) {
				if (firstPass) {
					results.clear();
					if (!invalid)
						results.add(items.getStackInSlot(9));
					resultCraftable = false;
				}
				break;
			}

			if (!sneak)
				break;

		} while (success);

		for (int i = 0; i < 9; i++) {
			ItemStack available = availableItems.getStackInSlot(i);
			if (available.isEmpty())
				continue;
			ingredients.add(Pair.of(available, true));
		}
		for (int i = 0; i < 9; i++) {
			ItemStack missing = missingItems.getStackInSlot(i);
			if (missing.isEmpty())
				continue;
			ingredients.add(Pair.of(missing, false));
		}
	}

	public static void renderOverlay(ForgeGui gui, GuiGraphics graphics, float partialTicks, int width, int height) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.options.hideGui || mc.screen != null)
			return;

		if (!active || empty)
			return;

		boolean invalidShop = shopContext != null && (ingredients.isEmpty() || ingredients.get(0)
			.getFirst()
			.isEmpty() || shopContext.stockLevel() == 0);

		int w = 21 * ingredients.size();

		if (!noOutput) {
			w += 21 * results.size();
			w += 30;
		}

		int x = (width - w) / 2;
		int y = (int) (height - 100);

		if (shopContext != null) {
			TooltipRenderUtil.renderTooltipBackground(graphics, x - 2, y + 1, w + 4, 19, 0, 0x55_000000, 0x55_000000, 0,
				0);

			AllGuiTextures.TRADE_OVERLAY.render(graphics, width / 2 - 48, y - 19);
			if (shopContext.purchases() > 0) {
				graphics.renderItem(AllItems.SHOPPING_LIST.asStack(), width / 2 + 20, y - 20);
				graphics.drawString(mc.font, Component.literal("x" + shopContext.purchases()), width / 2 + 20 + 16,
					y - 20 + 4, 0xff_eeeeee, true);
			}
		}

		// Ingredients
		for (Pair<ItemStack, Boolean> pair : ingredients) {
			RenderSystem.enableBlend();
			(pair.getSecond() ? AllGuiTextures.HOTSLOT_ACTIVE : AllGuiTextures.HOTSLOT).render(graphics, x, y);
			ItemStack itemStack = pair.getFirst();
			String count = shopContext != null && !shopContext.checkout() || pair.getSecond() ? null
				: ChatFormatting.GOLD.toString() + itemStack.getCount();
			drawItemStack(graphics, mc, x, y, itemStack, count);
			x += 21;
		}

		if (noOutput)
			return;

		// Arrow
		x += 5;
		RenderSystem.enableBlend();
		if (invalidShop)
			AllGuiTextures.HOTSLOT_ARROW_BAD.render(graphics, x, y + 4);
		else
			AllGuiTextures.HOTSLOT_ARROW.render(graphics, x, y + 4);
		x += 25;

		// Outputs
		if (results.isEmpty()) {
			AllGuiTextures.HOTSLOT.render(graphics, x, y);
			GuiGameElement.of(Items.BARRIER)
				.at(x + 3, y + 3)
				.render(graphics);
		} else {
			for (ItemStack result : results) {
				AllGuiTextures slot = resultCraftable ? AllGuiTextures.HOTSLOT_SUPER_ACTIVE : AllGuiTextures.HOTSLOT;
				if (!invalidShop && shopContext != null && shopContext.stockLevel() > shopContext.purchases())
					slot = AllGuiTextures.HOTSLOT_ACTIVE;
				slot.render(graphics, resultCraftable ? x - 1 : x, resultCraftable ? y - 1 : y);
				drawItemStack(graphics, mc, x, y, result, null);
				x += 21;
			}
		}

		if (shopContext != null && !shopContext.checkout()) {
			int cycle = 0;
			for (boolean count : Iterate.trueAndFalse)
				for (int i = 0; i < results.size(); i++) {
					ItemStack result = results.get(i);
					List<Component> tooltipLines = result.getTooltipLines(mc.player, TooltipFlag.NORMAL);
					if (tooltipLines.size() <= 1)
						continue;
					if (count) {
						cycle++;
						continue;
					}
					if ((gui.getGuiTicks() / 40) % cycle != i)
						continue;
					graphics.renderComponentTooltip(gui.getFont(), tooltipLines, mc.getWindow()
							.getGuiScaledWidth(),
						mc.getWindow()
							.getGuiScaledHeight());
				}
		}

		RenderSystem.disableBlend();
	}

	public static void drawItemStack(GuiGraphics graphics, Minecraft mc, int x, int y, ItemStack itemStack,
									 String count) {
		if (itemStack.getItem() instanceof FilterItem) {
			int step = AnimationTickHolder.getTicks(mc.level) / 10;
			ItemStack[] itemsMatchingFilter = getItemsMatchingFilter(itemStack);
			if (itemsMatchingFilter.length > 0)
				itemStack = itemsMatchingFilter[step % itemsMatchingFilter.length];
		}

		GuiGameElement.of(itemStack)
			.at(x + 3, y + 3)
			.render(graphics);
		graphics.renderItemDecorations(mc.font, itemStack, x + 3, y + 3, count);
	}

	private static ItemStack[] getItemsMatchingFilter(ItemStack filter) {
		return cachedRenderedFilters.computeIfAbsent(filter, itemStack -> {
			CompoundTag tag = itemStack.getOrCreateTag();

			if (AllItems.FILTER.isIn(itemStack) && !tag.getBoolean("Blacklist")) {
				ItemStackHandler filterItems = FilterItem.getFilterItems(itemStack);
				return ItemHelper.getNonEmptyStacks(filterItems).toArray(ItemStack[]::new);
			}

			if (AllItems.ATTRIBUTE_FILTER.isIn(itemStack)) {
				WhitelistMode whitelistMode = WhitelistMode.values()[tag.getInt("WhitelistMode")];
				ListTag attributes = tag.getList("MatchedAttributes", net.minecraft.nbt.Tag.TAG_COMPOUND);
				if (whitelistMode == WhitelistMode.WHITELIST_DISJ && attributes.size() == 1) {
					ItemAttribute fromNBT = ItemAttribute.loadStatic((CompoundTag) attributes.get(0));
					if (fromNBT instanceof InTagAttribute inTag) {
						ITagManager<Item> tagManager = ForgeRegistries.ITEMS.tags();
						if (tagManager.isKnownTagName(inTag.tag)) {
							ITag<Item> taggedItems = tagManager.getTag(inTag.tag);
							if (!taggedItems.isEmpty()) {
								ItemStack[] stacks = new ItemStack[taggedItems.size()];
								int i = 0;
								for (Item item : taggedItems) {
									stacks[i] = new ItemStack(item);
									i++;
								}
								return stacks;
							}
						}
					}
				}
			}

			return new ItemStack[0];
		});
	}

}
