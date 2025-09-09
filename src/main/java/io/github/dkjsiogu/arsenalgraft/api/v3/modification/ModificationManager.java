package io.github.dkjsiogu.arsenalgraft.api.v3.modification;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Optional;

/**
 * 改造管理器接口
 * 
 * 这是3.0版本的核心管理器，负责：
 * 1. 加载JSON和KubeJS定义的改造模板
 * 2. 管理玩家的已安装插槽
 * 3. 提供统一的安装/卸载接口
 * 4. 数据同步
 */
public interface ModificationManager {
    
    /**
     * 注册改造模板
     */
    void registerTemplate(ResourceLocation id, ModificationTemplate template);
    
    /**
     * 获取改造模板
     */
    Optional<ModificationTemplate> getTemplate(ResourceLocation id);
    
    /**
     * 获取所有已注册的模板ID
     */
    List<ResourceLocation> getAllTemplateIds();

    /**
     * 清空所有已注册模板 (用于资源重载)
     */
    void clearTemplates();

    /**
     * 是否已注册指定模板
     */
    boolean isTemplateRegistered(ResourceLocation id);
    
    /**
     * 检查玩家是否可以安装指定改造
     */
    boolean canInstallModification(Player player, ModificationTemplate template);
    
    /**
     * 为玩家创建已安装插槽
     */
    InstalledSlot createInstalledSlot(Player player, ModificationTemplate template);
    
    /**
     * 安装插槽到玩家
     */
    boolean installSlot(Player player, InstalledSlot slot);
    
    /**
     * 卸载玩家的指定插槽
     */
    boolean uninstallSlot(Player player, InstalledSlot slot);
    
    /**
     * 卸载玩家的指定改造
     */
    boolean uninstallModification(Player player, ResourceLocation modificationId);
    
    /**
     * 检查玩家是否有指定改造
     */
    boolean hasModification(Player player, ResourceLocation modificationId);
    
    /**
     * 获取玩家的指定改造插槽
     */
    Optional<InstalledSlot> getInstalledSlot(Player player, ResourceLocation modificationId);
    
    /**
     * 获取玩家的所有已安装插槽
     */
    List<InstalledSlot> getAllInstalledSlots(Player player);
    
    /**
     * 获取指定槽位类型的已安装插槽
     */
    List<InstalledSlot> getInstalledSlotsByType(Player player, String slotType);
    
    /**
     * 同步数据到客户端
     */
    void syncToClient(net.minecraft.server.level.ServerPlayer player);
    
    /**
     * 保存玩家数据
     */
    void savePlayerData(Player player);
    
    /**
     * 加载玩家数据
     */
    void loadPlayerData(Player player);
}
