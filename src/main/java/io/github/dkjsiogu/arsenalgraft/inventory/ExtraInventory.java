package io.github.dkjsiogu.arsenalgraft.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * IExtraInventory 实现。内部用 Entry 列表保存顺序 + 来源。
 */
public class ExtraInventory implements IExtraInventory {

    public static class Entry {
        public final String sourceId; // 来源（改造ID）
        public ItemStack stack;
        Entry(String sourceId) {
            this.sourceId = sourceId;
            this.stack = ItemStack.EMPTY;
        }
    }

    private final List<Entry> entries = new ArrayList<>();
    private boolean changed = false;

    @Override
    public List<ItemStack> getAll() {
        return entries.stream().map(e -> e.stack).collect(Collectors.toList());
    }

    @Override
    public ItemStack get(int index) {
        if (index < 0 || index >= entries.size()) return ItemStack.EMPTY;
        return entries.get(index).stack;
    }

    @Override
    public int addSlots(String sourceId, int count) {
        int start = entries.size();
        for (int i = 0; i < count; i++) {
            entries.add(new Entry(sourceId));
        }
        setChanged();
        return start;
    }

    @Override
    public List<ItemStack> getSourceItems(String sourceId) {
        return entries.stream().filter(e -> e.sourceId.equals(sourceId)).map(e -> e.stack).collect(Collectors.toList());
    }

    @Override
    public void removeSource(String sourceId, Player playerIfServer) {
        Iterator<Entry> it = entries.iterator();
        while (it.hasNext()) {
            Entry e = it.next();
            if (e.sourceId.equals(sourceId)) {
                if (playerIfServer != null && !playerIfServer.level().isClientSide && !e.stack.isEmpty()) {
                    playerIfServer.drop(e.stack.copy(), true);
                }
                it.remove();
            }
        }
        setChanged();
    }

    @Override
    public int size() { return entries.size(); }

    @Override
    public void setChanged() { changed = true; }

    public boolean isChanged() { return changed; }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (Entry e : entries) {
            CompoundTag ct = new CompoundTag();
            ct.putString("source", e.sourceId);
            CompoundTag itemTag = new CompoundTag();
            e.stack.save(itemTag);
            ct.put("item", itemTag);
            list.add(ct);
        }
        tag.put("entries", list);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        entries.clear();
        if (tag.contains("entries", Tag.TAG_LIST)) {
            ListTag list = tag.getList("entries", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag ct = list.getCompound(i);
                String src = ct.getString("source");
                Entry e = new Entry(src);
                if (ct.contains("item", Tag.TAG_COMPOUND)) {
                    e.stack = ItemStack.of(ct.getCompound("item"));
                } else {
                    e.stack = ItemStack.EMPTY;
                }
                entries.add(e);
            }
        }
    }
}
