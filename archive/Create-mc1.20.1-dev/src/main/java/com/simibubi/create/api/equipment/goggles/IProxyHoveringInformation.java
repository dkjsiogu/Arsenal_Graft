package com.simibubi.create.api.equipment.goggles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Implement this interface on the {@link BlockEntity} that wants proxy the information
 */
public interface IProxyHoveringInformation {
	BlockPos getInformationSource(Level level, BlockPos pos, BlockState state);
}
