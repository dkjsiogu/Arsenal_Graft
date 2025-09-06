package com.simibubi.create.api.contraption.storage.item.chest;

import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorage;
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorageType;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ChestMountedStorageType extends SimpleMountedStorageType<ChestMountedStorage> {
	public ChestMountedStorageType() {
		super(ChestMountedStorage.CODEC);
	}

	@Override
	protected IItemHandler getHandler(BlockEntity be) {
		return be instanceof Container container ? new InvWrapper(container) : null;
	}

	@Override
	protected SimpleMountedStorage createStorage(IItemHandler handler) {
		return new ChestMountedStorage(handler);
	}
}
