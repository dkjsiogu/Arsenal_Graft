package io.github.dkjsiogu.arsenalgraft.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import io.github.dkjsiogu.arsenalgraft.client.gui.GuiLayerManager;
import io.github.dkjsiogu.arsenalgraft.client.gui.widget.NavigationButton;
import io.github.dkjsiogu.arsenalgraft.client.gui.widget.ShapeButton;
import io.github.dkjsiogu.arsenalgraft.client.gui.registry.GuiRegistry;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

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
    
    private int centerX;
    private int centerY;
    
    public ModificationMainScreen() {
        super(Component.translatable("gui.arsenalgraft.main_screen.title"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        // 计算屏幕中心点
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
        
        // 创建身体部位按钮
        createBodyPartButtons();
        
        // 创建底部导航按钮
        createNavigationButtons();
    }
    
    private void createBodyPartButtons() {
        // 头部按钮
        addRenderableWidget(createBodyPartButton(
            "head", HEAD_X, HEAD_Y, HEAD_WIDTH, HEAD_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.head")
        ));
        
        // 躯干按钮
        addRenderableWidget(createBodyPartButton(
            "torso", TORSO_X, TORSO_Y, TORSO_WIDTH, TORSO_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.torso")
        ));
        
        // 左臂按钮 (映射到手臂改造)
        addRenderableWidget(createBodyPartButton(
            "left_arm", LEFT_ARM_X, LEFT_ARM_Y, ARM_WIDTH, ARM_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.left_arm")
        ));
        
        // 右臂按钮 (映射到手臂改造)
        addRenderableWidget(createBodyPartButton(
            "right_arm", RIGHT_ARM_X, RIGHT_ARM_Y, ARM_WIDTH, ARM_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.right_arm")
        ));
        
        // 左腿按钮 (映射到腿部改造)
        addRenderableWidget(createBodyPartButton(
            "left_leg", LEFT_LEG_X, LEFT_LEG_Y, LEG_WIDTH, LEG_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.left_leg")
        ));
        
        // 右腿按钮 (映射到腿部改造)
        addRenderableWidget(createBodyPartButton(
            "right_leg", RIGHT_LEG_X, RIGHT_LEG_Y, LEG_WIDTH, LEG_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.right_leg")
        ));
        
        // 左脚按钮
        addRenderableWidget(createBodyPartButton(
            "left_foot", LEFT_FOOT_X, LEFT_FOOT_Y, FOOT_WIDTH, FOOT_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.left_foot")
        ));
        
        // 右脚按钮
        addRenderableWidget(createBodyPartButton(
            "right_foot", RIGHT_FOOT_X, RIGHT_FOOT_Y, FOOT_WIDTH, FOOT_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.right_foot")
        ));
    }
    
    /**
     * 创建身体部位按钮的辅助方法
     */
    private ShapeButton createBodyPartButton(String bodyPart, int offsetX, int offsetY, int width, int height, Component displayName) {
        int x = centerX + offsetX - width / 2;
        int y = centerY + offsetY - height / 2;
        
        // 使用新的形状按钮系统
        return GuiRegistry.createBodyPartButton(
            x, y, width, height,
            bodyPart, displayName,
            () -> {
                var screen = GuiRegistry.createScreenForBodyPart(bodyPart);
                if (screen != null && minecraft != null) {
                    minecraft.setScreen(screen);
                }
            }
        );
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
