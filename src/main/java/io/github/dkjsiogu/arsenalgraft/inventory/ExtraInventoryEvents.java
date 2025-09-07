package io.github.dkjsiogu.arsenalgraft.inventory;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.nbt.CompoundTag;

/**
 * 事件：附加能力与玩家克隆。
 */
@Mod.EventBusSubscriber(modid = "arsenalgraft", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class ExtraInventoryEvents {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(ExtraInventoryProvider.KEY, new net.minecraftforge.common.capabilities.ICapabilityProvider() {
                final ExtraInventoryProvider provider = new ExtraInventoryProvider();
                @Override
                public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, net.minecraft.core.Direction side) {
                    return provider.getCapability(cap, side);
                }
                public net.minecraft.nbt.CompoundTag serializeNBT() { return provider.serializeNBT(); }
                public void deserializeNBT(net.minecraft.nbt.CompoundTag nbt) { provider.deserializeNBT(nbt); }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return; // 死亡时复制
        Player oldP = event.getOriginal();
        Player newP = event.getEntity();
        oldP.reviveCaps();
        oldP.getCapability(ExtraInventoryProvider.CAPABILITY).ifPresent(oldInv -> {
            newP.getCapability(ExtraInventoryProvider.CAPABILITY).ifPresent(newInv -> {
                if (oldInv instanceof ExtraInventory oi && newInv instanceof ExtraInventory ni) {
                    // 通过 NBT 复制
                    CompoundTag tag = oi.serializeNBT();
                    ni.deserializeNBT(tag);
                }
            });
        });
        oldP.invalidateCaps();
    }
}
