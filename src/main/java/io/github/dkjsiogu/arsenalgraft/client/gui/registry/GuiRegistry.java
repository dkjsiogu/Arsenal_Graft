package io.github.dkjsiogu.arsenalgraft.client.gui.registry;

import io.github.dkjsiogu.arsenalgraft.client.gui.screen.ModificationListScreen;
import io.github.dkjsiogu.arsenalgraft.client.gui.widget.ShapeButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * GUI注册系统 - 管理不同身体部位对应的GUI界面
 * 支持多个身体部位映射到同一个改造类型，以及自定义按钮样式
 */
public class GuiRegistry {
    
    private static final Map<String, ModificationCategory> bodyPartToCategory = new HashMap<>();
    private static final Map<String, CategoryInfo> categoryInfo = new HashMap<>();
    private static final Map<String, ButtonStyleInfo> buttonStyles = new HashMap<>();
    
    /**
     * 改造类别信息
     */
    public static class CategoryInfo {
        private final String categoryId;
        private final Component displayName;
        private final Supplier<Screen> screenSupplier;
        
        public CategoryInfo(String categoryId, Component displayName, Supplier<Screen> screenSupplier) {
            this.categoryId = categoryId;
            this.displayName = displayName;
            this.screenSupplier = screenSupplier;
        }
        
        public String getCategoryId() { return categoryId; }
        public Component getDisplayName() { return displayName; }
        public Supplier<Screen> getScreenSupplier() { return screenSupplier; }
    }
    
    /**
     * 按钮样式信息
     */
    public static class ButtonStyleInfo {
        private final ShapeButton.ButtonShape shape;
        private final int primaryColor;
        private final int secondaryColor;
        private final ResourceLocation texture;
        private final boolean useGradient;
        
        public ButtonStyleInfo(ShapeButton.ButtonShape shape, int primaryColor, int secondaryColor) {
            this(shape, primaryColor, secondaryColor, null, false);
        }
        
        public ButtonStyleInfo(ShapeButton.ButtonShape shape, int primaryColor, int secondaryColor, 
                              ResourceLocation texture, boolean useGradient) {
            this.shape = shape;
            this.primaryColor = primaryColor;
            this.secondaryColor = secondaryColor;
            this.texture = texture;
            this.useGradient = useGradient;
        }
        
        public ShapeButton.ButtonStyle createButtonStyle() {
            ShapeButton.ButtonStyle style = new ShapeButton.ButtonStyle(shape)
                .withColors(primaryColor, secondaryColor)
                .withGradient(useGradient);
            
            if (texture != null) {
                style.withTexture(texture, 0.8f);
            }
            
            return style;
        }
        
        public ShapeButton.ButtonShape getShape() { return shape; }
        public int getPrimaryColor() { return primaryColor; }
        public int getSecondaryColor() { return secondaryColor; }
        public ResourceLocation getTexture() { return texture; }
        public boolean useGradient() { return useGradient; }
    }
    
    /**
     * 改造类别枚举
     */
    public enum ModificationCategory {
        HEAD("head", "gui.arsenalgraft.category.head"),
        TORSO("torso", "gui.arsenalgraft.category.torso"), 
        ARMS("arms", "gui.arsenalgraft.category.arms"),      // 左右手都归类为手臂改造
        LEGS("legs", "gui.arsenalgraft.category.legs"),      // 左右腿都归类为腿部改造
        FEET("feet", "gui.arsenalgraft.category.feet");
        
        private final String categoryId;
        private final String translationKey;
        
        ModificationCategory(String categoryId, String translationKey) {
            this.categoryId = categoryId;
            this.translationKey = translationKey;
        }
        
        public String getCategoryId() { return categoryId; }
        public String getTranslationKey() { return translationKey; }
        public Component getDisplayName() { return Component.translatable(translationKey); }
    }
    
