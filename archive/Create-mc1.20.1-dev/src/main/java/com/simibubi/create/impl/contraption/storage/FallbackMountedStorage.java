package com.simibubi.create.impl.contraption.storage;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.simibubi.create.AllMountedStorageTypes;
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorage;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

/**
 * A fallback mounted storage impl that will try to be used when no type is
 * registered for a block. This requires that the mounted block provide an item handler
 * whose class is exactly {@link ItemStackHandler}.
 */
public class FallbackMountedStorage extends SimpleMountedStorage {
	public static final Codec<FallbackMountedStorage> CODEC = SimpleMountedStorage.codec(FallbackMountedStorage::new);

	public FallbackMountedStorage(IItemHandler handler) {
		super(AllMountedStorageTypes.FALLBACK.get(), handler);
	}

	@Override
	protected Optional<IItemHandlerModifiable> validate(IItemHandler handler) {
		return super.validate(handler).filter(FallbackMountedStorage::isValid);
	}

	public static boolean isValid(IItemHandler handler) {
		return handler.getClass() == ItemStackHandler.class;
	}
}
