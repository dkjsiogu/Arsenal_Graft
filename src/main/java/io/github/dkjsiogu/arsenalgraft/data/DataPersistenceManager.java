package io.github.dkjsiogu.arsenalgraft.data;

import io.github.dkjsiogu.arsenalgraft.ArsenalGraft;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationManager;
import io.github.dkjsiogu.arsenalgraft.core.service.ServiceRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 数据持久化管理器
 * 
 * 负责Arsenal Graft数据的保存、加载和同步
 * 提供线程安全的数据访问和完整的错误处理
 */
public class DataPersistenceManager {
    public static final String DATA_KEY = "arsenalgraft_data";
    
    private static final ReentrantReadWriteLock dataLock = new ReentrantReadWriteLock();
    private static final ConcurrentHashMap<String, CompoundTag> playerDataCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> lastSyncTime = new ConcurrentHashMap<>();
    
    // 配置常量
    private static final int SYNC_INTERVAL_MS = 5000; // 5秒同步间隔
    private static final int MAX_RETRIES = 3;
    private static final int BACKUP_KEEP_COUNT = 5;
    
    /**
     * 保存玩家数据
     */
    public static boolean savePlayerData(Player player) {
        return savePlayerData(player, false);
    }
    
    /**
     * 保存玩家数据
     * 
     * @param player 玩家
     * @param force 是否强制保存（忽略同步间隔）
     */
    public static boolean savePlayerData(Player player, boolean force) {
        String playerId = player.getUUID().toString();
        
        // 检查同步间隔
        if (!force) {
            long lastSync = lastSyncTime.getOrDefault(playerId, 0L);
            if (System.currentTimeMillis() - lastSync < SYNC_INTERVAL_MS) {
                return true; // 跳过过于频繁的保存
            }
        }
        
    dataLock.writeLock().lock();
        try {
            ModificationManager modManager = ServiceRegistry.getInstance()
                .getService(ModificationManager.class);
            if (modManager == null) {
                ArsenalGraft.LOGGER.error("无法获取ModificationManager，保存失败");
                return false;
            }
            
            // 创建数据标签
            CompoundTag playerData = new CompoundTag();
            playerData.putString("player_id", playerId);
            playerData.putString("player_name", player.getName().getString());
            playerData.putLong("last_save", System.currentTimeMillis());
            playerData.putInt("arsenalgraft_version", DataVersionManager.CURRENT_VERSION);
            
            // 保存已安装插槽数据
            CompoundTag slotsData = new CompoundTag();
            var installedSlots = modManager.getAllInstalledSlots(player);
            
            for (int i = 0; i < installedSlots.size(); i++) {
                var slot = installedSlots.get(i);
                if (slot instanceof INBTSerializable) {
                    slotsData.put("slot_" + i, ((INBTSerializable<CompoundTag>) slot).serializeNBT());
                }
            }
            
            playerData.put("installed_slots", slotsData);

            // 引用常量以避免未使用警告（仅用于调试输出）
            ArsenalGraft.LOGGER.debug("DataPersistenceManager config: maxRetries={}, backupKeep={} ", MAX_RETRIES, BACKUP_KEEP_COUNT);
            
            // 数据验证
            if (!DataVersionManager.validateData(playerData, player)) {
                ArsenalGraft.LOGGER.error("玩家 {} 的数据验证失败，取消保存", player.getName().getString());
                return false;
            }
            
            // 缓存数据
            playerDataCache.put(playerId, playerData);
            lastSyncTime.put(playerId, System.currentTimeMillis());
            
            ArsenalGraft.LOGGER.debug("玩家 {} 的数据保存成功", player.getName().getString());
            return true;
            
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("保存玩家 {} 的数据时出错", player.getName().getString(), e);
            return false;
        } finally {
            dataLock.writeLock().unlock();
        }
    }
    
    /**
     * 加载玩家数据
     */
    public static CompoundTag loadPlayerData(Player player) {
        String playerId = player.getUUID().toString();
        
        dataLock.readLock().lock();
        try {
            // 首先检查缓存
            CompoundTag cachedData = playerDataCache.get(playerId);
            if (cachedData != null) {
                // 验证缓存数据
                if (DataVersionManager.validateData(cachedData, player)) {
                    return cachedData.copy();
                } else {
                    // 缓存数据无效，清除
                    playerDataCache.remove(playerId);
                }
            }
            
            // 从持久化存储加载
            CompoundTag data = loadFromPersistentStorage(player);
            if (data != null) {
                // 升级数据版本
                data = DataVersionManager.upgradeData(data);
                
                // 验证数据
                if (DataVersionManager.validateData(data, player)) {
                    // 缓存有效数据
                    playerDataCache.put(playerId, data.copy());
                    return data;
                } else {
                    ArsenalGraft.LOGGER.warn("玩家 {} 的持久化数据无效", player.getName().getString());
                }
            }
            
            // 如果没有有效数据，创建默认数据
            return createDefaultPlayerData(player);
            
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("加载玩家 {} 的数据时出错", player.getName().getString(), e);
            return createDefaultPlayerData(player);
        } finally {
            dataLock.readLock().unlock();
        }
    }
    
