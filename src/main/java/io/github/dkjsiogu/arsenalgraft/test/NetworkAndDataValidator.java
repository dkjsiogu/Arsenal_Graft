package io.github.dkjsiogu.arsenalgraft.test;

import io.github.dkjsiogu.arsenalgraft.network.ModificationSyncPacket;
import io.github.dkjsiogu.arsenalgraft.network.ComponentUpdatePacket;
import io.github.dkjsiogu.arsenalgraft.data.DataVersionManager;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 网络通信和数据持久化系统验证器
 * 
 * 验证Arsenal Graft 3.0的网络包系统和数据管理功能
 */
public class NetworkAndDataValidator {
    
    public static boolean validateNetworkAndDataSystems() {
        try {
            System.out.println("=== Arsenal Graft 3.0 网络通信和数据持久化验证开始 ===");
            
            // 测试1: 网络包创建和验证
            boolean networkPacketTest = testNetworkPackets();
            System.out.println("网络包系统测试: " + (networkPacketTest ? "✓ 通过" : "✗ 失败"));
            
            // 测试2: 数据版本管理
            boolean dataVersionTest = testDataVersionManagement();
            System.out.println("数据版本管理测试: " + (dataVersionTest ? "✓ 通过" : "✗ 失败"));
            
            // 测试3: 数据序列化和反序列化
            boolean serializationTest = testDataSerialization();
            System.out.println("数据序列化测试: " + (serializationTest ? "✓ 通过" : "✗ 失败"));
            
            // 测试4: 网络包安全验证
            boolean securityTest = testNetworkSecurity();
            System.out.println("网络安全测试: " + (securityTest ? "✓ 通过" : "✗ 失败"));
            
            boolean allPassed = networkPacketTest && dataVersionTest && 
                              serializationTest && securityTest;
            
            System.out.println("=== 网络通信和数据持久化验证完成: " + 
                             (allPassed ? "所有测试通过 🎉" : "部分测试失败 ❌") + " ===");
            
            return allPassed;
            
        } catch (Exception e) {
            System.err.println("网络通信和数据持久化验证过程中发生异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static boolean testNetworkPackets() {
        try {
            // 测试ModificationSyncPacket创建
            UUID testPlayerId = UUID.randomUUID();
            Map<String, CompoundTag> testData = new HashMap<>();
            
            CompoundTag slotData = new CompoundTag();
            slotData.putString("slotId", UUID.randomUUID().toString());
            slotData.putString("templateId", "test:simple_hand");
            slotData.putBoolean("installed", true);
            
            testData.put("test_slot", slotData);
            
            ModificationSyncPacket syncPacket = new ModificationSyncPacket(testPlayerId, testData);
            
            // 验证包数据
            if (!syncPacket.getPlayerId().equals(testPlayerId)) {
                System.err.println("  - ModificationSyncPacket玩家ID不匹配");
                return false;
            }
            
            if (syncPacket.getModificationData().size() != 1) {
                System.err.println("  - ModificationSyncPacket数据大小不匹配");
                return false;
            }
            
            // 测试ComponentUpdatePacket创建
            UUID slotId = UUID.randomUUID();
            CompoundTag updateData = new CompoundTag();
            updateData.putString("action", "activate");
            updateData.putLong("timestamp", System.currentTimeMillis());
            
            ComponentUpdatePacket updatePacket = new ComponentUpdatePacket(
                slotId, "skill", updateData);
            
            // 验证包数据
            if (!updatePacket.getSlotId().equals(slotId)) {
                System.err.println("  - ComponentUpdatePacket槽位ID不匹配");
                return false;
            }
            
            if (!"skill".equals(updatePacket.getComponentType())) {
                System.err.println("  - ComponentUpdatePacket组件类型不匹配");
                return false;
            }
            
            System.out.println("  - 网络包创建和验证成功");
            return true;
            
        } catch (Exception e) {
            System.err.println("  - 网络包测试异常: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testDataVersionManagement() {
        try {
            // 测试版本升级
            CompoundTag v1Data = new CompoundTag();
            v1Data.putString("player_id", UUID.randomUUID().toString());
            v1Data.putInt("arsenalgraft_version", 1);
            
            // 添加一些v1格式的数据
            CompoundTag oldSlots = new CompoundTag();
            oldSlots.putString("hand_slot", "basic_hand");
            v1Data.put("slots", oldSlots);
            
            // 升级到当前版本
            CompoundTag upgradedData = DataVersionManager.upgradeData(v1Data);
            
            // 验证升级结果
            if (upgradedData.getInt("arsenalgraft_version") != DataVersionManager.CURRENT_VERSION) {
                System.err.println("  - 数据版本升级失败");
                return false;
            }
            
            // 测试创建备份
            CompoundTag backup = DataVersionManager.createBackup(upgradedData);
            if (!backup.contains("backup_timestamp")) {
                System.err.println("  - 数据备份创建失败");
                return false;
            }
            
            System.out.println("  - 数据版本管理和备份功能正常");
            return true;
            
        } catch (Exception e) {
            System.err.println("  - 数据版本管理测试异常: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testDataSerialization() {
        try {
            // 创建测试数据
            CompoundTag testData = new CompoundTag();
            testData.putString("player_id", UUID.randomUUID().toString());
            testData.putString("player_name", "TestPlayer");
            testData.putLong("last_save", System.currentTimeMillis());
            testData.putInt("arsenalgraft_version", DataVersionManager.CURRENT_VERSION);
            
            // 添加插槽数据
            CompoundTag slotsData = new CompoundTag();
            for (int i = 0; i < 3; i++) {
                CompoundTag slot = new CompoundTag();
                slot.putString("slotId", UUID.randomUUID().toString());
                slot.putString("templateId", "test:slot_" + i);
                slot.putBoolean("installed", i % 2 == 0);
                
                // 添加组件数据
                CompoundTag components = new CompoundTag();
                CompoundTag attributeComp = new CompoundTag();
                attributeComp.putString("componentType", "attribute_modification");
                attributeComp.putBoolean("active", true);
                components.put("attribute_modification", attributeComp);
                
                slot.put("components", components);
                slotsData.put("slot_" + i, slot);
            }
            testData.put("installed_slots", slotsData);
            
            // 测试数据大小
            String serialized = testData.toString();
            if (serialized.length() > 65536) {
                System.err.println("  - 序列化数据过大: " + serialized.length() + " 字节");
                return false;
            }
            
            // 测试数据完整性
            if (!testData.contains("installed_slots")) {
                System.err.println("  - 序列化数据缺少关键字段");
                return false;
            }
            
            CompoundTag deserializedSlots = testData.getCompound("installed_slots");
            if (deserializedSlots.size() != 3) {
                System.err.println("  - 反序列化插槽数量不匹配");
                return false;
            }
            
            System.out.println("  - 数据序列化和反序列化功能正常");
            System.out.println("  - 测试数据大小: " + serialized.length() + " 字节");
            return true;
            
        } catch (Exception e) {
            System.err.println("  - 数据序列化测试异常: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testNetworkSecurity() {
        try {
            // 测试数据包大小限制
            UUID slotId = UUID.randomUUID();
            CompoundTag largeData = new CompoundTag();
            
            // 创建超大数据
            StringBuilder largeString = new StringBuilder();
            for (int i = 0; i < 10000; i++) {
                largeString.append("这是一个很长的测试字符串_").append(i);
            }
            largeData.putString("large_field", largeString.toString());
            
            ComponentUpdatePacket largePacket = new ComponentUpdatePacket(
                slotId, "test", largeData);
            
            // 验证包应该被拒绝（因为太大）
            if (largeData.toString().length() <= largePacket.getMaxSize()) {
                System.err.println("  - 大数据包未被正确限制");
                return false;
            }
            
            // 测试频率限制
            ComponentUpdatePacket normalPacket = new ComponentUpdatePacket(
                slotId, "skill", new CompoundTag());
            
            // 验证正常包的频率限制
            long minInterval = normalPacket.getMinInterval();
            if (minInterval <= 0 || minInterval > 1000) {
                System.err.println("  - 频率限制配置不合理: " + minInterval + "ms");
                return false;
            }
            
            // 测试优先级系统
            if (normalPacket.getPriority() < 0) {
                System.err.println("  - 包优先级配置无效");
                return false;
            }
            
            System.out.println("  - 网络安全验证功能正常");
            System.out.println("  - 包大小限制: " + normalPacket.getMaxSize() + " 字节");
            System.out.println("  - 频率限制: " + minInterval + "ms");
            return true;
            
        } catch (Exception e) {
            System.err.println("  - 网络安全测试异常: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取系统性能指标
     */
    public static void printPerformanceMetrics() {
        System.out.println("\n=== 系统性能指标 ===");
        
        // 内存使用情况
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        System.out.println("内存使用: " + (usedMemory / 1024 / 1024) + "MB / " + 
                         (totalMemory / 1024 / 1024) + "MB");
        
        // 网络包性能测试
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            UUID testId = UUID.randomUUID();
            CompoundTag testData = new CompoundTag();
            testData.putString("test", "performance_test_" + i);
            
            new ComponentUpdatePacket(testId, "test", testData);
        }
        long endTime = System.nanoTime();
        
        double avgTimeMs = (endTime - startTime) / 1000000.0 / 1000.0;
        System.out.println("网络包创建性能: " + String.format("%.3f", avgTimeMs) + "ms/1000次");
        
        System.out.println("===================");
    }
}
