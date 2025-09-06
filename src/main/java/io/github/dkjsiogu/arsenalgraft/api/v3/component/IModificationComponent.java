package io.github.dkjsiogu.arsenalgraft.api.v3.component;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * 改造组件基础接口
 * 
 * 所有改造组件都必须实现这个接口。
 * 组件是改造功能的基本单位，决定了一个插槽能做什么。
 */
public interface IModificationComponent extends INBTSerializable<CompoundTag> {
    
    /**
     * 获取组件类型ID
     */
    String getComponentType();
    
    /**
     * 组件是否已激活
     */
    boolean isActive();
    
    /**
     * 设置组件激活状态
     */
    void setActive(boolean active);
    
    /**
     * 组件初始化 (当插槽被安装时调用)
     */
    void onInstall(Player player);
    
    /**
     * 组件清理 (当插槽被卸载时调用)
     */
    void onUninstall(Player player);
    
    /**
     * 每tick更新 (如果需要)
     */
    default void tick(Player player) {}
    
    /**
     * 创建组件的副本
     */
    IModificationComponent copy();
}
