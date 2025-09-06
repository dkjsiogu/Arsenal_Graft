package io.github.dkjsiogu.arsenalgraft.data;

import io.github.dkjsiogu.arsenalgraft.ArsenalGraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 数据版本管理器
 * 
 * 负责处理数据格式的版本迁移和兼容性
 * 确保旧版本的存档数据能够正确升级到新版本
 */
public class DataVersionManager {
    
    public static final int CURRENT_VERSION = 3; // Arsenal Graft 3.0
    
    private static final List<DataMigrator> migrators = new ArrayList<>();
    
    static {
        // 注册数据迁移器
        registerMigrator(1, 2, DataVersionManager::migrateV1ToV2);
        registerMigrator(2, 3, DataVersionManager::migrateV2ToV3);
    }
    
    /**
     * 数据迁移器接口
     */
    @FunctionalInterface
    public interface DataMigrator extends Function<CompoundTag, CompoundTag> {
    }
    
    /**
     * 数据迁移记录
     */
    private static class MigrationRecord {
        final int fromVersion;
        final int toVersion;
        final DataMigrator migrator;
        
        MigrationRecord(int fromVersion, int toVersion, DataMigrator migrator) {
            this.fromVersion = fromVersion;
            this.toVersion = toVersion;
            this.migrator = migrator;
        }
    }
    
    private static final List<MigrationRecord> migrations = new ArrayList<>();
    
    /**
     * 注册数据迁移器
     */
    public static void registerMigrator(int fromVersion, int toVersion, DataMigrator migrator) {
        migrations.add(new MigrationRecord(fromVersion, toVersion, migrator));
        ArsenalGraft.LOGGER.info("注册数据迁移器: v{} -> v{}", fromVersion, toVersion);
    }
    
    /**
     * 升级数据到当前版本
     */
    public static CompoundTag upgradeData(CompoundTag data) {
        int dataVersion = data.getInt("arsenalgraft_version");
        
        // 如果没有版本信息，假设是v1
        if (dataVersion == 0) {
            dataVersion = 1;
        }
        
        // 如果已经是当前版本，直接返回
        if (dataVersion == CURRENT_VERSION) {
            return data;
        }
        
        // 如果版本更新，警告但尝试处理
        if (dataVersion > CURRENT_VERSION) {
            ArsenalGraft.LOGGER.warn("检测到未来版本的数据 (v{}), 当前支持版本: v{}", 
                                   dataVersion, CURRENT_VERSION);
            return data;
        }
        
        ArsenalGraft.LOGGER.info("升级Arsenal Graft数据: v{} -> v{}", dataVersion, CURRENT_VERSION);
        
        CompoundTag result = data.copy();
        int currentVersion = dataVersion;
        
        // 逐步升级到当前版本
        while (currentVersion < CURRENT_VERSION) {
            boolean migrated = false;
            
            for (MigrationRecord migration : migrations) {
                if (migration.fromVersion == currentVersion) {
                    try {
                        result = migration.migrator.apply(result);
                        currentVersion = migration.toVersion;
                        migrated = true;
                        
                        ArsenalGraft.LOGGER.info("数据升级成功: v{} -> v{}", 
                                               migration.fromVersion, migration.toVersion);
                        break;
                    } catch (Exception e) {
                        ArsenalGraft.LOGGER.error("数据迁移失败: v{} -> v{}", 
                                                migration.fromVersion, migration.toVersion, e);
                        return data; // 返回原始数据
                    }
                }
            }
            
            if (!migrated) {
                ArsenalGraft.LOGGER.error("无法找到从v{}的迁移路径", currentVersion);
                break;
            }
        }
        
        // 设置当前版本
        result.putInt("arsenalgraft_version", CURRENT_VERSION);
        
        return result;
    }
    
    /**
     * 验证数据的完整性
     */
    public static boolean validateData(CompoundTag data, Player player) {
        try {
            // 基础验证
            if (data == null) {
                ArsenalGraft.LOGGER.warn("玩家 {} 的数据为null", player.getName().getString());
                return false;
            }
            
            // 检查版本
            int version = data.getInt("arsenalgraft_version");
            if (version <= 0 || version > CURRENT_VERSION) {
                ArsenalGraft.LOGGER.warn("玩家 {} 的数据版本无效: {}", 
                                       player.getName().getString(), version);
                return false;
            }
            
            // 检查必要字段
            if (!data.contains("player_id") || !data.contains("installed_slots")) {
                ArsenalGraft.LOGGER.warn("玩家 {} 的数据缺少必要字段", player.getName().getString());
                return false;
            }
            
            // 验证玩家ID
            String dataPlayerId = data.getString("player_id");
            if (!dataPlayerId.equals(player.getUUID().toString())) {
                ArsenalGraft.LOGGER.warn("玩家 {} 的数据ID不匹配: {} vs {}", 
                                       player.getName().getString(), dataPlayerId, player.getUUID());
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("验证玩家 {} 的数据时出错", player.getName().getString(), e);
            return false;
        }
    }
    
    /**
     * 创建数据备份
     */
    public static CompoundTag createBackup(CompoundTag data) {
        CompoundTag backup = data.copy();
        backup.putLong("backup_timestamp", System.currentTimeMillis());
        backup.putString("backup_reason", "pre_migration");
        return backup;
    }
    
    /**
     * v1到v2的迁移
     */
    private static CompoundTag migrateV1ToV2(CompoundTag v1Data) {
        ArsenalGraft.LOGGER.info("执行v1到v2数据迁移");
        
        CompoundTag v2Data = new CompoundTag();
        
        // 复制基础数据
        v2Data.putString("player_id", v1Data.getString("player_id"));
        v2Data.putInt("arsenalgraft_version", 2);
        
        // 迁移旧的slot数据到新的installed_slots格式
        if (v1Data.contains("slots")) {
            CompoundTag oldSlots = v1Data.getCompound("slots");
            CompoundTag newSlots = new CompoundTag();
            
            // 转换格式...
            // 这里应该包含具体的数据转换逻辑
            
            v2Data.put("installed_slots", newSlots);
        }
        
        return v2Data;
    }
    
    /**
     * v2到v3的迁移
     */
    private static CompoundTag migrateV2ToV3(CompoundTag v2Data) {
        ArsenalGraft.LOGGER.info("执行v2到v3数据迁移");
        
        CompoundTag v3Data = v2Data.copy();
        v3Data.putInt("arsenalgraft_version", 3);
        
        // v3的改进：添加新的组件系统支持
        if (v3Data.contains("installed_slots")) {
            CompoundTag slots = v3Data.getCompound("installed_slots");
            
            // 为每个插槽添加组件系统支持
            for (String slotKey : slots.getAllKeys()) {
                CompoundTag slot = slots.getCompound(slotKey);
                
                // 确保每个插槽都有组件数据
                if (!slot.contains("components")) {
                    CompoundTag components = new CompoundTag();
                    slot.put("components", components);
                }
            }
        }
        
        // 添加新的配置字段
        if (!v3Data.contains("settings")) {
            CompoundTag settings = new CompoundTag();
            settings.putBoolean("auto_sync", true);
            settings.putInt("sync_interval", 1000);
            v3Data.put("settings", settings);
        }
        
        return v3Data;
    }
}
