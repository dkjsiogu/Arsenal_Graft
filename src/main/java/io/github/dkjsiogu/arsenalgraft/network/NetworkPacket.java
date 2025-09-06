package io.github.dkjsiogu.arsenalgraft.network;

import net.minecraft.server.level.ServerPlayer;

/**
 * 网络包基础接口
 * 
 * 所有Arsenal Graft网络包都应该实现此接口
 * 提供基础的验证和安全检查功能
 */
public interface NetworkPacket {
    
    /**
     * 验证数据包的有效性
     * 
     * @param sender 发送方玩家（可能为null，如果是服务端发送的包）
     * @return 如果数据包有效则返回true
     */
    boolean validate(ServerPlayer sender);
    
    /**
     * 获取数据包的大小限制（字节）
     * 用于防止恶意客户端发送过大的数据包
     * 
     * @return 最大字节数
     */
    default int getMaxSize() {
        return 32768; // 32KB默认限制
    }
    
    /**
     * 检查发送频率限制
     * 防止数据包洪泛攻击
     * 
     * @param sender 发送方玩家
     * @return 如果在频率限制内则返回true
     */
    default boolean checkRateLimit(ServerPlayer sender) {
        if (sender == null) return true;
        
        String packetType = this.getClass().getSimpleName();
        long currentTime = System.currentTimeMillis();
        String rateLimitKey = "arsenalgraft.ratelimit." + packetType;
        
        long lastSent = sender.getPersistentData().getLong(rateLimitKey);
        long minInterval = getMinInterval();
        
        if (currentTime - lastSent >= minInterval) {
            sender.getPersistentData().putLong(rateLimitKey, currentTime);
            return true;
        }
        
        return false;
    }
    
    /**
     * 获取此类型数据包的最小发送间隔（毫秒）
     * 
     * @return 最小间隔时间
     */
    default long getMinInterval() {
        return 50; // 50毫秒默认间隔，即最多20次/秒
    }
    
    /**
     * 获取数据包的优先级
     * 用于网络拥塞时的包调度
     * 
     * @return 优先级值，越大越优先
     */
    default int getPriority() {
        return 0; // 默认优先级
    }
    
    /**
     * 是否需要确认收到
     * 
     * @return 如果需要确认则返回true
     */
    default boolean requiresAck() {
        return false;
    }
    
    /**
     * 是否允许在离线时处理
     * 
     * @return 如果允许离线处理则返回true
     */
    default boolean allowOfflineProcessing() {
        return false;
    }
}
