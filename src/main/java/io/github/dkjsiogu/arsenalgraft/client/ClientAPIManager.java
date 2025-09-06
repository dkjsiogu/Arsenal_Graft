package io.github.dkjsiogu.arsenalgraft.client;

import io.github.dkjsiogu.arsenalgraft.ArsenalGraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;
import java.util.Optional;

/**
 * Arsenal Graft 客户端API管理器
 * 
 * 提供给其他模组和组件使用的客户端API接口
 * 只在客户端环境下可用
 */
@OnlyIn(Dist.CLIENT)
public class ClientAPIManager {
    
    /**
     * 获取当前客户端玩家的修改数据
     * 
     * @return 当前玩家的修改数据，如果不存在则返回空
     */
    public static Optional<CompoundTag> getCurrentPlayerModificationData() {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            
            if (player == null) {
                return Optional.empty();
            }
            
            CompoundTag data = ClientEventHandler.getClientPlayerData(player.getUUID());
            return Optional.ofNullable(data);
            
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("获取客户端玩家数据失败: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    /**
     * 检查当前玩家是否有特定的修改
     * 
     * @param modificationId 修改ID
     * @return 是否拥有指定修改
     */
    public static boolean hasModification(String modificationId) {
        Optional<CompoundTag> data = getCurrentPlayerModificationData();
        return data.map(tag -> tag.contains(modificationId)).orElse(false);
    }
    
    /**
     * 获取特定修改的数据
     * 
     * @param modificationId 修改ID
     * @return 修改数据，如果不存在则返回空
     */
    public static Optional<CompoundTag> getModificationData(String modificationId) {
        Optional<CompoundTag> playerData = getCurrentPlayerModificationData();
        
        if (playerData.isPresent() && playerData.get().contains(modificationId)) {
            CompoundTag modData = playerData.get().getCompound(modificationId);
            return Optional.of(modData);
        }
        
        return Optional.empty();
    }
    
    /**
     * 检查是否有待处理的UI更新
     * 
     * @return 是否有待更新的UI
     */
    public static boolean hasPendingUIUpdates() {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            
            if (player == null) {
                return false;
            }
            
            return ClientEventHandler.hasPendingUIUpdate(player.getUUID());
            
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("检查UI更新状态失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 获取当前客户端玩家UUID
     * 
     * @return 玩家UUID，如果玩家不存在则返回空
     */
    public static Optional<UUID> getCurrentPlayerId() {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            
            return player != null ? Optional.of(player.getUUID()) : Optional.empty();
            
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("获取当前玩家ID失败: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    /**
     * 检查客户端是否准备就绪
     * 
     * @return 客户端是否准备就绪
     */
    public static boolean isClientReady() {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            return minecraft.level != null && minecraft.player != null;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 强制触发UI刷新
     * 
     * 注意：此方法应谨慎使用，频繁调用可能影响性能
     */
    public static void forceUIRefresh() {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            
            if (player != null) {
                ClientEventHandler.updateClientPlayerData(
                    player.getUUID(), 
                    java.util.Collections.emptyMap(), 
                    true
                );
                ArsenalGraft.LOGGER.debug("强制UI刷新触发: {}", player.getUUID());
            }
            
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("强制UI刷新失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 获取修改数据的摘要信息
     * 
     * @return 修改数据摘要
     */
    public static ModificationSummary getModificationSummary() {
        Optional<CompoundTag> data = getCurrentPlayerModificationData();
        
        if (data.isPresent()) {
            CompoundTag playerData = data.get();
            return new ModificationSummary(
                playerData.getAllKeys().size(),
                playerData.getAllKeys()
            );
        }
        
        return new ModificationSummary(0, java.util.Collections.emptySet());
    }
    
    /**
     * 修改数据摘要类
     */
    public static class ModificationSummary {
        private final int totalModifications;
        private final java.util.Set<String> modificationIds;
        
        public ModificationSummary(int totalModifications, java.util.Set<String> modificationIds) {
            this.totalModifications = totalModifications;
            this.modificationIds = new java.util.HashSet<>(modificationIds);
        }
        
        public int getTotalModifications() {
            return totalModifications;
        }
        
        public java.util.Set<String> getModificationIds() {
            return java.util.Collections.unmodifiableSet(modificationIds);
        }
        
        public boolean hasModification(String modificationId) {
            return modificationIds.contains(modificationId);
        }
        
        @Override
        public String toString() {
            return String.format("ModificationSummary{total=%d, ids=%s}", 
                totalModifications, modificationIds);
        }
    }
}
