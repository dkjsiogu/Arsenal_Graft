package io.github.dkjsiogu.arsenalgraft.network;

import io.github.dkjsiogu.arsenalgraft.ArsenalGraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Arsenal Graft 3.0 网络处理器
 * 
 * 提供完整的客户端-服务端通信机制，包括：
 * - 数据包注册和路由
 * - 序列化/反序列化
 * - 网络延迟处理
 * - 数据包验证
 */
public class NetworkHandler {
    
    private static final String PROTOCOL_VERSION = "3.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        ResourceLocation.fromNamespaceAndPath(ArsenalGraft.MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );
    
    private static int nextId = 0;
    
    /**
     * 注册所有网络包
     */
    public static void register() {
        ArsenalGraft.LOGGER.info("注册Arsenal Graft 3.0网络系统...");
        
        // 修改同步包
        INSTANCE.messageBuilder(ModificationSyncPacket.class, nextId++, NetworkDirection.PLAY_TO_CLIENT)
            .decoder(ModificationSyncPacket::decode)
            .encoder(ModificationSyncPacket::encode)
            .consumerMainThread(ModificationSyncPacket::handle)
            .add();
            
        // 组件更新包
        INSTANCE.messageBuilder(ComponentUpdatePacket.class, nextId++, NetworkDirection.PLAY_TO_SERVER)
            .decoder(ComponentUpdatePacket::decode)
            .encoder(ComponentUpdatePacket::encode)
            .consumerMainThread(ComponentUpdatePacket::handle)
            .add();
            
        // 库存同步包
        INSTANCE.messageBuilder(InventorySyncPacket.class, nextId++, NetworkDirection.PLAY_TO_CLIENT)
            .decoder(InventorySyncPacket::decode)
            .encoder(InventorySyncPacket::encode)
            .consumerMainThread(InventorySyncPacket::handle)
            .add();
            
        // 配置同步包
        INSTANCE.messageBuilder(ConfigSyncPacket.class, nextId++, NetworkDirection.PLAY_TO_CLIENT)
            .decoder(ConfigSyncPacket::decode)
            .encoder(ConfigSyncPacket::encode)
            .consumerMainThread(ConfigSyncPacket::handle)
            .add();
            
        // 技能激活包
        INSTANCE.messageBuilder(SkillActivationPacket.class, nextId++, NetworkDirection.PLAY_TO_SERVER)
            .decoder(SkillActivationPacket::decode)
            .encoder(SkillActivationPacket::encode)
            .consumerMainThread(SkillActivationPacket::handle)
            .add();
            
        ArsenalGraft.LOGGER.info("Arsenal Graft 3.0网络系统注册完成，已注册{}个数据包", nextId);
    }
    
    /**
     * 发送包到服务器
     */
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
    
    /**
     * 发送包到客户端
     */
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
    
    /**
     * 发送包到所有客户端
     */
    public static <MSG> void sendToAllClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
    
    /**
     * 发送包到指定维度的所有玩家
     */
    public static <MSG> void sendToDimension(MSG message, net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension) {
        INSTANCE.send(PacketDistributor.DIMENSION.with(() -> dimension), message);
    }
    
    /**
     * 发送包到指定范围内的玩家
     */
    public static <MSG> void sendToNearbyPlayers(MSG message, ServerPlayer centerPlayer, double range) {
        INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
            centerPlayer.getX(), centerPlayer.getY(), centerPlayer.getZ(), range, centerPlayer.level().dimension()
        )), message);
    }
    
    /**
     * 验证网络包的完整性
     */
    public static boolean validatePacket(Object packet, ServerPlayer sender) {
        if (packet == null || sender == null) {
            return false;
        }
        
        // 基础验证：检查玩家是否在线
        if (!sender.isAlive() || sender.hasDisconnected()) {
            return false;
        }
        
        // 检查数据包大小限制
        if (packet instanceof NetworkPacket networkPacket) {
            return networkPacket.validate(sender);
        }
        
        return true;
    }
    
    /**
     * 处理网络延迟补偿
     */
    public static void handleLatencyCompensation(ServerPlayer player, long clientTimestamp) {
        long serverTime = System.currentTimeMillis();
        long latency = serverTime - clientTimestamp;
        
        // 存储延迟信息用于后续计算
        if (latency > 0 && latency < 5000) { // 合理的延迟范围
            player.getPersistentData().putLong("arsenalgraft.latency", latency);
        }
    }
    
    /**
     * 获取玩家的网络延迟
     */
    public static long getPlayerLatency(ServerPlayer player) {
        return player.getPersistentData().getLong("arsenalgraft.latency");
    }
}
