package io.github.dkjsiogu.arsenalgraft.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 动态扩展物品栏接口：由不同来源(改造)注册的槽位聚合。
 */
public interface IExtraInventory {
    /** 所有槽位的物品（按添加顺序）。 */
    List<ItemStack> getAll();

    /** 指定下标物品（越界返回 ItemStack.EMPTY）。 */
    ItemStack get(int index);

    /** 为一个来源添加 count 个槽位，返回第一个槽位的起始索引。 */
    int addSlots(String sourceId, int count);

    /** 获取某来源的所有物品引用（顺序稳定）。 */
    List<ItemStack> getSourceItems(String sourceId);

    /** 移除来源并把其中物品丢出（仅服务端）。 */
    void removeSource(String sourceId, Player playerIfServer);

    /** 槽位总数。 */
    int size();

    /** 标记已修改（触发后续同步/保存）。 */
    void setChanged();
}