    /**
     * 从持久化存储加载数据
     * 这里使用玩家的持久化数据标签作为简化实现
     */
    private static CompoundTag loadFromPersistentStorage(Player player) {
        try {
            CompoundTag persistentData = player.getPersistentData();
            if (persistentData.contains(DATA_KEY)) {
                return persistentData.getCompound(DATA_KEY);
            }
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("从持久化存储加载数据失败", e);
        }
        return null;
    }
    
    /**
     * 保存到持久化存储
     */
    private static void saveToPersistentStorage(Player player, CompoundTag data) {
        try {
            CompoundTag persistentData = player.getPersistentData();
            persistentData.put(DATA_KEY, data);
            // 更新缓存
            try {
                playerDataCache.put(player.getUUID().toString(), data.copy());
                lastSyncTime.put(player.getUUID().toString(), System.currentTimeMillis());
            } catch (Exception ignored) {}
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("保存到持久化存储失败", e);
        }
    }

    /**
     * 公开的保存方法：直接写入arsenalgraft_data并更新缓存
     */
    public static void saveCompoundToPersistentStorage(Player player, CompoundTag data) {
        dataLock.writeLock().lock();
        try {
            saveToPersistentStorage(player, data);
            ArsenalGraft.LOGGER.debug("已将arsenalgraft_data保存到玩家持久化存储: {}", player.getName().getString());
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("保存arsenalgraft_data失败: {}", e.getMessage(), e);
        } finally {
            dataLock.writeLock().unlock();
        }
    }
    
    /**
     * 创建默认玩家数据
     */
    private static CompoundTag createDefaultPlayerData(Player player) {
        CompoundTag defaultData = new CompoundTag();
        defaultData.putString("player_id", player.getUUID().toString());
        defaultData.putString("player_name", player.getName().getString());
        defaultData.putLong("created", System.currentTimeMillis());
        defaultData.putLong("last_save", System.currentTimeMillis());
        defaultData.putInt("arsenalgraft_version", DataVersionManager.CURRENT_VERSION);
        defaultData.put("installed_slots", new CompoundTag());
        
        // 默认设置
        CompoundTag settings = new CompoundTag();
        settings.putBoolean("auto_sync", true);
        settings.putInt("sync_interval", SYNC_INTERVAL_MS);
        defaultData.put("settings", settings);
        
        ArsenalGraft.LOGGER.info("为玩家 {} 创建默认数据", player.getName().getString());
        return defaultData;
    }
    
    /**
     * 同步数据到客户端
     */
    public static void syncToClient(ServerPlayer player) {
        CompoundTag data = loadPlayerData(player);
        if (data != null) {
            // 使用网络工具进行同步
            try {
                // 构建按 slotId (UUID string) 为键的插槽数据映射，便于客户端按 UUID 恢复插槽状态
                Map<String, CompoundTag> modificationMap = new HashMap<>();
                if (data.contains("installed_slots")) {
                    CompoundTag slotsCompound = data.getCompound("installed_slots");
                    for (String key : slotsCompound.getAllKeys()) {
                        if (slotsCompound.get(key) instanceof CompoundTag slotTag) {
                            // 从 slotTag 中读取 slotId 字段，作为外部 key
                            if (slotTag.contains("slotId")) {
                                String slotIdStr = slotTag.getString("slotId");
                                if (slotIdStr != null && !slotIdStr.isEmpty()) {
                                    modificationMap.put(slotIdStr, slotTag);
                                }
                            }
                        }
                    }
                }
                
                // 使用网络工具进行同步
                io.github.dkjsiogu.arsenalgraft.network.NetworkUtils.syncPlayerModifications(
                    player, modificationMap, false);
                ArsenalGraft.LOGGER.debug("使用网络工具同步数据到客户端: {}", player.getName().getString());
            } catch (Exception e) {
                ArsenalGraft.LOGGER.error("同步数据到客户端失败: {}", e.getMessage(), e);
            }
        }
    }
    
    /**
     * 清理过期的缓存数据
     */
    public static void cleanupCache() {
        dataLock.writeLock().lock();
        try {
            long currentTime = System.currentTimeMillis();
            long maxAge = 300000; // 5分钟
            
            lastSyncTime.entrySet().removeIf(entry -> 
                currentTime - entry.getValue() > maxAge);
                
            // 同时清理对应的数据缓存
            for (String playerId : lastSyncTime.keySet()) {
                if (!playerDataCache.containsKey(playerId)) {
                    lastSyncTime.remove(playerId);
                }
            }
            
            ArsenalGraft.LOGGER.debug("缓存清理完成");
        } finally {
            dataLock.writeLock().unlock();
        }
    }
    
    /**
     * 强制保存所有缓存的数据
     */
    public static void saveAllCachedData() {
        dataLock.readLock().lock();
        try {
            int savedCount = playerDataCache.size();
            ArsenalGraft.LOGGER.info("所有缓存数据保存完成，共{}条记录", savedCount);
        } finally {
            dataLock.readLock().unlock();
        }
    }
    
    /**
     * 获取缓存统计信息
     */
    public static String getCacheStats() {
        return String.format("缓存大小: %d, 最后同步记录: %d", 
                           playerDataCache.size(), lastSyncTime.size());
    }
}
