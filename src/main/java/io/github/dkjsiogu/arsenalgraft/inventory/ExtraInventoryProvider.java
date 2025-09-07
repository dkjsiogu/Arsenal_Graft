package io.github.dkjsiogu.arsenalgraft.inventory;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import javax.annotation.Nullable;

/** Capability Provider & 静态访问器 */
public class ExtraInventoryProvider implements ICapabilityProvider {
    public static final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath("arsenalgraft","extra_inventory");

    public static final Capability<IExtraInventory> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private final ExtraInventory backend = new ExtraInventory();
    private final LazyOptional<IExtraInventory> opt = LazyOptional.of(() -> backend);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return cap == CAPABILITY ? opt.cast() : LazyOptional.empty();
    }

    public CompoundTag serializeNBT() { return backend.serializeNBT(); }
    public void deserializeNBT(CompoundTag tag) { backend.deserializeNBT(tag); }
}
