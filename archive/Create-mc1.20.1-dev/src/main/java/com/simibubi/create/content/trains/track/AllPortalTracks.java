package com.simibubi.create.content.trains.track;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.simibubi.create.Create;
import com.simibubi.create.api.contraption.train.PortalTrackProvider;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.betterend.BetterEndPortalCompat;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;

import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;

import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Manages portal track integrations for various dimensions and mods within the Create mod.
 * <p>
 * Portals must be entered from the side and must lead to a different dimension than the one entered from.
 * This class handles the registration and functionality of portal tracks for standard and modded portals.
 * </p>
 */
public class AllPortalTracks {
	/**
	 * Registers a portal track integration for a given block identified by its {@link ResourceLocation}, if it exists.
	 * If it does not, a warning will be logged.
	 *
	 * @param id    The resource location of the portal block.
	 * @param provider The portal track provider for the block.
	 */
	public static void tryRegisterIntegration(ResourceLocation id, PortalTrackProvider provider) {
		if (ForgeRegistries.BLOCKS.containsKey(id)) {
			Block block = ForgeRegistries.BLOCKS.getValue(id);
			PortalTrackProvider.REGISTRY.register(block, provider);
		} else {
			Create.LOGGER.warn("Portal for integration wasn't found: {}. Compat outdated?", id);
		}
	}

	// Built-in handlers

	/**
	 * Registers default portal track integrations for built-in dimensions and mods.
	 * This includes the Nether and the Aether (if loaded).
	 */
	public static void registerDefaults() {
		PortalTrackProvider.REGISTRY.register(Blocks.NETHER_PORTAL, AllPortalTracks::nether);

		if (Mods.AETHER.isLoaded()) {
			tryRegisterIntegration(Mods.AETHER.rl("aether_portal"), AllPortalTracks::aether);
		}

		if (Mods.BETTEREND.isLoaded()) {
			tryRegisterIntegration(Mods.BETTEREND.rl("end_portal_block"), AllPortalTracks::betterend);
		}
	}

	private static PortalTrackProvider.Exit nether(ServerLevel level, BlockFace face) {
		MinecraftServer minecraftServer = level.getServer();

		if (!minecraftServer.isNetherEnabled())
			return null;

		return PortalTrackProvider.fromTeleporter(level, face, Level.OVERWORLD, Level.NETHER, ServerLevel::getPortalForcer);
	}

	private static PortalTrackProvider.Exit aether(ServerLevel level, BlockFace face) {
		ResourceKey<Level> aetherLevelKey = ResourceKey.create(Registries.DIMENSION, Mods.AETHER.rl("the_aether"));
		return PortalTrackProvider.fromTeleporter(level, face, Level.OVERWORLD, aetherLevelKey, serverLevel -> {
			try {
				return (ITeleporter) Class.forName("com.aetherteam.aether.block.portal.AetherPortalForcer")
						.getDeclaredConstructor(ServerLevel.class, boolean.class)
						.newInstance(serverLevel, true);
			} catch (Exception e) {
				Create.LOGGER.error("Failed to create Aether teleporter: ", e);
			}
			return serverLevel.getPortalForcer();
		});
	}

	private static PortalTrackProvider.Exit betterend(ServerLevel level, BlockFace face) {
		return fromProbe(level, face, Level.OVERWORLD, Level.END, BetterEndPortalCompat::getBetterEndPortalInfo);
	}

	public static PortalTrackProvider.Exit fromTeleporter(
		ServerLevel level, BlockFace inboundTrack,
		ResourceKey<Level> firstDimension,
		ResourceKey<Level> secondDimension,
		Function<ServerLevel, ITeleporter> customPortalForcer
	) {
		return PortalTrackProvider.fromProbe(
			level, inboundTrack, firstDimension, secondDimension,
			(otherLevel, probe) -> {
				ITeleporter teleporter = customPortalForcer.apply(otherLevel);
				return teleporter.getPortalInfo(probe, otherLevel, probe::findDimensionEntryPoint);
			}
		);
	}

	public static PortalTrackProvider.Exit fromProbe(
		ServerLevel level, BlockFace inboundTrack,
		ResourceKey<Level> firstDimension,
		ResourceKey<Level> secondDimension,
		BiFunction<ServerLevel, SuperGlueEntity, PortalInfo> portalInfoProvider
	) {
		ResourceKey<Level> resourceKey = level.dimension() == secondDimension ? firstDimension : secondDimension;

		MinecraftServer minecraftServer = level.getServer();
		ServerLevel otherLevel = minecraftServer.getLevel(resourceKey);

		if (otherLevel == null)
			return null;

		BlockPos portalPos = inboundTrack.getConnectedPos();
		BlockState portalState = level.getBlockState(portalPos);

		SuperGlueEntity probe = new SuperGlueEntity(level, new AABB(portalPos));
		probe.setYRot(inboundTrack.getFace().toYRot());
		probe.setPortalEntrancePos();

		PortalInfo portalInfo = portalInfoProvider.apply(otherLevel, probe);
		if (portalInfo == null)
			return null;

		BlockPos otherPortalPos = BlockPos.containing(portalInfo.pos);
		BlockState otherPortalState = otherLevel.getBlockState(otherPortalPos);
		if (!otherPortalState.is(portalState.getBlock()))
			return null;

		Direction targetDirection = inboundTrack.getFace();
		if (targetDirection.getAxis() == otherPortalState.getValue(BlockStateProperties.HORIZONTAL_AXIS))
			targetDirection = targetDirection.getClockWise();
		BlockPos otherPos = otherPortalPos.relative(targetDirection);
		return new PortalTrackProvider.Exit(otherLevel, new BlockFace(otherPos, targetDirection.getOpposite()));
	}
}
