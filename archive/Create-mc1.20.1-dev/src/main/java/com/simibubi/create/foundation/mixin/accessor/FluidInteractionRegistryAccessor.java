package com.simibubi.create.foundation.mixin.accessor;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraftforge.fluids.FluidInteractionRegistry;
import net.minecraftforge.fluids.FluidInteractionRegistry.InteractionInformation;
import net.minecraftforge.fluids.FluidType;

@Mixin(FluidInteractionRegistry.class)
public interface FluidInteractionRegistryAccessor {
	@Accessor(value = "INTERACTIONS", remap = false)
	static Map<FluidType, List<InteractionInformation>> getInteractions() {
		throw new AssertionError();
	}
}
