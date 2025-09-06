package io.github.dkjsiogu.arsenalgraft.network;

import net.minecraftforge.network.NetworkEvent;

/**
 * 网络处理器接口
 */
public interface INetworkHandler<T> {
    
    /**
     * 处理网络消息
     */
    void handle(T message, NetworkEvent.Context context);
}
