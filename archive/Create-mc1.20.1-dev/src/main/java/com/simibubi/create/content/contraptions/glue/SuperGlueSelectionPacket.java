package com.simibubi.create.content.contraptions.glue;

import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.Set;

public class SuperGlueSelectionPacket extends SimplePacketBase {

	private BlockPos from;
	private BlockPos to;

	public SuperGlueSelectionPacket(BlockPos from, BlockPos to) {
		this.from = from;
		this.to = to;
	}

	public SuperGlueSelectionPacket(FriendlyByteBuf buffer) {
		from = buffer.readBlockPos();
		to = buffer.readBlockPos();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(from);
		buffer.writeBlockPos(to);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();

			double range = player.getAttribute(ForgeMod.BLOCK_REACH.get())
				.getValue() + 2;
			if (player.distanceToSqr(Vec3.atCenterOf(to)) > range * range)
				return;
			if (!to.closerThan(from, 25))
				return;

			Set<BlockPos> group = SuperGlueSelectionHelper.searchGlueGroup(player.level(), from, to, false);
			if (group == null)
				return;
			if (!group.contains(to))
				return;
			if (!SuperGlueSelectionHelper.collectGlueFromInventory(player, 1, true))
				return;

			AABB bb = SuperGlueEntity.span(from, to);
			SuperGlueSelectionHelper.collectGlueFromInventory(player, 1, false);
			SuperGlueEntity entity = new SuperGlueEntity(player.level(), bb);
			player.level().addFreshEntity(entity);
			entity.spawnParticles();

			AllAdvancements.SUPER_GLUE.awardTo(player);
		});
		return true;
	}

}
