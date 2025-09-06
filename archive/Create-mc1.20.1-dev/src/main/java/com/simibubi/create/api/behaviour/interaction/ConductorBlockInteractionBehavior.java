package com.simibubi.create.api.behaviour.interaction;

import java.util.function.Consumer;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleItem;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.data.Iterate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

/**
 * Partial interaction behavior implementation that allows blocks to act as conductors on trains, like Blaze Burners.
 */
public abstract class ConductorBlockInteractionBehavior extends MovingInteractionBehaviour {
	/**
	 * Check if the given state is capable of being a conductor.
	 */
	public abstract boolean isValidConductor(BlockState state);

	/**
	 * Called when the conductor's schedule has changed.
	 * @param hasSchedule true if the schedule was set, false if it was removed
	 * @param blockStateSetter a consumer that will change the BlockState of this conductor on the contraption
	 */
	protected void onScheduleUpdate(boolean hasSchedule, BlockState currentBlockState, Consumer<BlockState> blockStateSetter) {
	}

	@Override
	public final boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos,
		AbstractContraptionEntity contraptionEntity) {
		ItemStack itemInHand = player.getItemInHand(activeHand);

		if (!(contraptionEntity instanceof CarriageContraptionEntity carriageEntity))
			return false;
		if (activeHand == InteractionHand.OFF_HAND)
			return false;
		Contraption contraption = carriageEntity.getContraption();
		if (!(contraption instanceof CarriageContraption carriageContraption))
			return false;

		StructureBlockInfo info = carriageContraption.getBlocks()
			.get(localPos);
		if (info == null || !this.isValidConductor(info.state()))
			return false;

		Direction assemblyDirection = carriageContraption.getAssemblyDirection();
		for (Direction direction : Iterate.directionsInAxis(assemblyDirection.getAxis())) {
			if (!carriageContraption.inControl(localPos, direction))
				continue;

			Train train = carriageEntity.getCarriage().train;
			if (train == null)
				return false;
			if (player.level().isClientSide)
				return true;

			if (train.runtime.getSchedule() != null) {
				if (train.runtime.paused && !train.runtime.completed) {
					train.runtime.paused = false;
					AllSoundEvents.CONFIRM.playOnServer(player.level(), player.blockPosition(), 1, 1);
					player.displayClientMessage(CreateLang.translateDirect("schedule.continued"), true);
					return true;
				}

				if (!itemInHand.isEmpty()) {
					AllSoundEvents.DENY.playOnServer(player.level(), player.blockPosition(), 1, 1);
					player.displayClientMessage(CreateLang.translateDirect("schedule.remove_with_empty_hand"), true);
					return true;
				}

				AllSoundEvents.playItemPickup(player);
				player.displayClientMessage(CreateLang.translateDirect(
					train.runtime.isAutoSchedule ? "schedule.auto_removed_from_train" : "schedule.removed_from_train"),
					true);
				player.setItemInHand(activeHand, train.runtime.returnSchedule());
				this.onScheduleUpdate(false, info.state(), newBlockState -> setBlockState(localPos, contraptionEntity, newBlockState));
				return true;
			}

			if (!AllItems.SCHEDULE.isIn(itemInHand))
				return true;

			Schedule schedule = ScheduleItem.getSchedule(itemInHand);
			if (schedule == null)
				return false;

			if (schedule.entries.isEmpty()) {
				AllSoundEvents.DENY.playOnServer(player.level(), player.blockPosition(), 1, 1);
				player.displayClientMessage(CreateLang.translateDirect("schedule.no_stops"), true);
				return true;
			}
			this.onScheduleUpdate(true, info.state(), newBlockState -> setBlockState(localPos, contraptionEntity, newBlockState));
			train.runtime.setSchedule(schedule, false);
			AllAdvancements.CONDUCTOR.awardTo(player);
			AllSoundEvents.CONFIRM.playOnServer(player.level(), player.blockPosition(), 1, 1);
			player.displayClientMessage(CreateLang.translateDirect("schedule.applied_to_train")
				.withStyle(ChatFormatting.GREEN), true);
			itemInHand.shrink(1);
			player.setItemInHand(activeHand, itemInHand.isEmpty() ? ItemStack.EMPTY : itemInHand);
			return true;
		}

		player.displayClientMessage(CreateLang.translateDirect("schedule.non_controlling_seat"), true);
		AllSoundEvents.DENY.playOnServer(player.level(), player.blockPosition(), 1, 1);
		return true;
	}

	private void setBlockState(BlockPos localPos, AbstractContraptionEntity contraption, BlockState newState) {
		StructureTemplate.StructureBlockInfo info = contraption.getContraption().getBlocks().get(localPos);
		if (info != null) {
			setContraptionBlockData(contraption, localPos, new StructureTemplate.StructureBlockInfo(info.pos(), newState, info.nbt()));
		}
	}

	/**
	 * Implementation used for Blaze Burners. May be reused by addons if applicable.
	 */
	public static class BlazeBurner extends ConductorBlockInteractionBehavior {
		@Override
		public boolean isValidConductor(BlockState state) {
			return state.getValue(BlazeBurnerBlock.HEAT_LEVEL) != BlazeBurnerBlock.HeatLevel.NONE;
		}
	}
}
