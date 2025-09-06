package com.simibubi.create.api.contraption.train;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.trains.track.AllPortalTracks;

import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalInfo;

import net.minecraftforge.common.util.ITeleporter;

/**
 * A provider for portal track connections.
 * Takes a track inbound through a portal and finds the exit location for the outbound track.
 */
@FunctionalInterface
public interface PortalTrackProvider {
	SimpleRegistry<Block, PortalTrackProvider> REGISTRY = SimpleRegistry.create();

	/**
	 * Find the exit location for a track going through a portal.
	 * @param level the level of the inbound track
	 * @param face the face of the inbound track
	 */
	Exit findExit(ServerLevel level, BlockFace face);

	/**
	 * Checks if a given {@link BlockState} represents a supported portal block.
	 * @param state The block state to check.
	 * @return {@code true} if the block state represents a supported portal; {@code false} otherwise.
	 */
	static boolean isSupportedPortal(BlockState state) {
		return REGISTRY.get(state) != null;
	}

	/**
	 * Retrieves the corresponding outbound track on the other side of a portal.
	 * @param level        The current {@link ServerLevel}.
	 * @param inboundTrack The inbound track {@link BlockFace}.
	 * @return the found outbound track, or null if one wasn't found.
	 */
	@Nullable
	static Exit getOtherSide(ServerLevel level, BlockFace inboundTrack) {
		BlockPos portalPos = inboundTrack.getConnectedPos();
		BlockState portalState = level.getBlockState(portalPos);
		PortalTrackProvider provider = REGISTRY.get(portalState);
		return provider == null ? null : provider.findExit(level, inboundTrack);
	}

	/**
	 * Find an exit location by using an {@link ITeleporter} instance.
	 * @param level              The level of the inbound track
	 * @param face				 The face of the inbound track
	 * @param firstDimension     The first dimension (typically the Overworld)
	 * @param secondDimension    The second dimension (e.g., Nether, Aether)
	 * @param customPortalForcer A function to obtain the {@link ITeleporter} for the target level
	 * @return A found exit, or null if one wasn't found
	 */
	static Exit fromTeleporter(ServerLevel level, BlockFace face, ResourceKey<Level> firstDimension,
							   ResourceKey<Level> secondDimension, Function<ServerLevel, ITeleporter> customPortalForcer) {
		return AllPortalTracks.fromTeleporter(level, face, firstDimension, secondDimension, customPortalForcer);
	}

	/**
	 * Find an exit location by teleporting a probe entity to find a {@link PortalInfo}.
	 * @param level              The level of the inbound track
	 * @param face 				 The face of the inbound track
	 * @param firstDimension     The first dimension
	 * @param secondDimension    The second dimension
	 * @param portalInfoProvider A function that provides the {@link PortalInfo} given the target level and probe entity.
	 * @return A found exit, or null if one wasn't found
	 */
	static Exit fromProbe(ServerLevel level, BlockFace face, ResourceKey<Level> firstDimension,
						  ResourceKey<Level> secondDimension, BiFunction<ServerLevel, SuperGlueEntity, PortalInfo> portalInfoProvider) {
		return AllPortalTracks.fromProbe(level, face, firstDimension, secondDimension, portalInfoProvider);
	}

	record Exit(ServerLevel level, BlockFace face) {
	}
}
