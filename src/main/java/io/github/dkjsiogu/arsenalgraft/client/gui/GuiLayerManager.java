package io.github.dkjsiogu.arsenalgraft.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * 分层 GUI 管理器 - 处理界面间的导航、层级管理和状态保持
 * 支持前进/后退导航、命名层级和上下文数据传递
 */
public class GuiLayerManager {
    
    /**
     * GUI 层级标识符枚举
     */
    public enum GuiLayer {
        MAIN_SCREEN("modification_main"),           // 主界面（人形布局）
        MODIFICATION_LIST("modification_list"),    // 改造列表界面
        MODIFICATION_DETAIL("modification_detail"), // 改造详情界面
        SETTINGS("settings");                       // 设置界面
        
        private final String id;
        
        GuiLayer(String id) {
            this.id = id;
        }
        
        public String getId() {
            return id;
        }
    }
    
    /**
     * GUI 层级数据容器
     */
    public static class LayerData {
        private final GuiLayer layer;
        private final Screen screen;
        private final Map<String, Object> context;
        
        public LayerData(GuiLayer layer, Screen screen, Map<String, Object> context) {
            this.layer = layer;
            this.screen = screen;
            this.context = new HashMap<>(context);
        }
        
        public GuiLayer getLayer() { return layer; }
        public Screen getScreen() { return screen; }
        public Map<String, Object> getContext() { return context; }
        
        @SuppressWarnings("unchecked")
        public <T> T getContextValue(String key, Class<T> type) {
            Object value = context.get(key);
            return type.isInstance(value) ? (T) value : null;
        }
    }
    
    private static GuiLayerManager instance;
    private final Deque<LayerData> navigationStack = new ArrayDeque<>();
    private LayerData currentLayer;
    
    private GuiLayerManager() {}
    
    public static GuiLayerManager getInstance() {
        if (instance == null) {
            instance = new GuiLayerManager();
        }
        return instance;
    }
    
    /**
     * 导航到指定层级
     * @param layer 目标层级
     * @param screen 关联的屏幕实例
     * @param context 上下文数据（如选中的身体部位、改造ID等）
     * @param addToHistory 是否添加到导航历史（返回时可用）
     */
    public void navigateTo(GuiLayer layer, Screen screen, Map<String, Object> context, boolean addToHistory) {
        if (currentLayer != null && addToHistory) {
            navigationStack.push(currentLayer);
        }
        
        currentLayer = new LayerData(layer, screen, context);
        
        // 设置屏幕并显示
        net.minecraft.client.Minecraft.getInstance().setScreen(screen);
    }
    
    /**
     * 导航到指定层级（默认添加到历史）
     */
    public void navigateTo(GuiLayer layer, Screen screen, Map<String, Object> context) {
        navigateTo(layer, screen, context, true);
    }
    
    /**
     * 导航到指定层级（无上下文数据）
     */
    public void navigateTo(GuiLayer layer, Screen screen) {
        navigateTo(layer, screen, new HashMap<>(), true);
    }
    
    /**
     * 返回上一层级
     * @return 是否成功返回（false表示已在最顶层）
     */
    public boolean goBack() {
        if (navigationStack.isEmpty()) {
            return false;
        }
        
        LayerData previousLayer = navigationStack.pop();
        currentLayer = previousLayer;
        
        net.minecraft.client.Minecraft.getInstance().setScreen(previousLayer.getScreen());
        return true;
    }
    
    /**
     * 返回到指定层级
     * @param targetLayer 目标层级
     * @return 是否找到并返回到目标层级
     */
    public boolean goBackTo(GuiLayer targetLayer) {
        if (currentLayer != null && currentLayer.getLayer() == targetLayer) {
            return true;
        }
        
        while (!navigationStack.isEmpty()) {
            LayerData layer = navigationStack.pop();
            if (layer.getLayer() == targetLayer) {
                currentLayer = layer;
                net.minecraft.client.Minecraft.getInstance().setScreen(layer.getScreen());
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 清空导航历史并关闭所有界面
     */
    public void closeAll() {
        navigationStack.clear();
        currentLayer = null;
        net.minecraft.client.Minecraft.getInstance().setScreen(null);
    }
    
    /**
     * 获取当前层级
     */
    @Nullable
    public GuiLayer getCurrentLayer() {
        return currentLayer != null ? currentLayer.getLayer() : null;
    }
    
    /**
     * 获取当前层级数据
     */
    @Nullable
    public LayerData getCurrentLayerData() {
        return currentLayer;
    }
    
    /**
     * 检查是否可以返回
     */
    public boolean canGoBack() {
        return !navigationStack.isEmpty();
    }
    
    /**
     * 获取导航历史深度
     */
    public int getNavigationDepth() {
        return navigationStack.size();
    }
}