    /**
     * 初始化GUI注册系统
     */
    public static void initialize() {
        // 注册按钮样式
        registerButtonStyles();
        
        // 注册身体部位到改造类别的映射
        registerBodyPartMapping("head", ModificationCategory.HEAD);
        registerBodyPartMapping("torso", ModificationCategory.TORSO);
        registerBodyPartMapping("chest", ModificationCategory.TORSO);  // 胸部也算躯干
        
        // 左右手都映射到手臂改造
        registerBodyPartMapping("left_arm", ModificationCategory.ARMS);
        registerBodyPartMapping("right_arm", ModificationCategory.ARMS);
        registerBodyPartMapping("left_hand", ModificationCategory.ARMS);
        registerBodyPartMapping("right_hand", ModificationCategory.ARMS);
        
        // 左右腿都映射到腿部改造
        registerBodyPartMapping("left_leg", ModificationCategory.LEGS);
        registerBodyPartMapping("right_leg", ModificationCategory.LEGS);
        
        registerBodyPartMapping("feet", ModificationCategory.FEET);
        registerBodyPartMapping("left_foot", ModificationCategory.FEET);
        registerBodyPartMapping("right_foot", ModificationCategory.FEET);
        
        // 注册类别信息和对应的GUI界面
        registerCategoryInfo(ModificationCategory.HEAD, 
            () -> new ModificationListScreen(ModificationCategory.HEAD.getCategoryId(), ModificationCategory.HEAD.getDisplayName()));
        
        registerCategoryInfo(ModificationCategory.TORSO,
            () -> new ModificationListScreen(ModificationCategory.TORSO.getCategoryId(), ModificationCategory.TORSO.getDisplayName()));
        
        registerCategoryInfo(ModificationCategory.ARMS,
            () -> new ModificationListScreen(ModificationCategory.ARMS.getCategoryId(), ModificationCategory.ARMS.getDisplayName()));
        
        registerCategoryInfo(ModificationCategory.LEGS,
            () -> new ModificationListScreen(ModificationCategory.LEGS.getCategoryId(), ModificationCategory.LEGS.getDisplayName()));
        
        registerCategoryInfo(ModificationCategory.FEET,
            () -> new ModificationListScreen(ModificationCategory.FEET.getCategoryId(), ModificationCategory.FEET.getDisplayName()));
    }
    
    /**
     * 注册按钮样式
     */
    private static void registerButtonStyles() {
        // 头部按钮 - 金色人头形状
        buttonStyles.put("head", new ButtonStyleInfo(
            ShapeButton.ButtonShape.HUMAN_HEAD,
            0xFFFFD700,  // 金色
            0xFFFFB347,  // 暖金色
            null, true
        ));
        
        // 躯干按钮 - 蓝色人躯干形状
        buttonStyles.put("torso", new ButtonStyleInfo(
            ShapeButton.ButtonShape.HUMAN_TORSO,
            0xFF4169E1,  // 皇家蓝
            0xFF1E3A8A,  // 深蓝色
            null, true
        ));
        
        // 手臂按钮 - 绿色人手臂形状
        buttonStyles.put("left_arm", new ButtonStyleInfo(
            ShapeButton.ButtonShape.HUMAN_ARM,
            0xFF32CD32,  // 石灰绿
            0xFF228B22,  // 森林绿
            null, true
        ));
        
        buttonStyles.put("right_arm", new ButtonStyleInfo(
            ShapeButton.ButtonShape.HUMAN_ARM,
            0xFF32CD32,  // 石灰绿
            0xFF228B22,  // 森林绿
            null, true
        ));
        
        // 腿部按钮 - 紫色人腿形状
        buttonStyles.put("left_leg", new ButtonStyleInfo(
            ShapeButton.ButtonShape.HUMAN_LEG,
            0xFF9932CC,  // 深兰花紫
            0xFF663399,  // 深紫色
            null, true
        ));
        
        buttonStyles.put("right_leg", new ButtonStyleInfo(
            ShapeButton.ButtonShape.HUMAN_LEG,
            0xFF9932CC,  // 深兰花紫
            0xFF663399,  // 深紫色
            null, true
        ));
        
        // 脚部按钮 - 橙色人脚形状
        buttonStyles.put("left_foot", new ButtonStyleInfo(
            ShapeButton.ButtonShape.HUMAN_FOOT,
            0xFFFF8C00,  // 深橙色
            0xFFFF6347,  // 番茄红
            null, true
        ));
        
        buttonStyles.put("right_foot", new ButtonStyleInfo(
            ShapeButton.ButtonShape.HUMAN_FOOT,
            0xFFFF8C00,  // 深橙色
            0xFFFF6347,  // 番茄红
            null, true
        ));
    }
    
