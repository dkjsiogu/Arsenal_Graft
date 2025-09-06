package io.github.dkjsiogu.arsenalgraft.network;

import io.github.dkjsiogu.arsenalgraft.api.v3.component.IModificationComponent;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.InstalledSlot;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationManager;
import io.github.dkjsiogu.arsenalgraft.core.service.ServiceRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 组件更新数据包
 * 
 * 客户端向服务端发送组件状态更新
 * 例如：技能激活、属性调整等
 */
public class ComponentUpdatePacket implements NetworkPacket {
    
    private final UUID slotId;
    private final String componentType;
    private final CompoundTag updateData;
    private final long timestamp;
    
    public ComponentUpdatePacket(UUID slotId, String componentType, CompoundTag updateData) {
        this.slotId = slotId;
        this.componentType = componentType;
        this.updateData = updateData;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 从网络缓冲区解码
     */
    public static ComponentUpdatePacket decode(FriendlyByteBuf buffer) {
        UUID slotId = buffer.readUUID();
        String componentType = buffer.readUtf();
        CompoundTag updateData = buffer.readNbt();
        
        return new ComponentUpdatePacket(slotId, componentType, updateData);
    }
    
    /**
     * 编码到网络缓冲区
     */
    public static void encode(ComponentUpdatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.slotId);
        buffer.writeUtf(packet.componentType);
        buffer.writeNbt(packet.updateData);
    }
    
    /**
     * 处理数据包
     */
    public static void handle(ComponentUpdatePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender != null && NetworkHandler.validatePacket(packet, sender)) {
                handleServerSide(packet, sender);
            }
        });
        context.setPacketHandled(true);
    }
    
    /**
     * 服务端处理逻辑
     */
    private static void handleServerSide(ComponentUpdatePacket packet, ServerPlayer player) {
        ModificationManager modManager = ServiceRegistry.getInstance()
            .getService(ModificationManager.class);
        if (modManager == null) return;
        
        // 查找对应的插槽
        Optional<InstalledSlot> slotOpt = modManager.getAllInstalledSlots(player)
            .stream()
            .filter(slot -> slot.getSlotId().equals(packet.slotId))
            .findFirst();
            
        if (!slotOpt.isPresent()) {
            return; // 插槽不存在
        }
        
        InstalledSlot slot = slotOpt.get();
        IModificationComponent component = slot.getComponent(packet.componentType, IModificationComponent.class);
        
        if (component == null) {
            return; // 组件不存在
        }
        
        // 应用更新数据
        try {
            component.deserializeNBT(packet.updateData);
            
            // 触发组件更新（通过tick方法）
            component.tick(player);
            
            // 同步到其他客户端（简化版本，总是同步）
            ModificationSyncPacket syncPacket = new ModificationSyncPacket(
                player.getUUID(),
                packet.slotId.toString(),
                slot.serializeNBT()
            );
            NetworkHandler.sendToNearbyPlayers(syncPacket, player, 32.0);
            
        } catch (Exception e) {
            // 记录错误但不崩溃
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean validate(ServerPlayer sender) {
        if (sender == null) return false;
        
        // 验证必要字段
        if (slotId == null || componentType == null || updateData == null) {
            return false;
        }
        
        // 验证组件类型是否有效
        if (componentType.isEmpty() || componentType.length() > 64) {
            return false;
        }
        
        // 验证数据大小
        return updateData.toString().length() <= getMaxSize();
    }
    
    @Override
    public int getMaxSize() {
        return 8192; // 8KB，组件更新数据不应该太大
    }
    
    @Override
    public long getMinInterval() {
        return 50; // 50ms间隔，允许较频繁的更新
    }
    
    @Override
    public int getPriority() {
        return 2; // 中等优先级
    }
    
    @Override
    public boolean allowOfflineProcessing() {
        return false; // 不允许离线处理
    }
    
    // Getters
    public UUID getSlotId() { return slotId; }
    public String getComponentType() { return componentType; }
    public CompoundTag getUpdateData() { return updateData; }
    public long getTimestamp() { return timestamp; }
}
