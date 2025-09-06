package com.simibubi.create.foundation.mixin;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.advancement.AllAdvancements;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

@Mixin(SmithingMenu.class)
public class SmithingMenuMixin {
	@Inject(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;awardUsedRecipes(Lnet/minecraft/world/entity/player/Player;Ljava/util/List;)V"))
	private void create$awardAdvancementWhenTrimmingCardboardArmor(Player player, ItemStack stack, CallbackInfo ci) {
		if (AllItems.CARDBOARD_HELMET.isIn(stack) ||
			AllItems.CARDBOARD_CHESTPLATE.isIn(stack) ||
			AllItems.CARDBOARD_LEGGINGS.isIn(stack) ||
			AllItems.CARDBOARD_BOOTS.isIn(stack)) {
			AllAdvancements.CARDBOARD_ARMOR_TRIM.awardTo(player);
		}
	}

	// Only add enchantments to the backtank if it supports them
	@ModifyExpressionValue(
		method = "createResult",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/crafting/SmithingRecipe;assemble(Lnet/minecraft/world/Container;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;"
		)
	)
	private ItemStack create$preventUnbreakingOnBacktanks(ItemStack original) {
		if (AllItems.COPPER_BACKTANK.isIn(original) || AllItems.NETHERITE_BACKTANK.isIn(original)) {
			Map<Enchantment, Integer> enchantments = new HashMap<>();

			EnchantmentHelper.getEnchantments(original).forEach((enchantment, level) -> {
				if (enchantment.canEnchant(original))
					enchantments.put(enchantment, level);
			});

			EnchantmentHelper.setEnchantments(enchantments, original);
		}

		return original;
	}
}
