package io.github.dkjsiogu.arsenalgraft.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * 对外提供的静态访问 API，供改造模块调用。
 */
public class ExtraInventoryAPI {

    /** 为玩家增加指定来源的槽位。返回新增槽位起始索引。 */
    public static int addSlots(Player player, String sourceId, int count) {
        IExtraInventory inv = get(player);
        if (inv == null || count <= 0) return -1;
        return inv.addSlots(sourceId, count);
    }

    /** 移除来源所有槽位并丢出物品（仅在服务端调用）。 */
    public static void removeSource(Player player, String sourceId) {
        IExtraInventory inv = get(player);
        if (inv == null) return;
        inv.removeSource(sourceId, player);
    }

    /** 获取来源的所有物品引用。 */
    public static List<ItemStack> getSourceItems(Player player, String sourceId) {
        IExtraInventory inv = get(player);
        if (inv == null) return Collections.emptyList();
        return inv.getSourceItems(sourceId);
    }

    /** 遍历查找符合条件的首个物品。 */
    public static ItemStack findFirst(Player player, Predicate<ItemStack> predicate) {
        IExtraInventory inv = get(player);
        if (inv == null) return ItemStack.EMPTY;
        for (ItemStack s : inv.getAll()) {
            if (!s.isEmpty() && predicate.test(s)) return s;
        }
        return ItemStack.EMPTY;
    }

    /** 槽位总数 */
    public static int size(Player player) {
        IExtraInventory inv = get(player);
        return inv == null ? 0 : inv.size();
    }

    private static IExtraInventory get(Player player) {
        LazyOptional<IExtraInventory> opt = player.getCapability(ExtraInventoryProvider.CAPABILITY);
        return opt.map(v -> v).orElse(null);
    }
}
