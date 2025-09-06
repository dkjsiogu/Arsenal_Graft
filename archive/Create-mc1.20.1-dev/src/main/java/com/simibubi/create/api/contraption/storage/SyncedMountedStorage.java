package com.simibubi.create.api.contraption.storage;

import com.simibubi.create.content.contraptions.Contraption;

import net.minecraft.core.BlockPos;

/**
 * Optional interface for mounted storage that is synced with the client.
 */
public interface SyncedMountedStorage {
	/**
	 * @return true if this storage needs to be synced.
	 */
	boolean isDirty();

	/**
	 * Called after this storage has been synced.
	 */
	void markClean();

	/**
	 * Called on the client side after this storage has been synced from the server.
	 */
	void afterSync(Contraption contraption, BlockPos localPos);
}
