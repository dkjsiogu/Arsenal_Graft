package io.github.dkjsiogu.arsenalgraft.client.gui.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import io.github.dkjsiogu.arsenalgraft.client.gui.GuiLayerManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 通用导航按钮 - 用于 GUI 层级间的跳转和导航
 * 支持动态文本、图标、上下文数据传递和条件启用
 */
public class NavigationButton extends Button {
    
    /**
     * 按钮样式枚举
     */
    public enum ButtonStyle {
        DEFAULT,        // 默认样式
        BODY_PART,      // 身体部位按钮（人形界面用）
        NAVIGATION,     // 导航按钮（返回/前进）
        ACTION          // 操作按钮（确认/取消）
    }
    
    private final GuiLayerManager.GuiLayer targetLayer;
    private final Supplier<Screen> screenSupplier;
    private final Map<String, Object> contextData;
    private final ButtonStyle style;
    private final boolean addToHistory;
    
    /**
     * 创建导航按钮
     * @param x 按钮 X 坐标
     * @param y 按钮 Y 坐标
     * @param width 按钮宽度
     * @param height 按钮高度
     * @param message 按钮显示文本
     * @param targetLayer 目标层级
     * @param screenSupplier 目标屏幕提供者（延迟创建）
     * @param contextData 传递给目标层级的上下文数据
     * @param style 按钮样式
     * @param addToHistory 是否添加到导航历史
     */
    public NavigationButton(int x, int y, int width, int height, Component message,
                           GuiLayerManager.GuiLayer targetLayer, 
                           Supplier<Screen> screenSupplier,
                           Map<String, Object> contextData,
                           ButtonStyle style,
                           boolean addToHistory) {
        super(x, y, width, height, message, btn -> {
            // 点击时执行导航
            GuiLayerManager.getInstance().navigateTo(
                targetLayer, 
                screenSupplier.get(), 
                contextData, 
                addToHistory
            );
        }, DEFAULT_NARRATION);
        
        this.targetLayer = targetLayer;
        this.screenSupplier = screenSupplier;
        this.contextData = new HashMap<>(contextData);
        this.style = style;
        this.addToHistory = addToHistory;
    }
    
    /**
     * 创建身体部位按钮的便捷方法
     */
    public static NavigationButton createBodyPartButton(int x, int y, int width, int height,
                                                       Component message, String bodyPart,
                                                       Supplier<Screen> listScreenSupplier) {
        Map<String, Object> context = new HashMap<>();
        context.put("bodyPart", bodyPart);
        context.put("displayName", message.getString());
        
        return new NavigationButton(x, y, width, height, message,
                                  GuiLayerManager.GuiLayer.MODIFICATION_LIST,
                                  listScreenSupplier, context, ButtonStyle.BODY_PART, true);
    }
    
    /**
     * 创建返回按钮的便捷方法
     */
    public static NavigationButton createBackButton(int x, int y, int width, int height) {
        return new NavigationButton(x, y, width, height, 
                                  Component.translatable("gui.arsenalgraft.back"),
                                  null, // 返回按钮不需要目标层级
                                  () -> null, // 不需要屏幕提供者
                                  new HashMap<>(), ButtonStyle.NAVIGATION, false) {
            @Override
            public void onPress() {
                // 返回按钮的特殊逻辑
                if (!GuiLayerManager.getInstance().goBack()) {
                    // 如果无法返回，关闭界面
                    GuiLayerManager.getInstance().closeAll();
                }
            }
        };
    }
    
    /**
     * 创建主菜单按钮的便捷方法
     */
    public static NavigationButton createMainMenuButton(int x, int y, int width, int height,
                                                       Supplier<Screen> mainScreenSupplier) {
        return new NavigationButton(x, y, width, height,
                                  Component.translatable("gui.arsenalgraft.main_menu"),
                                  GuiLayerManager.GuiLayer.MAIN_SCREEN,
                                  mainScreenSupplier, new HashMap<>(), 
                                  ButtonStyle.NAVIGATION, false);
    }
    
    // Getter 方法
    public GuiLayerManager.GuiLayer getTargetLayer() { return targetLayer; }
    public ButtonStyle getStyle() { return style; }
    public Map<String, Object> getContextData() { return new HashMap<>(contextData); }
    public boolean isAddToHistory() { return addToHistory; }
    
    /**
     * 更新上下文数据
     */
    public void updateContext(String key, Object value) {
        contextData.put(key, value);
    }
    
    /**
     * 获取上下文数据
     */
    @SuppressWarnings("unchecked")
    public <T> T getContextValue(String key, Class<T> type) {
        Object value = contextData.get(key);
        return type.isInstance(value) ? (T) value : null;
    }
    
    /**
     * 根据样式应用不同的渲染效果（可在子类中重写）
     */
    @Override
    protected void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 应用样式特定的渲染逻辑
        switch (style) {
            case BODY_PART -> renderBodyPartStyle(guiGraphics, mouseX, mouseY, partialTick);
            case NAVIGATION -> renderNavigationStyle(guiGraphics, mouseX, mouseY, partialTick);
            case ACTION -> renderActionStyle(guiGraphics, mouseX, mouseY, partialTick);
            default -> super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
    
    protected void renderBodyPartStyle(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 身体部位按钮的特殊渲染（可以添加图标、特殊颜色等）
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        // TODO: 添加身体部位图标渲染
    }
    
    protected void renderNavigationStyle(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 导航按钮的特殊渲染
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        // TODO: 添加导航图标（箭头等）
    }
    
    protected void renderActionStyle(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 操作按钮的特殊渲染
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        // TODO: 添加操作图标
    }
}
