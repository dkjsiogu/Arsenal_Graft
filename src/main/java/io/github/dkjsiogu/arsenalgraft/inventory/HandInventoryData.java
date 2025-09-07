package io.github.dkjsiogu.arsenalgraft.inventory;

import net.minecraftforge.items.ItemStackHandler;
import java.util.function.IntConsumer;

/**
 * 服务端权威手部改造库存数据容器 (每个 InstalledSlot 专属)。
 * 客户端只持有镜像, 不直接写入。
 */
public class HandInventoryData extends ItemStackHandler {

    private IntConsumer changeListener; // 监听单槽变化 (服务器端设置)

    public HandInventoryData(int size) { super(size); }

    public void setChangeListener(IntConsumer listener) { this.changeListener = listener; }

    @Override
    protected void onContentsChanged(int slot) {
        if (changeListener != null) changeListener.accept(slot);
    }
}
