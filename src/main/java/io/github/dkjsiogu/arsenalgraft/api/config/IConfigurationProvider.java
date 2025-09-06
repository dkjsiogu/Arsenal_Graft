package io.github.dkjsiogu.arsenalgraft.api.config;

import net.minecraft.world.entity.player.Player;

import java.util.Map;

/**
 * 配置提供者接口 - 架构核心
 * 
 * 每个改造都应该实现这个接口来提供配置功能
 * 这是整个配置系统的入口点
 */
public interface IConfigurationProvider {
    
    /**
     * 获取配置架构 - 定义有哪些配置选项
     * 这是声明式的，描述了配置的结构
     */
    ConfigurationSchema getConfigurationSchema();
    
    /**
     * 获取当前配置值
     * 从玩家数据中读取实际的配置值
     */
    Map<String, Object> getCurrentConfiguration(Player player);
    
    /**
     * 设置配置值
     * 验证并保存配置值到玩家数据
     */
    boolean setConfiguration(Player player, String key, Object value);
    
    /**
     * 验证配置值
     * 在设置之前验证值是否有效
     */
    ValidationResult validateConfiguration(String key, Object value);
    
    /**
     * 重置配置到默认值
     * 清除所有自定义配置，恢复默认值
     */
    void resetToDefaults(Player player);
}
