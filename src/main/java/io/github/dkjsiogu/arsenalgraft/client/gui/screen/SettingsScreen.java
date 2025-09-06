package io.github.dkjsiogu.arsenalgraft.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import io.github.dkjsiogu.arsenalgraft.client.gui.widget.NavigationButton;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * 设置界面 - 配置模组的各种选项
 */
public class SettingsScreen extends Screen {
    
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int SETTING_SPACING = 30;
    private static final int SETTINGS_START_Y = 80;
    
    // 设置项存储
    private final Map<String, Boolean> booleanSettings = new HashMap<>();
    private final Map<String, Integer> integerSettings = new HashMap<>();
    private final Map<String, String> stringSettings = new HashMap<>();
    
    // UI 组件
    private Checkbox showModificationTooltipsCheckbox;
    private Checkbox enableSoundEffectsCheckbox;
    private Checkbox enableParticleEffectsCheckbox;
    private Checkbox enableAutoSaveCheckbox;
    private Button resetSettingsButton;
    
    public SettingsScreen() {
        super(Component.translatable("gui.arsenalgraft.settings.title"));
        loadSettings();
    }
    
    private void loadSettings() {
        // TODO: 从配置文件加载设置
        // 临时默认值
        booleanSettings.put("show_modification_tooltips", true);
        booleanSettings.put("enable_sound_effects", true);
        booleanSettings.put("enable_particle_effects", true);
        booleanSettings.put("enable_auto_save", true);
        
        integerSettings.put("gui_scale", 100);
        integerSettings.put("max_modifications_per_slot", 5);
        
        stringSettings.put("default_modification_style", "realistic");
    }
    
    private void saveSettings() {
        // TODO: 保存设置到配置文件
        /*
        Configuration config = ArsenalGraft.getConfig();
        
        for (Map.Entry<String, Boolean> entry : booleanSettings.entrySet()) {
            config.setBoolean(entry.getKey(), entry.getValue());
        }
        
        for (Map.Entry<String, Integer> entry : integerSettings.entrySet()) {
            config.setInteger(entry.getKey(), entry.getValue());
        }
        
        for (Map.Entry<String, String> entry : stringSettings.entrySet()) {
            config.setString(entry.getKey(), entry.getValue());
        }
        
        config.save();
        */
    }
    
    @Override
    protected void init() {
        super.init();
        
        // 创建导航按钮
        addRenderableWidget(NavigationButton.createBackButton(10, 10, 60, 20));
        
        addRenderableWidget(NavigationButton.createMainMenuButton(
            this.width - 100, 10, 90, 20,
            () -> new ModificationMainScreen()
        ));
        
        // 创建设置项
        createSettingControls();
        
        // 创建操作按钮
        createActionButtons();
    }
    
