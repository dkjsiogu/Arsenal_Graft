package io.github.dkjsiogu.arsenalgraft.network;

import io.github.dkjsiogu.arsenalgraft.ArsenalGraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * Arsenal Graft 网络工具类
 * 
 * 提供便捷的网络通信方法，简化网络包的创建和发送
 */
public class NetworkUtils {
    
    // 防止频繁发送的缓存
    private static final Map<String, Long> lastSentTime = new HashMap<>();
    private static final long MIN_SYNC_INTERVAL = 1000; // 1秒最少间隔
    
    /**
     * 同步玩家修改数据到客户端
     * 
     * @param player 目标玩家
     * @param modificationData 修改数据
     * @param forceSync 是否强制同步（忽略频率限制）
     */
    public static void syncPlayerModifications(ServerPlayer player, Map<String, CompoundTag> modificationData, boolean forceSync) {
        if (player == null || modificationData == null || modificationData.isEmpty()) {
            return;
        }
        
        String cacheKey = player.getUUID().toString() + "_modifications";
        long currentTime = System.currentTimeMillis();
        
        // 检查频率限制
        if (!forceSync) {
            Long lastSent = lastSentTime.get(cacheKey);
            if (lastSent != null && (currentTime - lastSent) < MIN_SYNC_INTERVAL) {
                return; // 太频繁，跳过此次同步
            }
        }
        
        try {
            ModificationSyncPacket packet = new ModificationSyncPacket(player.getUUID(), modificationData);
            NetworkHandler.sendToPlayer(packet, player);
            
            lastSentTime.put(cacheKey, currentTime);
            ArsenalGraft.LOGGER.debug("同步修改数据到客户端: {}, 条目数: {}", 
                player.getName().getString(), modificationData.size());
                
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("同步修改数据失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 同步单个修改到客户端
     * 
     * @param player 目标玩家
     * @param modificationId 修改ID
     * @param data 修改数据
     */
    public static void syncSingleModification(ServerPlayer player, String modificationId, CompoundTag data) {
        if (player == null || modificationId == null || data == null) {
            return;
        }
        
        try {
            ModificationSyncPacket packet = new ModificationSyncPacket(player.getUUID(), modificationId, data);
            NetworkHandler.sendToPlayer(packet, player);
            
            ArsenalGraft.LOGGER.debug("同步单个修改到客户端: {} -> {}", 
                player.getName().getString(), modificationId);
                
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("同步单个修改失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 同步库存数据到客户端
     * 
     * @param player 目标玩家
     * @param inventoryData 库存数据
     */
    public static void syncInventory(ServerPlayer player, CompoundTag inventoryData) {
        if (player == null || inventoryData == null) {
            return;
        }
        
        try {
            InventorySyncPacket packet = new InventorySyncPacket(player.getUUID(), inventoryData);
            NetworkHandler.sendToPlayer(packet, player);
            
            ArsenalGraft.LOGGER.debug("同步库存到客户端: {}", player.getName().getString());
            
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("同步库存失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 向多个玩家广播修改同步
     * 
     * @param players 目标玩家列表
     * @param sourcePlayer 源玩家（其修改发生了变化）
     * @param modificationData 修改数据
     */
    public static void broadcastModificationSync(List<ServerPlayer> players, ServerPlayer sourcePlayer, Map<String, CompoundTag> modificationData) {
        if (players == null || players.isEmpty() || sourcePlayer == null || modificationData == null) {
            return;
        }
        
        try {
            ModificationSyncPacket packet = new ModificationSyncPacket(sourcePlayer.getUUID(), modificationData);
            
            for (ServerPlayer player : players) {
                if (player != sourcePlayer) { // 不要发给源玩家自己
                    NetworkHandler.sendToPlayer(packet, player);
                }
            }
            
            ArsenalGraft.LOGGER.debug("广播修改同步: {} -> {} 玩家", 
                sourcePlayer.getName().getString(), players.size());
                
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("广播修改同步失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 向附近玩家同步修改
     * 
     * @param sourcePlayer 源玩家
     * @param modificationData 修改数据
     * @param range 同步范围
     */
    public static void syncToNearbyPlayers(ServerPlayer sourcePlayer, Map<String, CompoundTag> modificationData, double range) {
        if (sourcePlayer == null || modificationData == null) {
            return;
        }
        
        try {
            ModificationSyncPacket packet = new ModificationSyncPacket(sourcePlayer.getUUID(), modificationData);
            NetworkHandler.sendToNearbyPlayers(packet, sourcePlayer, range);
            
            ArsenalGraft.LOGGER.debug("向附近玩家同步修改: {}, 范围: {}", 
                sourcePlayer.getName().getString(), range);
                
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("向附近玩家同步修改失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 发送配置同步到客户端
     * 
     * @param player 目标玩家
     * @param configData 配置数据
     */
    public static void syncConfig(ServerPlayer player, CompoundTag configData) {
        if (player == null || configData == null) {
            return;
        }
        
        try {
            ConfigSyncPacket packet = new ConfigSyncPacket(configData);
            NetworkHandler.sendToPlayer(packet, player);
            
            ArsenalGraft.LOGGER.debug("同步配置到客户端: {}", player.getName().getString());
            
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("同步配置失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 向所有在线玩家广播配置更新
     * 
     * @param configData 配置数据
     */
    public static void broadcastConfigUpdate(CompoundTag configData) {
        if (configData == null) {
            return;
        }
        
        try {
            ConfigSyncPacket packet = new ConfigSyncPacket(configData);
            NetworkHandler.sendToAllClients(packet);
            
            ArsenalGraft.LOGGER.info("向所有客户端广播配置更新");
            
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("广播配置更新失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 清理网络缓存
     * 应该在服务器关闭或玩家退出时调用
     */
    public static void cleanupCache() {
        long currentTime = System.currentTimeMillis();
        
        // 清理超过5分钟的缓存项
        lastSentTime.entrySet().removeIf(entry -> 
            (currentTime - entry.getValue()) > 300000);
            
        ArsenalGraft.LOGGER.debug("网络缓存清理完成，剩余: {} 项", lastSentTime.size());
    }
    
    /**
     * 获取网络统计信息
     * 
     * @return 网络统计信息
     */
    public static NetworkStats getNetworkStats() {
        return new NetworkStats(lastSentTime.size());
    }
    
    /**
     * 网络统计信息类
     */
    public static class NetworkStats {
        private final int cachedEntries;
        private final long timestamp;
        
        public NetworkStats(int cachedEntries) {
            this.cachedEntries = cachedEntries;
            this.timestamp = System.currentTimeMillis();
        }
        
        public int getCachedEntries() {
            return cachedEntries;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        @Override
        public String toString() {
            return String.format("NetworkStats{cached=%d, time=%d}", cachedEntries, timestamp);
        }
    }
}