    /**
     * 创建身体部位按钮
     */
    public static ShapeButton createBodyPartButton(int x, int y, int width, int height, 
                                                   String bodyPart, Component displayName, Runnable onPress) {
        ButtonStyleInfo styleInfo = buttonStyles.get(bodyPart);
        if (styleInfo == null) {
            // 默认样式
            styleInfo = new ButtonStyleInfo(ShapeButton.ButtonShape.RECTANGLE, 0xFF888888, 0xFF666666);
        }
        
        ShapeButton.ButtonStyle style = styleInfo.createButtonStyle()
            .withHoverColor(adjustBrightness(styleInfo.getPrimaryColor(), 1.2f))
            .withPressedColor(adjustBrightness(styleInfo.getPrimaryColor(), 0.8f))
            .noBorder()
            .withShadow(false);
        
        return new ShapeButton(x, y, width, height, displayName, style, onPress);
    }
    
    /**
     * 调整颜色亮度
     */
    private static int adjustBrightness(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) * factor));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) * factor));
        int b = Math.min(255, (int) ((color & 0xFF) * factor));
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    /**
     * 注册身体部位到改造类别的映射
     */
    private static void registerBodyPartMapping(String bodyPart, ModificationCategory category) {
        bodyPartToCategory.put(bodyPart, category);
    }
    
    /**
     * 注册改造类别信息
     */
    private static void registerCategoryInfo(ModificationCategory category, Supplier<Screen> screenSupplier) {
        categoryInfo.put(category.getCategoryId(), 
            new CategoryInfo(category.getCategoryId(), category.getDisplayName(), screenSupplier));
    }
    
    /**
     * 根据身体部位获取对应的改造类别
     */
    public static ModificationCategory getCategoryForBodyPart(String bodyPart) {
        return bodyPartToCategory.get(bodyPart);
    }
    
    /**
     * 获取改造类别对应的GUI界面
     */
    public static Screen createScreenForBodyPart(String bodyPart) {
        ModificationCategory category = getCategoryForBodyPart(bodyPart);
        if (category != null) {
            CategoryInfo info = categoryInfo.get(category.getCategoryId());
            if (info != null) {
                return info.getScreenSupplier().get();
            }
        }
        return null;
    }
    
    /**
     * 获取改造类别对应的显示名称
     */
    public static Component getDisplayNameForBodyPart(String bodyPart) {
        ModificationCategory category = getCategoryForBodyPart(bodyPart);
        return category != null ? category.getDisplayName() : Component.literal(bodyPart);
    }
    
    /**
     * 获取所有注册的改造类别
     */
    public static Map<String, CategoryInfo> getAllCategories() {
        return new HashMap<>(categoryInfo);
    }
    
    /**
     * 检查身体部位是否已注册
     */
    public static boolean isBodyPartRegistered(String bodyPart) {
        return bodyPartToCategory.containsKey(bodyPart);
    }
    
    /**
     * 获取所有注册的身体部位
     */
    public static Map<String, ModificationCategory> getAllBodyPartMappings() {
        return new HashMap<>(bodyPartToCategory);
    }
}
