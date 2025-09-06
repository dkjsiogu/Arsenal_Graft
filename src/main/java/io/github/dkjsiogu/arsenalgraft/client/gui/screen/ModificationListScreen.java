package io.github.dkjsiogu.arsenalgraft.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import io.github.dkjsiogu.arsenalgraft.client.gui.widget.NavigationButton;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationTemplate;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * 改造列表界面 - 显示特定身体部位的所有可用改造
 */
public class ModificationListScreen extends Screen {
    
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 25;
    private static final int LIST_START_Y = 60;
    
    private final String bodyPart;
    private final Component bodyPartDisplayName;
    private final List<ModificationTemplate> availableModifications;
    
    private int scrollOffset = 0;
    private int maxScrollOffset = 0;
    
    public ModificationListScreen(String bodyPart, Component bodyPartDisplayName) {
        super(Component.translatable("gui.arsenalgraft.modification_list.title", bodyPartDisplayName));
        this.bodyPart = bodyPart;
        this.bodyPartDisplayName = bodyPartDisplayName;
        this.availableModifications = new ArrayList<>();
        
        // TODO: 从 ModificationManager 获取该身体部位的可用改造
        loadAvailableModifications();
    }
    
    private void loadAvailableModifications() {
        // TODO: 实现从 ModificationManager 加载改造模板
        // 示例数据用于测试
        /*
        ModificationManager manager = ServiceRegistry.getService(ModificationManager.class);
        if (manager != null) {
            availableModifications.addAll(manager.getModificationsForBodyPart(bodyPart));
        }
        */
        
        // 临时测试数据
        availableModifications.clear();
        // 添加一些虚拟改造用于界面测试
        for (int i = 1; i <= 10; i++) {
            // TODO: 使用真实的 ModificationTemplate 实例
            // availableModifications.add(new TestModificationTemplate(bodyPart + "_mod_" + i));
        }
    }
    
    @Override
    protected void init() {
        super.init();
        
        // 计算最大滚动偏移
        int totalHeight = availableModifications.size() * BUTTON_SPACING;
        int availableHeight = this.height - LIST_START_Y - 60;
        maxScrollOffset = Math.max(0, totalHeight - availableHeight);
        
        // 创建返回按钮
        addRenderableWidget(NavigationButton.createBackButton(10, 10, 60, 20));
        
        // 创建主菜单按钮
        addRenderableWidget(NavigationButton.createMainMenuButton(
            this.width - 100, 10, 90, 20,
            () -> new ModificationMainScreen()
        ));
        
        // 创建改造列表按钮
        createModificationButtons();
        
        // 创建滚动控制按钮
        if (maxScrollOffset > 0) {
            createScrollButtons();
        }
    }
    
    private void createModificationButtons() {
        int startX = (this.width - BUTTON_WIDTH) / 2;
        int currentY = LIST_START_Y - scrollOffset;
        
        for (int i = 0; i < availableModifications.size(); i++) {
            ModificationTemplate template = availableModifications.get(i);
            
            // 只创建可见区域内的按钮
            if (currentY >= LIST_START_Y - BUTTON_HEIGHT && 
                currentY <= this.height - 60) {
                
                Button modButton = Button.builder(
                    Component.literal("Test Modification " + (i + 1)), // TODO: 使用真实的模板名称
                    btn -> openModificationDetail(template)
                ).bounds(startX, currentY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
                
                addRenderableWidget(modButton);
            }
            
            currentY += BUTTON_SPACING;
        }
    }
    
    private void createScrollButtons() {
        // 向上滚动按钮
        Button scrollUpButton = Button.builder(
            Component.literal("↑"),
            btn -> scrollUp()
        ).bounds(this.width - 30, LIST_START_Y, 20, 20).build();
        
        // 向下滚动按钮
        Button scrollDownButton = Button.builder(
            Component.literal("↓"),
            btn -> scrollDown()
        ).bounds(this.width - 30, this.height - 80, 20, 20).build();
        
        addRenderableWidget(scrollUpButton);
        addRenderableWidget(scrollDownButton);
    }
    
    private void scrollUp() {
        scrollOffset = Math.max(0, scrollOffset - BUTTON_SPACING * 3);
        rebuildWidgets(); // 重新创建按钮以更新位置
    }
    
    private void scrollDown() {
        scrollOffset = Math.min(maxScrollOffset, scrollOffset + BUTTON_SPACING * 3);
        rebuildWidgets(); // 重新创建按钮以更新位置
    }
    
    private void openModificationDetail(ModificationTemplate template) {
        // TODO: 导航到改造详情界面
        /*
        Map<String, Object> context = new HashMap<>();
        context.put("template", template);
        context.put("bodyPart", bodyPart);
        
        GuiLayerManager.getInstance().navigateTo(
            GuiLayerManager.GuiLayer.MODIFICATION_DETAIL,
            new ModificationDetailScreen(template, bodyPart),
            context
        );
        */
    }
    
    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染背景
        renderBackground(guiGraphics);
        
        // 渲染标题
        guiGraphics.drawCenteredString(this.font, this.title, 
                                     this.width / 2, 25, 0xFFFFFF);
        
        // 渲染身体部位信息
        Component infoText = Component.translatable("gui.arsenalgraft.modification_list.info", 
                                                  bodyPartDisplayName, availableModifications.size());
        guiGraphics.drawCenteredString(this.font, infoText, 
                                     this.width / 2, 45, 0xCCCCCC);
        
        // 创建裁剪区域用于滚动列表
        guiGraphics.enableScissor(0, LIST_START_Y, this.width, this.height - 60);
        
        // 渲染所有组件
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // 禁用裁剪
        guiGraphics.disableScissor();
        
        // 渲染滚动指示器
        if (maxScrollOffset > 0) {
            renderScrollIndicator(guiGraphics);
        }
    }
    
    private void renderScrollIndicator(GuiGraphics guiGraphics) {
        // 渲染滚动条
        int scrollBarX = this.width - 10;
        int scrollBarY = LIST_START_Y;
        int scrollBarHeight = this.height - LIST_START_Y - 60;
        
        // 背景
        guiGraphics.fill(scrollBarX, scrollBarY, scrollBarX + 5, 
                        scrollBarY + scrollBarHeight, 0x40FFFFFF);
        
        // 滚动指示器
        if (maxScrollOffset > 0) {
            int indicatorHeight = Math.max(10, (scrollBarHeight * scrollBarHeight) / 
                                             (scrollBarHeight + maxScrollOffset));
            int indicatorY = scrollBarY + (scrollOffset * (scrollBarHeight - indicatorHeight)) / maxScrollOffset;
            
            guiGraphics.fill(scrollBarX + 1, indicatorY, scrollBarX + 4, 
                           indicatorY + indicatorHeight, 0xFFFFFFFF);
        }
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (maxScrollOffset > 0) {
            if (delta > 0) {
                scrollUp();
            } else if (delta < 0) {
                scrollDown();
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    /**
     * 获取当前选中的身体部位
     */
    public String getBodyPart() {
        return bodyPart;
    }
    
    /**
     * 获取可用改造列表
     */
    public List<ModificationTemplate> getAvailableModifications() {
        return new ArrayList<>(availableModifications);
    }
    
    /**
     * 刷新改造列表
     */
    public void refreshModifications() {
        loadAvailableModifications();
        rebuildWidgets();
    }
}
