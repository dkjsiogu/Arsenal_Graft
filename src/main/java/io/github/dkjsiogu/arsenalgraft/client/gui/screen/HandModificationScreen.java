package io.github.dkjsiogu.arsenalgraft.client.gui.screen;

import io.github.dkjsiogu.arsenalgraft.api.v3.ArsenalGraftAPI;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.InstalledSlot;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationTemplate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 手部改造列表界面：列出已安装的 hand 类型模板 (normal_hand / extra_hand 等)。
 */
public class HandModificationScreen extends Screen {

    private final Player player;
    private List<InstalledSlot> handSlots; // 过滤后的 hand 类型插槽

    private static final int BUTTON_WIDTH = 160;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_GAP = 6;

    public HandModificationScreen(Player player) {
        super(Component.literal("手部改造"));
        this.player = player;
    }

    @Override
    protected void init() {
        super.init();
        loadSlots();
        int startY = 60;
        int x = (this.width - BUTTON_WIDTH) / 2;
        int i = 0;
        for (InstalledSlot slot : handSlots) {
            ModificationTemplate tpl = slot.getTemplate();
            Component name = Component.literal(tpl.getDisplayName().getString());
            addRenderableWidget(Button.builder(name, b -> openSlot(slot))
                .bounds(x, startY + i * (BUTTON_HEIGHT + BUTTON_GAP), BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());
            i++;
        }
        // 返回按钮
        addRenderableWidget(Button.builder(Component.literal("返回"), b -> Minecraft.getInstance().setScreen(new ModificationMainScreen()))
            .bounds(10, 10, 50, 20).build());
    }

    private void loadSlots() {
        List<InstalledSlot> all = ArsenalGraftAPI.getAllModifications(player);
        handSlots = all.stream().filter(s -> {
            String slotType = s.getTemplate().getSlotType();
            return "hand".equals(slotType);
        }).collect(Collectors.toList());
    }

    private void openSlot(InstalledSlot slot) {
        Minecraft.getInstance().setScreen(new HandInventoryScreen(player, slot));
    }

    @Override
    public void render(@Nonnull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        g.drawCenteredString(this.font, this.title, this.width / 2, 25, 0xFFFFFF);
        super.render(g, mouseX, mouseY, partialTick);
        if (handSlots.isEmpty()) {
            g.drawCenteredString(this.font, Component.literal("尚未安装手部改造"), this.width / 2, 50, 0xAAAAAA);
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
