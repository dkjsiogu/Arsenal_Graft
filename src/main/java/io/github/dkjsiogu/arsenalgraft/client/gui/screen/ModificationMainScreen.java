package io.github.dkjsiogu.arsenalgraft.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import io.github.dkjsiogu.arsenalgraft.client.gui.GuiLayerManager;
import io.github.dkjsiogu.arsenalgraft.client.gui.widget.NavigationButton;
import io.github.dkjsiogu.arsenalgraft.client.gui.widget.ShapeButton;
import io.github.dkjsiogu.arsenalgraft.client.gui.registry.GuiRegistry;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import io.github.dkjsiogu.arsenalgraft.api.v3.ArsenalGraftAPI;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationTemplate;
import net.minecraft.resources.ResourceLocation;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.InstalledSlot;
import io.github.dkjsiogu.arsenalgraft.network.OpenHandInventoryPacket;
import io.github.dkjsiogu.arsenalgraft.network.NetworkHandler;
import net.minecraft.client.Minecraft;

/**
 * 改造系统主界面 - 显示人形布局，点击身体部位导航到对应的改造列表
 */
public class ModificationMainScreen extends Screen {
    
    // 界面布局常量 - 史蒂夫比例调整
    private static final int HEAD_WIDTH = 40;
    private static final int HEAD_HEIGHT = 40;
    
    private static final int TORSO_WIDTH = 60;  // 躯干更宽
    private static final int TORSO_HEIGHT = 50; // 躯干更高
    
    private static final int ARM_WIDTH = 24;
    private static final int ARM_HEIGHT = 50;
    
    private static final int LEG_WIDTH = 24;
    private static final int LEG_HEIGHT = 50;
    
    private static final int FOOT_WIDTH = 24;
    private static final int FOOT_HEIGHT = 16;
    
    // 人形布局坐标（相对于屏幕中心）- 更紧密的史蒂夫布局
    // 头部
    private static final int HEAD_X = 0;
    private static final int HEAD_Y = -80;
    
    // 躯干
    private static final int TORSO_X = 0;
    private static final int TORSO_Y = -30;
    
    // 手臂 - 更贴近躯干
    private static final int LEFT_ARM_X = -50;  // 更靠近
    private static final int LEFT_ARM_Y = -30;
    private static final int RIGHT_ARM_X = 50;  // 更靠近
    private static final int RIGHT_ARM_Y = -30;
    
    // 腿部 - 更贴近躯干
    private static final int LEFT_LEG_X = -15;  // 更靠近中心
    private static final int LEFT_LEG_Y = 30;
    private static final int RIGHT_LEG_X = 15;  // 更靠近中心
    private static final int RIGHT_LEG_Y = 30;
    
    // 脚部
    private static final int LEFT_FOOT_X = -15;
    private static final int LEFT_FOOT_Y = 65;
    private static final int RIGHT_FOOT_X = 15;
    private static final int RIGHT_FOOT_Y = 65;
    
    private int centerY;
    // 右侧内嵌改造列表面板尺寸
    private static final int SIDE_PANEL_WIDTH = 260;
    private static final int SIDE_PANEL_PADDING = 10;
    // 下移侧栏，避免与上方提示文字重叠
    private static final int SIDE_LIST_START_Y = 50; // 相对面板顶部的列表起始 Y
    private static final int SIDE_LIST_ITEM_HEIGHT = 22;
    // 人形与右侧面板之间额外留白，缓解拥挤
    private static final int HUMANOID_RIGHT_GAP = 40;

    // 当前选择的身体部位（用于在右侧显示对应改造列表）
    private Component selectedBodyPartDisplayName = null;
    private final List<Component> sideListItems = new ArrayList<>();
    private final List<ResourceLocation> sideListTemplateIds = new ArrayList<>(); // 与条目一一对应（已安装或可安装）
    private final List<UUID> sideListSlotIds = new ArrayList<>(); // 已安装条目对应槽位ID；未安装为 null
    private final List<Boolean> sideListIsInstalled = new ArrayList<>(); // 标记是否已安装
    private int selectedTemplateIndex = -1;
    
