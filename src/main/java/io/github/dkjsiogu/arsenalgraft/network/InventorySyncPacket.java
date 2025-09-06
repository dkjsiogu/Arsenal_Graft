package io.github.dkjsiogu.arsenalgraft.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * 库存同步数据包
 * 
 * 用于同步玩家的额外库存数据
 */
public class InventorySyncPacket implements NetworkPacket {
    
    private final UUID playerId;
    private final CompoundTag inventoryData;
    private final long timestamp;
    
    public InventorySyncPacket(UUID playerId, CompoundTag inventoryData) {
        this.playerId = playerId;
        this.inventoryData = inventoryData;
        this.timestamp = System.currentTimeMillis();
    }
    
    public static InventorySyncPacket decode(FriendlyByteBuf buffer) {
        UUID playerId = buffer.readUUID();
        CompoundTag inventoryData = buffer.readNbt();
        return new InventorySyncPacket(playerId, inventoryData);
    }
    
    public static void encode(InventorySyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.playerId);
        buffer.writeNbt(packet.inventoryData);
    }
    
    public static void handle(InventorySyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // 处理客户端库存同步
            try {
                // 使用DistExecutor确保只在客户端执行
                net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT, () -> () -> {
                    io.github.dkjsiogu.arsenalgraft.client.ClientEventHandler.updateClientInventory(
                        packet.playerId, packet.inventoryData);
                });
            } catch (Exception e) {
                System.err.println("客户端库存更新失败: " + e.getMessage());
            }
        });
        context.setPacketHandled(true);
    }
    
    @Override
    public boolean validate(ServerPlayer sender) {
        return playerId != null && inventoryData != null;
    }
    
    @Override
    public int getMaxSize() {
        return 16384; // 16KB用于库存数据
    }
    
    @Override
    public long getMinInterval() {
        return 200; // 库存同步不需要太频繁
    }
    
    public UUID getPlayerId() { return playerId; }
    public CompoundTag getInventoryData() { return inventoryData; }
}
