package com.simibubi.create.content.equipment.potatoCannon;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllEnchantments;
import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.CreateClient;
import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.zapper.ShootableGadgetItemMethods;
import com.simibubi.create.foundation.item.CustomArmPoseItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.GlobalRegistryAccess;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class PotatoCannonItem extends ProjectileWeaponItem implements CustomArmPoseItem {

	public static final int MAX_DAMAGE = 100;

	public PotatoCannonItem(Properties properties) {
		super(properties.defaultDurability(MAX_DAMAGE));
	}

	@Nullable
	public static Ammo getAmmo(Player player, ItemStack heldStack) {
		ItemStack ammoStack = player.getProjectile(heldStack);
		if (ammoStack.isEmpty()) {
			return null;
		}

		Optional<Holder.Reference<PotatoCannonProjectileType>> optionalType = PotatoCannonProjectileType.getTypeForItem(player.level().registryAccess(), ammoStack.getItem());
		if (optionalType.isEmpty()) {
			return null;
		}

		return new Ammo(ammoStack, optionalType.get().value());
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		return use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack heldStack = player.getItemInHand(hand);
		if (ShootableGadgetItemMethods.shouldSwap(player, heldStack, hand, s -> s.getItem() instanceof PotatoCannonItem)) {
			return InteractionResultHolder.fail(heldStack);
		}

		Ammo ammo = getAmmo(player, heldStack);
		if (ammo == null) {
			return InteractionResultHolder.pass(heldStack);
		}
		ItemStack ammoStack = ammo.stack();
		PotatoCannonProjectileType projectileType = ammo.type();

		if (level.isClientSide) {
			CreateClient.POTATO_CANNON_RENDER_HANDLER.dontAnimateItem(hand);
			return InteractionResultHolder.success(heldStack);
		}

		Vec3 barrelPos = ShootableGadgetItemMethods.getGunBarrelVec(player, hand == InteractionHand.MAIN_HAND,
			new Vec3(.75f, -0.15f, 1.5f));
		Vec3 correction =
			ShootableGadgetItemMethods.getGunBarrelVec(player, hand == InteractionHand.MAIN_HAND, new Vec3(-.05f, 0, 0))
				.subtract(player.position()
					.add(0, player.getEyeHeight(), 0));

		Vec3 lookVec = player.getLookAngle();
		Vec3 motion = lookVec.add(correction)
			.normalize()
			.scale(2)
			.scale(projectileType.velocityMultiplier());

		float soundPitch = projectileType.soundPitch() + (level.getRandom().nextFloat() - .5f) / 4f;

		boolean spray = projectileType.split() > 1;
		Vec3 sprayBase = VecHelper.rotate(new Vec3(0, 0.1, 0), 360 * level.getRandom().nextFloat(), Axis.Z);
		float sprayChange = 360f / projectileType.split();

		ItemStack ammoStackCopy = ammoStack.copy();

		for (int i = 0; i < projectileType.split(); i++) {
			PotatoProjectileEntity projectile = AllEntityTypes.POTATO_PROJECTILE.create(level);
			projectile.setItem(ammoStackCopy);
			projectile.setEnchantmentEffectsFromCannon(heldStack);

			Vec3 splitMotion = motion;
			if (spray) {
				float imperfection = 40 * (level.getRandom().nextFloat() - 0.5f);
				Vec3 sprayOffset = VecHelper.rotate(sprayBase, i * sprayChange + imperfection, Axis.Z);
				splitMotion = splitMotion.add(VecHelper.lookAt(sprayOffset, motion));
			}

			if (i != 0)
				projectile.recoveryChance = 0;

			projectile.setPos(barrelPos.x, barrelPos.y, barrelPos.z);
			projectile.setDeltaMovement(splitMotion);
			projectile.setOwner(player);
			level.addFreshEntity(projectile);
		}

		if (!player.isCreative()) {
			ammoStack.shrink(1);
			if (ammoStack.isEmpty())
				player.getInventory().removeItem(ammoStack);
		}

		if (!BacktankUtil.canAbsorbDamage(player, maxUses()))
			heldStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

		ShootableGadgetItemMethods.applyCooldown(player, heldStack, hand, s -> s.getItem() instanceof PotatoCannonItem, projectileType.reloadTicks());
		ShootableGadgetItemMethods.sendPackets(player,
			b -> new PotatoCannonPacket(barrelPos, lookVec.normalize(), ammoStack, hand, soundPitch, b));
		return InteractionResultHolder.success(heldStack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			super.appendHoverText(stack, level, tooltip, flag);
			return;
		}

		Ammo ammo = getAmmo(player, stack);
		if (ammo == null) {
			super.appendHoverText(stack, level, tooltip, flag);
			return;
		}
		ItemStack ammoStack = ammo.stack();
		PotatoCannonProjectileType type = ammo.type();

		int power = stack.getEnchantmentLevel(Enchantments.POWER_ARROWS);
		int punch = stack.getEnchantmentLevel(Enchantments.PUNCH_ARROWS);
		final float additionalDamageMult = 1 + power * .2f;
		final float additionalKnockback = punch * .5f;

		String _attack = "potato_cannon.ammo.attack_damage";
		String _reload = "potato_cannon.ammo.reload_ticks";
		String _knockback = "potato_cannon.ammo.knockback";

		tooltip.add(CommonComponents.EMPTY);
		tooltip.add(Component.translatable(ammoStack.getDescriptionId()).append(Component.literal(":"))
			.withStyle(ChatFormatting.GRAY));
		MutableComponent spacing = CommonComponents.space();
		ChatFormatting green = ChatFormatting.GREEN;
		ChatFormatting darkGreen = ChatFormatting.DARK_GREEN;

		float damageF = type.damage() * additionalDamageMult;
		MutableComponent damage = Component.literal(damageF == Mth.floor(damageF) ? "" + Mth.floor(damageF) : "" + damageF);
		MutableComponent reloadTicks = Component.literal("" + type.reloadTicks());
		MutableComponent knockback =
			Component.literal("" + (type.knockback() + additionalKnockback));

		damage = damage.withStyle(additionalDamageMult > 1 ? green : darkGreen);
		knockback = knockback.withStyle(additionalKnockback > 0 ? green : darkGreen);
		reloadTicks = reloadTicks.withStyle(darkGreen);

		tooltip.add(spacing.plainCopy()
			.append(CreateLang.translateDirect(_attack, damage)
				.withStyle(darkGreen)));
		tooltip.add(spacing.plainCopy()
			.append(CreateLang.translateDirect(_reload, reloadTicks)
				.withStyle(darkGreen)));
		tooltip.add(spacing.plainCopy()
			.append(CreateLang.translateDirect(_knockback, knockback)
				.withStyle(darkGreen)));

		super.appendHoverText(stack, level, tooltip, flag);
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player player) {
		return false;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || newStack.getItem() != oldStack.getItem();
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return stack -> PotatoCannonProjectileType.getTypeForItem(GlobalRegistryAccess.getOrThrow(), stack.getItem())
			.isPresent();
	}

	@Override
	public int getDefaultProjectileRange() {
		return 15;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if (enchantment == Enchantments.POWER_ARROWS)
			return true;
		if (enchantment == Enchantments.PUNCH_ARROWS)
			return true;
		if (enchantment == Enchantments.FLAMING_ARROWS)
			return true;
		if (enchantment == Enchantments.MOB_LOOTING)
			return true;
		if (enchantment == AllEnchantments.POTATO_RECOVERY.get())
			return true;
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return BacktankUtil.isBarVisible(stack, maxUses());
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return BacktankUtil.getBarWidth(stack, maxUses());
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return BacktankUtil.getBarColor(stack, maxUses());
	}

	private static int maxUses() {
		return AllConfigs.server().equipment.maxPotatoCannonShots.get();
	}

	@Override
	public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
		return true;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.NONE;
	}

	@Override
	@Nullable
	public ArmPose getArmPose(ItemStack stack, AbstractClientPlayer player, InteractionHand hand) {
		if (!player.swinging) {
			return ArmPose.CROSSBOW_HOLD;
		}
		return null;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(SimpleCustomRenderer.create(this, new PotatoCannonItemRenderer()));
	}

	public record Ammo(ItemStack stack, PotatoCannonProjectileType type) {
	}
}
