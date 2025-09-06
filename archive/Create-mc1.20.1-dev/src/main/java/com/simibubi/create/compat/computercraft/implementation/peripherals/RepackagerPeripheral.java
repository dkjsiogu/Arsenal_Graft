package com.simibubi.create.compat.computercraft.implementation.peripherals;

import java.util.Optional;
import java.util.Map;

import com.simibubi.create.content.logistics.packager.repackager.RepackagerBlockEntity;
import com.simibubi.create.compat.computercraft.implementation.luaObjects.PackageLuaObject;
import com.simibubi.create.compat.computercraft.implementation.ComputerUtil;

import dan200.computercraft.api.peripheral.IComputerAccess;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaException;

public class RepackagerPeripheral extends SyncedPeripheral<RepackagerBlockEntity> {

	public RepackagerPeripheral(RepackagerBlockEntity blockEntity) {
		super(blockEntity);
	}

	@Override
	public void attach(@NotNull IComputerAccess computer) {
		super.attach(computer);
		// Ephemeral nature of address, should not be set on load until a computer
		// explicitly calls setAddress again on the BE.
		blockEntity.hasCustomComputerAddress = false;
	}

	@Override
	public void detach(@NotNull IComputerAccess computer) {
		super.detach(computer);
		// Ephemeral nature of address, should not be set on load until a computer
		// explicitly calls setAddress again on the BE.
		blockEntity.hasCustomComputerAddress = false;
	}

	@LuaFunction(mainThread = true)
	public final boolean makePackage() {
		if (!blockEntity.heldBox.isEmpty())
			return false;
		blockEntity.activate();
		if (blockEntity.heldBox.isEmpty())
			return false;
		return true;
	}

	@LuaFunction(mainThread = true)
	public Map<Integer, Map<String, ?>> list() {
		return ComputerUtil.list(blockEntity.targetInventory.getInventory());
	}

	@LuaFunction(mainThread = true)
	public Map<String, ?> getItemDetail(int slot) throws LuaException {
		return ComputerUtil.getItemDetail(blockEntity.targetInventory.getInventory(), slot);
	}

	@LuaFunction(mainThread = true)
	public final String getAddress() {
		blockEntity.updateSignAddress();
		return blockEntity.signBasedAddress;
	}

	@LuaFunction(mainThread = true)
	public final void setAddress(Optional<String> argument) {
		if (argument.isPresent()) {
			blockEntity.customComputerAddress = argument.get();
			blockEntity.signBasedAddress = argument.get();
			blockEntity.hasCustomComputerAddress = true;
		} else {
			blockEntity.customComputerAddress = "";
			blockEntity.hasCustomComputerAddress = false;
		}
	}

	@LuaFunction(mainThread = true)
	public final PackageLuaObject getPackage() {
		ItemStack box = blockEntity.heldBox;
		if (box.isEmpty())
			return null;

		return new PackageLuaObject(blockEntity, box);
	}

	@NotNull
	@Override
	public String getType() {
		return "Create_Repackager";
	}

}