    private void createSettingControls() {
        int centerX = this.width / 2;
        int currentY = SETTINGS_START_Y;
        
        // 显示改造提示
        showModificationTooltipsCheckbox = new Checkbox(
            centerX - BUTTON_WIDTH / 2, currentY, BUTTON_WIDTH, BUTTON_HEIGHT,
            Component.translatable("gui.arsenalgraft.settings.show_tooltips"),
            booleanSettings.get("show_modification_tooltips")
        );
        addRenderableWidget(showModificationTooltipsCheckbox);
        currentY += SETTING_SPACING;
        
        // 启用音效
        enableSoundEffectsCheckbox = new Checkbox(
            centerX - BUTTON_WIDTH / 2, currentY, BUTTON_WIDTH, BUTTON_HEIGHT,
            Component.translatable("gui.arsenalgraft.settings.enable_sounds"),
            booleanSettings.get("enable_sound_effects")
        );
        addRenderableWidget(enableSoundEffectsCheckbox);
        currentY += SETTING_SPACING;
        
        // 启用粒子效果
        enableParticleEffectsCheckbox = new Checkbox(
            centerX - BUTTON_WIDTH / 2, currentY, BUTTON_WIDTH, BUTTON_HEIGHT,
            Component.translatable("gui.arsenalgraft.settings.enable_particles"),
            booleanSettings.get("enable_particle_effects")
        );
        addRenderableWidget(enableParticleEffectsCheckbox);
        currentY += SETTING_SPACING;
        
        // 启用自动保存
        enableAutoSaveCheckbox = new Checkbox(
            centerX - BUTTON_WIDTH / 2, currentY, BUTTON_WIDTH, BUTTON_HEIGHT,
            Component.translatable("gui.arsenalgraft.settings.enable_autosave"),
            booleanSettings.get("enable_auto_save")
        );
        addRenderableWidget(enableAutoSaveCheckbox);
        currentY += SETTING_SPACING;
        
        // GUI 缩放设置
        Button guiScaleButton = Button.builder(
            Component.translatable("gui.arsenalgraft.settings.gui_scale", 
                                  integerSettings.get("gui_scale") + "%"),
            btn -> cycleGuiScale()
        ).bounds(centerX - BUTTON_WIDTH / 2, currentY, BUTTON_WIDTH, BUTTON_HEIGHT)
         .build();
        addRenderableWidget(guiScaleButton);
        currentY += SETTING_SPACING;
        
        // 每槽最大改造数
        Button maxModsButton = Button.builder(
            Component.translatable("gui.arsenalgraft.settings.max_mods_per_slot", 
                                  integerSettings.get("max_modifications_per_slot")),
            btn -> cycleMaxModifications()
        ).bounds(centerX - BUTTON_WIDTH / 2, currentY, BUTTON_WIDTH, BUTTON_HEIGHT)
         .build();
        addRenderableWidget(maxModsButton);
        currentY += SETTING_SPACING;
        
        // 默认改造风格
        Button styleButton = Button.builder(
            Component.translatable("gui.arsenalgraft.settings.default_style", 
                                  stringSettings.get("default_modification_style")),
            btn -> cycleDefaultStyle()
        ).bounds(centerX - BUTTON_WIDTH / 2, currentY, BUTTON_WIDTH, BUTTON_HEIGHT)
         .build();
        addRenderableWidget(styleButton);
    }
    
    private void createActionButtons() {
        int centerX = this.width / 2;
        int bottomY = this.height - 60;
        
        // 保存设置按钮
        Button saveButton = Button.builder(
            Component.translatable("gui.arsenalgraft.settings.save"),
            btn -> {
                applySettings();
                saveSettings();
                onClose(); // 返回上一个界面
            }
        ).bounds(centerX - 110, bottomY, 100, BUTTON_HEIGHT)
         .build();
        addRenderableWidget(saveButton);
        
        // 重置设置按钮
        resetSettingsButton = Button.builder(
            Component.translatable("gui.arsenalgraft.settings.reset"),
            btn -> resetToDefaults()
        ).bounds(centerX + 10, bottomY, 100, BUTTON_HEIGHT)
         .build();
        addRenderableWidget(resetSettingsButton);
    }
    
    private void cycleGuiScale() {
        int currentScale = integerSettings.get("gui_scale");
        int newScale = switch (currentScale) {
            case 50 -> 75;
            case 75 -> 100;
            case 100 -> 125;
            case 125 -> 150;
            case 150 -> 200;
            default -> 50;
        };
        integerSettings.put("gui_scale", newScale);
        rebuildWidgets(); // 重新创建按钮以更新文本
    }
    
    private void cycleMaxModifications() {
        int current = integerSettings.get("max_modifications_per_slot");
        int newValue = current >= 10 ? 1 : current + 1;
        integerSettings.put("max_modifications_per_slot", newValue);
        rebuildWidgets();
    }
    
