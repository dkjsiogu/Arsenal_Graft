package io.github.dkjsiogu.arsenalgraft.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

/**
 * 身体改造接口 - 统一框架
 * 所有身体改造都必须实现这个接口
 */
public interface BodyModification extends INBTSerializable<CompoundTag> {
    
    /**
     * 获取改造ID
     */
    String getModificationId();
    
    /**
     * 获取改造显示名称
     */
    Component getDisplayName();
    
    /**
     * 获取带状态的显示名称
     */
    default Component getDisplayNameWithStatus() {
        String name = getDisplayName().getString();
        if (isInstalled()) {
            return Component.literal(name + " (已启用)");
        }
        return Component.literal(name);
    }
    
    /**
     * 获取改造描述
     */
    List<Component> getDescription();
    
    /**
     * 获取适用的身体部位
     */
    BodyPart getTargetBodyPart();
    
    /**
     * 是否已安装
     */
    boolean isInstalled();
    
    /**
     * 设置安装状态
     */
    void setInstalled(boolean installed);
    
    /**
     * 安装改造时调用
     */
    void onInstall(Player player);
    
    /**
     * 卸载改造时调用
     */
    void onUninstall(Player player);
    
    /**
     * 打开配置界面
     */
    void openConfigurationGui(Player player);
    
    /**
     * 是否有配置选项
     */
    boolean hasConfiguration();
    
    /**
     * 每tick更新（如果需要）
     */
    default void tick(Player player) {}
    
    /**
     * 获取最大安装数量（针对同一身体部位）
     */
    default int getMaxInstallCount() {
        return 1;
    }
    
    /**
     * 创建改造的副本
     */
    BodyModification copy();
}
