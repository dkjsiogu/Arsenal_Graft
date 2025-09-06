package io.github.dkjsiogu.arsenalgraft.network;

import net.minecraft.network.FriendlyByteBuf;

/**
 * 网络消息接口
 */
public interface INetworkMessage {
    
    /**
     * 编码消息到缓冲区
     */
    void encode(FriendlyByteBuf buffer);
    
    /**
     * 从缓冲区解码消息
     */
    void decode(FriendlyByteBuf buffer);
}
