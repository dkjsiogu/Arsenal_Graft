package io.github.dkjsiogu.arsenalgraft.api;

import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Set;

/**
 * 身体改造管理器 - 统一框架的核心
 * 管理所有身体改造的安装、卸载、配置
 */
public interface BodyModificationManager {
    
    /**
     * 获取玩家的所有已安装改造
     */
    List<BodyModification> getInstalledModifications(Player player);
    
    /**
     * 获取指定身体部位的已安装改造
     */
    List<BodyModification> getModificationsForBodyPart(Player player, BodyPart bodyPart);
    
    /**
     * 安装改造
     * @return 是否成功安装
     */
    boolean installModification(Player player, BodyModification modification);
    
    /**
     * 卸载改造
     * @return 是否成功卸载
     */
    boolean uninstallModification(Player player, String modificationId);
    
    /**
     * 检查是否可以安装改造
     */
    boolean canInstallModification(Player player, BodyModification modification);
    
    /**
     * 获取可用的改造类型
     */
    Set<String> getAvailableModificationTypes();
    
    /**
     * 根据ID创建改造实例
     */
    BodyModification createModification(String modificationId);
    
    /**
     * 注册新的改造类型
     */
    void registerModificationType(String id, ModificationFactory factory);
    
    /**
     * 改造工厂接口
     */
    @FunctionalInterface
    interface ModificationFactory {
        BodyModification create();
    }
}
