package io.github.dkjsiogu.arsenalgraft.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import io.github.dkjsiogu.arsenalgraft.client.gui.GuiLayerManager;
import io.github.dkjsiogu.arsenalgraft.client.gui.widget.NavigationButton;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * 改造系统主界面 - 显示人形布局，点击身体部位导航到对应的改造列表
 */
public class ModificationMainScreen extends Screen {
    
    // 界面布局常量
    private static final int BUTTON_WIDTH = 60;
    private static final int BUTTON_HEIGHT = 20;
    
    // 人形布局坐标（相对于屏幕中心）
    private static final int HEAD_X = 0;
    private static final int HEAD_Y = -80;
    
    private static final int LEFT_ARM_X = -80;
    private static final int LEFT_ARM_Y = -40;
    private static final int RIGHT_ARM_X = 80;
    private static final int RIGHT_ARM_Y = -40;
    
    private static final int TORSO_X = 0;
    private static final int TORSO_Y = -20;
    
    private static final int LEFT_LEG_X = -30;
    private static final int LEFT_LEG_Y = 40;
    private static final int RIGHT_LEG_X = 30;
    private static final int RIGHT_LEG_Y = 40;
    
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
        addRenderableWidget(NavigationButton.createBodyPartButton(
            centerX + HEAD_X - BUTTON_WIDTH / 2,
            centerY + HEAD_Y - BUTTON_HEIGHT / 2,
            BUTTON_WIDTH, BUTTON_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.head"),
            "head",
            () -> new ModificationListScreen("head", Component.translatable("gui.arsenalgraft.body_part.head"))
        ));
        
        // 左臂按钮
        addRenderableWidget(NavigationButton.createBodyPartButton(
            centerX + LEFT_ARM_X - BUTTON_WIDTH / 2,
            centerY + LEFT_ARM_Y - BUTTON_HEIGHT / 2,
            BUTTON_WIDTH, BUTTON_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.left_arm"),
            "left_arm",
            () -> new ModificationListScreen("left_arm", Component.translatable("gui.arsenalgraft.body_part.left_arm"))
        ));
        
        // 右臂按钮
        addRenderableWidget(NavigationButton.createBodyPartButton(
            centerX + RIGHT_ARM_X - BUTTON_WIDTH / 2,
            centerY + RIGHT_ARM_Y - BUTTON_HEIGHT / 2,
            BUTTON_WIDTH, BUTTON_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.right_arm"),
            "right_arm",
            () -> new ModificationListScreen("right_arm", Component.translatable("gui.arsenalgraft.body_part.right_arm"))
        ));
        
        // 躯干按钮
        addRenderableWidget(NavigationButton.createBodyPartButton(
            centerX + TORSO_X - BUTTON_WIDTH / 2,
            centerY + TORSO_Y - BUTTON_HEIGHT / 2,
            BUTTON_WIDTH, BUTTON_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.torso"),
            "torso",
            () -> new ModificationListScreen("torso", Component.translatable("gui.arsenalgraft.body_part.torso"))
        ));
        
        // 左腿按钮
        addRenderableWidget(NavigationButton.createBodyPartButton(
            centerX + LEFT_LEG_X - BUTTON_WIDTH / 2,
            centerY + LEFT_LEG_Y - BUTTON_HEIGHT / 2,
            BUTTON_WIDTH, BUTTON_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.left_leg"),
            "left_leg",
            () -> new ModificationListScreen("left_leg", Component.translatable("gui.arsenalgraft.body_part.left_leg"))
        ));
        
        // 右腿按钮
        addRenderableWidget(NavigationButton.createBodyPartButton(
            centerX + RIGHT_LEG_X - BUTTON_WIDTH / 2,
            centerY + RIGHT_LEG_Y - BUTTON_HEIGHT / 2,
            BUTTON_WIDTH, BUTTON_HEIGHT,
            Component.translatable("gui.arsenalgraft.body_part.right_leg"),
            "right_leg",
            () -> new ModificationListScreen("right_leg", Component.translatable("gui.arsenalgraft.body_part.right_leg"))
        ));
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
            () -> new SettingsScreen(), // TODO: 创建设置界面
            settingsContext,
            NavigationButton.ButtonStyle.NAVIGATION, true
        ));
    }
    
    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染背景
        renderBackground(guiGraphics);
        
        // 渲染人形轮廓（可选）
        renderBodyOutline(guiGraphics);
        
        // 渲染标题
        guiGraphics.drawCenteredString(this.font, this.title, 
                                     this.width / 2, 20, 0xFFFFFF);
        
        // 渲染所有组件
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // 渲染工具提示
        renderTooltips(guiGraphics, mouseX, mouseY);
    }
    
    private void renderBodyOutline(GuiGraphics guiGraphics) {
        // TODO: 绘制简单的人形轮廓作为视觉指导
        // 可以使用简单的线条或纹理来表示人体轮廓
        
        // 示例：绘制简单的线条连接各个身体部位
        int lineColor = 0x80FFFFFF; // 半透明白色
        
        // 头部到躯干
        drawLine(guiGraphics, 
                centerX + HEAD_X, centerY + HEAD_Y + BUTTON_HEIGHT / 2,
                centerX + TORSO_X, centerY + TORSO_Y - BUTTON_HEIGHT / 2,
                lineColor);
        
        // 躯干到手臂
        drawLine(guiGraphics,
                centerX + TORSO_X - BUTTON_WIDTH / 2, centerY + TORSO_Y,
                centerX + LEFT_ARM_X + BUTTON_WIDTH / 2, centerY + LEFT_ARM_Y,
                lineColor);
        
        drawLine(guiGraphics,
                centerX + TORSO_X + BUTTON_WIDTH / 2, centerY + TORSO_Y,
                centerX + RIGHT_ARM_X - BUTTON_WIDTH / 2, centerY + RIGHT_ARM_Y,
                lineColor);
        
        // 躯干到腿部
        drawLine(guiGraphics,
                centerX + TORSO_X, centerY + TORSO_Y + BUTTON_HEIGHT / 2,
                centerX + LEFT_LEG_X, centerY + LEFT_LEG_Y - BUTTON_HEIGHT / 2,
                lineColor);
        
        drawLine(guiGraphics,
                centerX + TORSO_X, centerY + TORSO_Y + BUTTON_HEIGHT / 2,
                centerX + RIGHT_LEG_X, centerY + RIGHT_LEG_Y - BUTTON_HEIGHT / 2,
                lineColor);
    }
    
    private void drawLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int color) {
        // 简单的线条绘制（使用填充矩形模拟）
        int dx = x2 - x1;
        int dy = y2 - y1;
        int length = (int) Math.sqrt(dx * dx + dy * dy);
        
        if (length > 0) {
            // 绘制1像素宽的线条
            for (int i = 0; i <= length; i++) {
                int x = x1 + (dx * i) / length;
                int y = y1 + (dy * i) / length;
                guiGraphics.fill(x, y, x + 1, y + 1, color);
            }
        }
    }
    
    private void renderTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // TODO: 为身体部位按钮添加工具提示
        // 显示已安装的改造数量、效果预览等信息
    }
    
    @Override
    public boolean isPauseScreen() {
        return false; // 不暂停游戏
    }
    
    @Override
    public void onClose() {
        GuiLayerManager.getInstance().closeAll();
        super.onClose();
    }
}