    private void cycleDefaultStyle() {
        String current = stringSettings.get("default_modification_style");
        String newStyle = switch (current) {
            case "realistic" -> "fantasy";
            case "fantasy" -> "cyberpunk";
            case "cyberpunk" -> "minimal";
            default -> "realistic";
        };
        stringSettings.put("default_modification_style", newStyle);
        rebuildWidgets();
    }
    
    private void applySettings() {
        // 应用复选框设置
        booleanSettings.put("show_modification_tooltips", 
                          showModificationTooltipsCheckbox.selected());
        booleanSettings.put("enable_sound_effects", 
                          enableSoundEffectsCheckbox.selected());
        booleanSettings.put("enable_particle_effects", 
                          enableParticleEffectsCheckbox.selected());
        booleanSettings.put("enable_auto_save", 
                          enableAutoSaveCheckbox.selected());
    }
    
    private void resetToDefaults() {
        // 重置为默认值
        booleanSettings.clear();
        integerSettings.clear();
        stringSettings.clear();
        loadSettings(); // 重新加载默认值
        rebuildWidgets();
    }
    
    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染背景
        renderBackground(guiGraphics);
        
        // 渲染标题
        guiGraphics.drawCenteredString(this.font, this.title, 
                                     this.width / 2, 25, 0xFFFFFF);
        
        // 渲染说明文本
        Component infoText = Component.translatable("gui.arsenalgraft.settings.info");
        guiGraphics.drawCenteredString(this.font, infoText, 
                                     this.width / 2, 45, 0xCCCCCC);
        
        // 渲染分类标题
        renderCategoryHeaders(guiGraphics);
        
        // 渲染所有组件
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // 渲染设置说明（鼠标悬停）
        renderSettingTooltips(guiGraphics, mouseX, mouseY);
    }
    
    private void renderCategoryHeaders(GuiGraphics guiGraphics) {
        int centerX = this.width / 2;
        
        // 基本设置标题
        Component basicSettingsTitle = Component.translatable("gui.arsenalgraft.settings.basic");
        guiGraphics.drawCenteredString(this.font, basicSettingsTitle, 
                                     centerX, SETTINGS_START_Y - 20, 0xAAAAFF);
        
        // 高级设置标题（在GUI缩放按钮上方）
        Component advancedSettingsTitle = Component.translatable("gui.arsenalgraft.settings.advanced");
        guiGraphics.drawCenteredString(this.font, advancedSettingsTitle, 
                                     centerX, SETTINGS_START_Y + SETTING_SPACING * 4 - 10, 0xAAAAFF);
    }
    
    private void renderSettingTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // TODO: 实现鼠标悬停时显示设置项的详细说明
        /*
        if (isHoveringOverWidget(mouseX, mouseY, someWidget)) {
            List<Component> tooltipLines = List.of(
                Component.translatable("gui.arsenalgraft.settings.tooltip.some_setting")
            );
            guiGraphics.renderTooltip(this.font, tooltipLines, mouseX, mouseY);
        }
        */
    }
    
    @Override
    public void onClose() {
        // 在关闭前应用设置
        applySettings();
        super.onClose();
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    /**
     * 获取布尔类型设置值
     */
    public boolean getBooleanSetting(String key) {
        return booleanSettings.getOrDefault(key, false);
    }
    
    /**
     * 获取整数类型设置值
     */
    public int getIntegerSetting(String key) {
        return integerSettings.getOrDefault(key, 0);
    }
    
    /**
     * 获取字符串类型设置值
     */
    public String getStringSetting(String key) {
        return stringSettings.getOrDefault(key, "");
    }
    
    /**
     * 设置布尔类型设置值
     */
    public void setBooleanSetting(String key, boolean value) {
        booleanSettings.put(key, value);
    }
    
    /**
     * 设置整数类型设置值
     */
    public void setIntegerSetting(String key, int value) {
        integerSettings.put(key, value);
    }
    
    /**
     * 设置字符串类型设置值
     */
    public void setStringSetting(String key, String value) {
        stringSettings.put(key, value);
    }
}
