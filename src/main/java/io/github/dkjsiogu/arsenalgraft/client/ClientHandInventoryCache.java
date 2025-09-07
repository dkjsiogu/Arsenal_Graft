package io.github.dkjsiogu.arsenalgraft.client;

import net.minecraft.world.item.ItemStack;

import java.util.*;

/**
 * 客户端镜像缓存 (只读)。
 */
public class ClientHandInventoryCache {
    private static final Map<UUID, List<ItemStack>> CACHE = new HashMap<>();
    private static final Set<UUID> OPEN = new HashSet<>();

    public static void markOpen(UUID id) { OPEN.add(id); }
    public static void markClosed(UUID id) { OPEN.remove(id); }
    public static boolean isOpen(UUID id) { return OPEN.contains(id); }

    public static void applyServerSnapshot(UUID id, List<ItemStack> stacks) {
        CACHE.put(id, new ArrayList<>(stacks));
    }

    public static ItemStack getItem(UUID id, int slot) {
        var list = CACHE.get(id); if (list == null || slot < 0 || slot >= list.size()) return ItemStack.EMPTY; return list.get(slot);
    }

    public static int getSize(UUID id) { var list = CACHE.get(id); return list==null?0:list.size(); }
}
