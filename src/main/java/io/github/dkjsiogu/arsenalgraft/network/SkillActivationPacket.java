package io.github.dkjsiogu.arsenalgraft.network;

import io.github.dkjsiogu.arsenalgraft.api.v3.component.impl.SkillComponent;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.InstalledSlot;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationManager;
import io.github.dkjsiogu.arsenalgraft.core.service.ServiceRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 技能激活数据包
 * 
 * 客户端请求激活特定技能
 */
public class SkillActivationPacket implements NetworkPacket {
    
    private final UUID slotId;
    private final String skillName;
    private final long timestamp;
    
    public SkillActivationPacket(UUID slotId, String skillName) {
        this.slotId = slotId;
        this.skillName = skillName;
        this.timestamp = System.currentTimeMillis();
    }
    
    public static SkillActivationPacket decode(FriendlyByteBuf buffer) {
        UUID slotId = buffer.readUUID();
        String skillName = buffer.readUtf();
        return new SkillActivationPacket(slotId, skillName);
    }
    
    public static void encode(SkillActivationPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.slotId);
        buffer.writeUtf(packet.skillName);
    }
    
    public static void handle(SkillActivationPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender != null && NetworkHandler.validatePacket(packet, sender)) {
                handleServerSide(packet, sender);
            }
        });
        context.setPacketHandled(true);
    }
    
    private static void handleServerSide(SkillActivationPacket packet, ServerPlayer player) {
        ModificationManager modManager = ServiceRegistry.getInstance()
            .getService(ModificationManager.class);
        if (modManager == null) return;
        
        // 查找对应的插槽
        Optional<InstalledSlot> slotOpt = modManager.getAllInstalledSlots(player)
            .stream()
            .filter(slot -> slot.getSlotId().equals(packet.slotId))
            .findFirst();
            
        if (!slotOpt.isPresent()) {
            return; // 插槽不存在
        }
        
        // 获取技能组件
        SkillComponent skillComponent = slotOpt.get().getComponent("skill", SkillComponent.class);
        if (skillComponent == null) {
            return; // 没有技能组件
        }
        
        // 激活技能
        try {
            skillComponent.activateSkill(player, packet.skillName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean validate(ServerPlayer sender) {
        if (sender == null) return false;
        
        // 验证必要字段
        if (slotId == null || skillName == null) {
            return false;
        }
        
        // 验证技能名长度
        if (skillName.isEmpty() || skillName.length() > 64) {
            return false;
        }
        
        // 检查频率限制
        return checkRateLimit(sender);
    }
    
    @Override
    public long getMinInterval() {
        return 100; // 技能激活最少间隔100ms
    }
    
    @Override
    public int getPriority() {
        return 4; // 高优先级，技能激活需要及时响应
    }
    
    public UUID getSlotId() { return slotId; }
    public String getSkillName() { return skillName; }
    public long getTimestamp() { return timestamp; }
}
