package io.github.dkjsiogu.arsenalgraft.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 配置同步数据包
 * 
 * 用于同步服务器配置到客户端
 */
public class ConfigSyncPacket implements NetworkPacket {
    
    private final CompoundTag configData;
    
    public ConfigSyncPacket(CompoundTag configData) {
        this.configData = configData;
    }
    
    public static ConfigSyncPacket decode(FriendlyByteBuf buffer) {
        CompoundTag configData = buffer.readNbt();
        return new ConfigSyncPacket(configData);
    }
    
    public static void encode(ConfigSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeNbt(packet.configData);
    }
    
    public static void handle(ConfigSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // 处理客户端配置同步
            try {
                // 使用DistExecutor确保只在客户端执行
                net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT, () -> () -> {
                    io.github.dkjsiogu.arsenalgraft.client.ClientEventHandler.updateClientConfig(packet.configData);
                });
            } catch (Exception e) {
                System.err.println("客户端配置更新失败: " + e.getMessage());
            }
        });
        context.setPacketHandled(true);
    }
    
    @Override
    public boolean validate(ServerPlayer sender) {
        // 服务端发送，不需要验证sender
        return configData != null;
    }
    
    @Override
    public int getMaxSize() {
        return 4096; // 4KB用于配置数据
    }
    
    @Override
    public long getMinInterval() {
        return 5000; // 配置同步很少需要
    }
    
    public CompoundTag getConfigData() { return configData; }
}
