package io.github.dkjsiogu.arsenalgraft.api;

import net.minecraft.world.entity.LivingEntity;

/**
 * 槽位上下文 - 提供关于槽位的信息
 */
public record SlotContext(
    LivingEntity entity,
    String identifier,
    int index
) {
    
    public SlotContext(LivingEntity entity, String identifier) {
        this(entity, identifier, 0);
    }
}
