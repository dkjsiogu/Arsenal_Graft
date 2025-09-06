package io.github.dkjsiogu.arsenalgraft.network;

import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationManager;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.InstalledSlot;
import io.github.dkjsiogu.arsenalgraft.core.service.ServiceRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.*;
import java.util.function.Supplier;

/**
 * 修改同步数据包
 * 
 * 用于同步玩家的所有修改到客户端
 * 支持增量更新和完整同步
 */
public class ModificationSyncPacket implements NetworkPacket {
    
    private final UUID playerId;
    private final Map<String, CompoundTag> modificationData;
    private final boolean fullSync;
    private final long timestamp;
    
    /**
     * 完整同步构造函数
     */
    public ModificationSyncPacket(UUID playerId, Map<String, CompoundTag> modificationData) {
        this.playerId = playerId;
        this.modificationData = new HashMap<>(modificationData);
        this.fullSync = true;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 增量更新构造函数
     */
    public ModificationSyncPacket(UUID playerId, String modificationId, CompoundTag data) {
        this.playerId = playerId;
        this.modificationData = new HashMap<>();
        this.modificationData.put(modificationId, data);
        this.fullSync = false;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 从网络缓冲区解码
     */
    public static ModificationSyncPacket decode(FriendlyByteBuf buffer) {
        UUID playerId = buffer.readUUID();
        boolean fullSync = buffer.readBoolean();
        long timestamp = buffer.readLong();
        
        int count = buffer.readVarInt();
        Map<String, CompoundTag> modificationData = new HashMap<>();
        
        for (int i = 0; i < count; i++) {
            String modId = buffer.readUtf();
            CompoundTag data = buffer.readNbt();
            modificationData.put(modId, data);
        }
        
        ModificationSyncPacket packet = new ModificationSyncPacket(playerId, modificationData);
        // 保持原有的fullSync和timestamp状态
        return packet;
    }
    
    /**
     * 编码到网络缓冲区
     */
    public static void encode(ModificationSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.playerId);
        buffer.writeBoolean(packet.fullSync);
        buffer.writeLong(packet.timestamp);
        
        buffer.writeVarInt(packet.modificationData.size());
        for (Map.Entry<String, CompoundTag> entry : packet.modificationData.entrySet()) {
            buffer.writeUtf(entry.getKey());
            buffer.writeNbt(entry.getValue());
        }
    }
    
    /**
     * 处理数据包
     */
    public static void handle(ModificationSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // 确保在客户端执行
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                handleClientSide(packet);
            });
        });
        context.setPacketHandled(true);
    }
    
    /**
     * 客户端处理逻辑
     */
    private static void handleClientSide(ModificationSyncPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        
        Player player = mc.level.getPlayerByUUID(packet.playerId);
        if (player == null) return;
        
        ModificationManager modManager = ServiceRegistry.getInstance()
            .getService(ModificationManager.class);
        if (modManager == null) return;
        
        // 处理同步数据
        for (Map.Entry<String, CompoundTag> entry : packet.modificationData.entrySet()) {
            String slotIdStr = entry.getKey();
            CompoundTag data = entry.getValue();
            
            try {
                UUID slotId = UUID.fromString(slotIdStr);
                
                // 查找对应的插槽
                Optional<InstalledSlot> slotOpt = modManager.getAllInstalledSlots(player)
                    .stream()
                    .filter(slot -> slot.getSlotId().equals(slotId))
                    .findFirst();
                    
                if (slotOpt.isPresent()) {
                    // 更新插槽数据
                    slotOpt.get().deserializeNBT(data);
                } else if (packet.fullSync) {
                    // 如果是完整同步且找不到插槽，可能需要创建新的插槽
                    // 这里可以添加创建逻辑
                }
            } catch (IllegalArgumentException e) {
                // 忽略无效的UUID字符串
                continue;
            }
        }
        
        // 触发客户端更新事件
        try {
            // 使用DistExecutor确保只在客户端执行
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                io.github.dkjsiogu.arsenalgraft.client.ClientEventHandler.updateClientPlayerData(
                    packet.playerId, packet.modificationData, true);
            });
        } catch (Exception e) {
            // 日志记录错误但不中断处理
            System.err.println("客户端UI更新失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean validate(ServerPlayer sender) {
        // 服务端发送的包，不需要验证发送者
        if (sender == null) return true;
        
        // 基础验证
        if (playerId == null || modificationData == null) {
            return false;
        }
        
        // 检查数据大小
        int totalSize = 0;
        for (CompoundTag tag : modificationData.values()) {
            totalSize += tag.toString().length(); // 粗略估算
        }
        
        return totalSize <= getMaxSize();
    }
    
    @Override
    public int getMaxSize() {
        return 65536; // 64KB，允许较大的修改数据
    }
    
    @Override
    public long getMinInterval() {
        return fullSync ? 1000 : 100; // 完整同步1秒一次，增量更新100ms一次
    }
    
    @Override
    public int getPriority() {
        return fullSync ? 5 : 3; // 完整同步优先级更高
    }
    
    @Override
    public boolean requiresAck() {
        return fullSync; // 完整同步需要确认
    }
    
    // Getters
    public UUID getPlayerId() { return playerId; }
    public Map<String, CompoundTag> getModificationData() { return modificationData; }
    public boolean isFullSync() { return fullSync; }
    public long getTimestamp() { return timestamp; }
}
