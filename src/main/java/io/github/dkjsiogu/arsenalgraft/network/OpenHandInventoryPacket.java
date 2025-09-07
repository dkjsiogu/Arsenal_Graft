package io.github.dkjsiogu.arsenalgraft.network;

import io.github.dkjsiogu.arsenalgraft.api.v3.modification.InstalledSlot;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationManager;
import io.github.dkjsiogu.arsenalgraft.core.service.ServiceRegistry;
import io.github.dkjsiogu.arsenalgraft.menu.impl.HandInventoryMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

/**
 * 客户端请求打开手部改造库存的包
 */
public class OpenHandInventoryPacket implements NetworkPacket {

    private final UUID slotId;

    public OpenHandInventoryPacket(UUID slotId) { this.slotId = slotId; }

    public static OpenHandInventoryPacket decode(FriendlyByteBuf buf) { return new OpenHandInventoryPacket(buf.readUUID()); }
    public static void encode(OpenHandInventoryPacket pkt, FriendlyByteBuf buf) { buf.writeUUID(pkt.slotId); }

    public static void handle(OpenHandInventoryPacket pkt, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ServerPlayer sender = ctx.getSender();
        if (sender == null) { ctx.setPacketHandled(true); return; }
        ctx.enqueueWork(() -> {
            ModificationManager manager = ServiceRegistry.getInstance().getService(ModificationManager.class);
            if (manager == null) return;
            Optional<InstalledSlot> opt = manager.getAllInstalledSlots(sender).stream()
                    .filter(s -> s.getSlotId().equals(pkt.slotId)).findFirst();
            if (opt.isEmpty()) return;
            InstalledSlot slot = opt.get();
            MenuProvider provider = new MenuProvider() {
                @Override public Component getDisplayName() { return Component.literal(slot.getTemplate().getDisplayName().getString()); }
                @Override public AbstractContainerMenu createMenu(int windowId, @Nonnull Inventory inv, @Nonnull Player player) { return new HandInventoryMenu(windowId, inv, slot); }
            };
            NetworkHooks.openScreen(sender, provider, buf -> buf.writeUUID(slot.getSlotId()));
        });
        ctx.setPacketHandled(true);
    }

    @Override
    public boolean validate(ServerPlayer sender) { return slotId != null; }
    @Override
    public int getMaxSize() { return 64; }
    @Override
    public long getMinInterval() { return 50; }
    @Override
    public int getPriority() { return 1; }
    @Override
    public boolean requiresAck() { return false; }
}
