package io.github.dkjsiogu.arsenalgraft.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 同步单个 hand 改造库存所有槽位到客户端 (全量)。
 * 增量可后续扩展 (slotIndex + stack)。
 */
public class HandInventorySyncPacket implements NetworkPacket {
    private final UUID slotId;
    private final List<ItemStack> stacks;

    public HandInventorySyncPacket(UUID slotId, List<ItemStack> stacks) {
        this.slotId = slotId; this.stacks = stacks; }

    public static HandInventorySyncPacket decode(FriendlyByteBuf buf) {
        UUID id = buf.readUUID();
        int size = buf.readVarInt();
        List<ItemStack> list = new ArrayList<>(size);
        for (int i=0;i<size;i++) list.add(buf.readItem());
        return new HandInventorySyncPacket(id, list);
    }

    public static void encode(HandInventorySyncPacket pkt, FriendlyByteBuf buf) {
        buf.writeUUID(pkt.slotId);
        buf.writeVarInt(pkt.stacks.size());
        for (ItemStack s : pkt.stacks) buf.writeItem(s);
    }

    public static void handle(HandInventorySyncPacket pkt, Supplier<NetworkEvent.Context> ctxSup) {
        var ctx = ctxSup.get();
        ctx.enqueueWork(() -> {
            io.github.dkjsiogu.arsenalgraft.client.ClientHandInventoryCache.applyServerSnapshot(pkt.slotId, pkt.stacks);
            // 强制当前打开菜单重绘 (若存在)
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            var player = mc.player;
            if (player == null) return;
            if (player.containerMenu instanceof io.github.dkjsiogu.arsenalgraft.menu.impl.HandInventoryMenu menu) {
                if (menu.getSlotId().equals(pkt.slotId)) {
                    // 当前使用 IItemHandler 直读, 数据已在缓存; 若后续改为缓存驱动, 这里可调用屏幕自定义刷新方法
                }
            }
        });
        ctx.setPacketHandled(true);
    }

    @Override public boolean validate(net.minecraft.server.level.ServerPlayer sender) { return slotId != null; }
    @Override public int getMaxSize() { return 8192; }
    @Override public long getMinInterval() { return 50; }
    @Override public int getPriority() { return 1; }
    @Override public boolean requiresAck() { return false; }
}