    public ModificationMainScreen() {
        super(Component.translatable("gui.arsenalgraft.main_screen.title"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        // 计算屏幕中心点
        this.centerY = this.height / 2;
        
    // 创建身体部位按钮（人形 GUI 向左移动，给右侧侧栏留出空间）
    createBodyPartButtons();
        
        // 创建底部导航按钮
        createNavigationButtons();
    }
    
    private void createBodyPartButtons() {
        // 计算人形中心（将人形区域限定在屏幕宽度减去侧边栏的左侧）
    int humanoidAreaRight = this.width - SIDE_PANEL_WIDTH - SIDE_PANEL_PADDING - HUMANOID_RIGHT_GAP;
        int humanoidCenterX = humanoidAreaRight / 2;

        // 头部按钮
        addRenderableWidget(createBodyPartButton(
            "head", humanoidCenterX + HEAD_X, HEAD_Y, HEAD_WIDTH, HEAD_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.head")
        ));
        
        // 躯干按钮
        addRenderableWidget(createBodyPartButton(
            "torso", humanoidCenterX + TORSO_X, TORSO_Y, TORSO_WIDTH, TORSO_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.torso")
        ));
        
        // 左臂按钮 -> 打开手部改造界面
        addRenderableWidget(GuiRegistry.createBodyPartButton(
            humanoidCenterX + LEFT_ARM_X - ARM_WIDTH / 2,
            centerY + LEFT_ARM_Y - ARM_HEIGHT / 2,
            ARM_WIDTH, ARM_HEIGHT,
            "left_arm",
            Component.translatable("gui.arsenalgraft.body_part.left_arm"),
            () -> selectBodyPart("left_arm", Component.translatable("gui.arsenalgraft.body_part.left_arm"))
        ));
        // 右臂按钮同样打开手部界面
        addRenderableWidget(GuiRegistry.createBodyPartButton(
            humanoidCenterX + RIGHT_ARM_X - ARM_WIDTH / 2,
            centerY + RIGHT_ARM_Y - ARM_HEIGHT / 2,
            ARM_WIDTH, ARM_HEIGHT,
            "right_arm",
            Component.translatable("gui.arsenalgraft.body_part.right_arm"),
            () -> selectBodyPart("right_arm", Component.translatable("gui.arsenalgraft.body_part.right_arm"))
        ));
        
        // 左腿按钮 (映射到腿部改造)
        addRenderableWidget(createBodyPartButton(
            "left_leg", humanoidCenterX + LEFT_LEG_X, LEFT_LEG_Y, LEG_WIDTH, LEG_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.left_leg")
        ));
        
        // 右腿按钮 (映射到腿部改造)
        addRenderableWidget(createBodyPartButton(
            "right_leg", humanoidCenterX + RIGHT_LEG_X, RIGHT_LEG_Y, LEG_WIDTH, LEG_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.right_leg")
        ));
        
        // 左脚按钮
        addRenderableWidget(createBodyPartButton(
            "left_foot", humanoidCenterX + LEFT_FOOT_X, LEFT_FOOT_Y, FOOT_WIDTH, FOOT_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.left_foot")
        ));
        
        // 右脚按钮
        addRenderableWidget(createBodyPartButton(
            "right_foot", humanoidCenterX + RIGHT_FOOT_X, RIGHT_FOOT_Y, FOOT_WIDTH, FOOT_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.right_foot")
        ));
    }
    
    /**
     * 创建身体部位按钮的辅助方法
     */
    private ShapeButton createBodyPartButton(String bodyPart, int offsetX, int offsetY, int width, int height, Component displayName) {
    int x = offsetX - width / 2;
    int y = centerY + offsetY - height / 2;
        
        // 使用新的形状按钮系统
        return GuiRegistry.createBodyPartButton(
            x, y, width, height,
            bodyPart, displayName,
            () -> {
                // 在同一画面显示右侧改造列表
                selectBodyPart(bodyPart, displayName);
            }
        );
    }

    /**
     * 选择身体部位并在右侧面板显示对应改造列表（目前使用占位数据）
     */
    private void selectBodyPart(String bodyPart, Component displayName) {
        this.selectedBodyPartDisplayName = displayName;

        // 从统一 API 获取已注册的模板并按 bodyPart -> slotType 进行过滤
    sideListItems.clear();
    sideListTemplateIds.clear();
    sideListSlotIds.clear();
    sideListIsInstalled.clear();
    selectedTemplateIndex = -1;

        String targetSlotType = mapBodyPartToSlotType(bodyPart);

        // 先列出玩家已安装的对应 slotType 的所有实例（可能有多个）
        var player = Minecraft.getInstance().player;
        if (player != null && targetSlotType != null) {
            var installedSlots = ArsenalGraftAPI.getAllModifications(player);
            // 计数同一种模板出现次数
            java.util.Map<ResourceLocation, Integer> counter = new java.util.HashMap<>();
            for (InstalledSlot slot : installedSlots) {
                ModificationTemplate tpl = slot.getTemplate();
                if (tpl == null) continue;
                if (!targetSlotType.equals(tpl.getSlotType())) continue;
                int idx = counter.getOrDefault(tpl.getId(), 0) + 1;
                counter.put(tpl.getId(), idx);
                // 条目文本：名称 #序号
                Component label = Component.literal(tpl.getDisplayName().getString() + " #" + idx);
                sideListItems.add(label);
                sideListTemplateIds.add(tpl.getId());
                sideListSlotIds.add(slot.getSlotId());
                sideListIsInstalled.add(Boolean.TRUE);
            }
        }

        // 如果还没有已安装但仍想展示可安装模板，可在此添加未安装模板。当前需求只展示已安装的。
    }

    /**
     * 根据 bodyPart 字符串映射到模板的 slotType（可扩展）
     */
    private String mapBodyPartToSlotType(String bodyPart) {
        if (bodyPart == null) return null;
        switch (bodyPart) {
            case "left_arm":
            case "right_arm":
            case "left_hand":
            case "right_hand":
                return "hand";
            case "left_leg":
            case "right_leg":
                return "leg";
            case "left_foot":
            case "right_foot":
            case "feet":
                return "feet";
            case "torso":
            case "chest":
                return "torso";
            case "head":
                return "head";
            default:
                return null;
        }
    }
    
    private void createNavigationButtons() {
        // 关闭按钮（右下角）
        addRenderableWidget(new NavigationButton(
            this.width - 80, this.height - 30, 70, 20,
            Component.translatable("gui.arsenalgraft.close"),
            null, () -> null, new HashMap<>(),
            NavigationButton.ButtonStyle.ACTION, false
        ) {
            @Override
            public void onPress() {
                GuiLayerManager.getInstance().closeAll();
            }
        });
        
        // 设置按钮（左下角）
        Map<String, Object> settingsContext = new HashMap<>();
        settingsContext.put("returnToMain", true);
        
        addRenderableWidget(new NavigationButton(
            10, this.height - 30, 70, 20,
            Component.translatable("gui.arsenalgraft.settings"),
            GuiLayerManager.GuiLayer.SETTINGS,
            () -> new SettingsScreen(),
            settingsContext,
            NavigationButton.ButtonStyle.NAVIGATION, true
        ));
    }

    // 手臂点击现在统一使用侧栏，不再跳转独立界面，保持单屏体验
    
    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染背景
        renderBackground(guiGraphics);
        
        // 渲染标题
        guiGraphics.drawCenteredString(this.font, this.title, 
                                     this.width / 2, 20, 0xFFFFFF);
        
        // 渲染说明文本
        Component infoText = Component.translatable("gui.arsenalgraft.main_screen.info");
        guiGraphics.drawCenteredString(this.font, infoText, 
                                     this.width / 2, 40, 0xCCCCCC);
        
        // 渲染所有组件
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // 渲染右侧内嵌改造列表面板
        renderSidePanel(guiGraphics, mouseX, mouseY);
    }

    private void renderSidePanel(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int panelX = this.width - SIDE_PANEL_WIDTH - SIDE_PANEL_PADDING;
    int panelY = 60; // 下移
        int panelRight = this.width - SIDE_PANEL_PADDING;
        int panelBottom = this.height - SIDE_PANEL_PADDING;

        // 背景
        guiGraphics.fill(panelX, panelY, panelRight, panelBottom, 0xCC1F1F1F);

        // 标题
        Component title = Component.translatable("gui.arsenalgraft.sidepanel.title");
        guiGraphics.drawString(this.font, title, panelX + SIDE_PANEL_PADDING, panelY + 8, 0xFFFFFF, false);

        // 选中身体部位名称
        if (selectedBodyPartDisplayName != null) {
            guiGraphics.drawString(this.font, selectedBodyPartDisplayName, panelX + SIDE_PANEL_PADDING, panelY + 26, 0xCCCCCC, false);
        }

    // 列表项
    int startY = panelY + SIDE_LIST_START_Y;
        for (int i = 0; i < sideListItems.size(); i++) {
            int itemY = startY + i * SIDE_LIST_ITEM_HEIGHT;
            int itemTop = itemY;
            int itemBottom = itemY + SIDE_LIST_ITEM_HEIGHT - 4;

            // 背景高亮
            boolean hovered = mouseX >= panelX + SIDE_PANEL_PADDING && mouseX <= panelRight - SIDE_PANEL_PADDING && mouseY >= itemTop && mouseY <= itemBottom;
            if (hovered || i == selectedTemplateIndex) {
                int bg = (i == selectedTemplateIndex) ? 0x8855AAFF : 0x88FFFFFF;
                guiGraphics.fill(panelX + SIDE_PANEL_PADDING, itemTop, panelRight - SIDE_PANEL_PADDING, itemBottom, bg);
            }

            guiGraphics.drawString(this.font, sideListItems.get(i), panelX + SIDE_PANEL_PADDING + 4, itemTop + 4, 0xFFFFFF, false);
        }

        // 简单的滚动 / 状态（后续可以接入真实滚动逻辑）
    if (sideListItems.isEmpty()) {
            guiGraphics.drawString(this.font, Component.translatable("gui.arsenalgraft.sidepanel.empty"), panelX + SIDE_PANEL_PADDING, startY, 0xAAAAAA, false);
        } else if (selectedTemplateIndex >= 0 && selectedTemplateIndex < sideListTemplateIds.size()) {
            // 绘制简单详情
            int detailTop = startY + sideListItems.size() * SIDE_LIST_ITEM_HEIGHT + 8;
            int detailLeft = panelX + SIDE_PANEL_PADDING;
            int detailRight = panelRight - SIDE_PANEL_PADDING;
            int detailBottom = panelBottom - SIDE_PANEL_PADDING;
            guiGraphics.fill(detailLeft, detailTop, detailRight, detailBottom, 0x33111111);

            ResourceLocation id = sideListTemplateIds.get(selectedTemplateIndex);
            ModificationTemplate tpl = ArsenalGraftAPI.getModificationTemplate(id);
            if (tpl != null) {
                guiGraphics.drawString(this.font, tpl.getDisplayName(), detailLeft + 2, detailTop + 2, 0xFFFFFF, false);
                int descY = detailTop + 16;
                int linesDrawn = 0;
                for (Component line : tpl.getDescription()) {
                    if (descY + 10 > detailBottom) break; // 防止溢出
                    guiGraphics.drawString(this.font, line, detailLeft + 2, descY, 0xCCCCCC, false);
                    descY += 10;
                    linesDrawn++;
                    if (linesDrawn > 6) break; // 最多显示 6 行
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检查是否点击了右侧面板的某一项
        int panelX = this.width - SIDE_PANEL_WIDTH - SIDE_PANEL_PADDING;
        int panelRight = this.width - SIDE_PANEL_PADDING;
    int panelY = 60 + SIDE_LIST_START_Y; // 与 renderSidePanel 的 panelY 保持一致

        if (mouseX >= panelX + SIDE_PANEL_PADDING && mouseX <= panelRight - SIDE_PANEL_PADDING) {
            int relativeY = (int) mouseY - panelY;
            if (relativeY >= 0 && !sideListItems.isEmpty()) {
                int index = relativeY / SIDE_LIST_ITEM_HEIGHT;
                if (index >= 0 && index < sideListItems.size()) {
                    selectedTemplateIndex = index; // 选中以显示详情
                    // 如果是已安装的 hand 槽位，打开对应实例
                    if (sideListIsInstalled.get(index) && sideListSlotIds.get(index) != null) {
                        ResourceLocation id = sideListTemplateIds.get(index);
                        ModificationTemplate tpl = ArsenalGraftAPI.getModificationTemplate(id);
                        if (tpl != null && "hand".equals(tpl.getSlotType())) {
                            NetworkHandler.sendToServer(new OpenHandInventoryPacket(sideListSlotIds.get(index)));
                        }
                    }
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public void onClose() {
        GuiLayerManager.getInstance().closeAll();
        super.onClose();
    }
}
