package io.github.dkjsiogu.arsenalgraft.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * 通用形状按钮 - 支持不同颜色、形状和贴图的按钮系统
 */
public class ShapeButton extends AbstractButton {
    
    /**
     * 按钮形状枚举
     */
    public enum ButtonShape {
        RECTANGLE,      // 矩形
        CIRCLE,         // 圆形
        HEXAGON,        // 六边形
        DIAMOND,        // 菱形
        OVAL,           // 椭圆
        HUMAN_HEAD,     // 人头形状
        HUMAN_TORSO,    // 人躯干形状
        HUMAN_ARM,      // 人手臂形状
        HUMAN_LEG,      // 人腿部形状
        HUMAN_FOOT      // 人脚部形状
    }
    
    /**
     * 按钮样式配置
     */
    public static class ButtonStyle {
        private ButtonShape shape;
        private int primaryColor;
        private int secondaryColor;
        private int hoverColor;
        private int pressedColor;
        private int borderColor;
        private int borderWidth;
        private ResourceLocation texture;
        private boolean useTexture;
        private float textureAlpha;
        private boolean useGradient;
        private boolean drawBorder;
        private boolean drawShadow;
        
        public ButtonStyle(ButtonShape shape) {
            this.shape = shape;
            this.primaryColor = 0xFF4A90E2;      // 默认蓝色
            this.secondaryColor = 0xFF2E5A87;    // 默认深蓝色
            this.hoverColor = 0xFF5DA0F2;        // 悬停颜色
            this.pressedColor = 0xFF1E3F5F;      // 按下颜色
            this.borderColor = 0xFF000000;       // 边框颜色
            this.borderWidth = 1;
            this.textureAlpha = 1.0f;
            this.useGradient = false;
            this.drawBorder = true;
            this.drawShadow = false;
        }
        
        // Builder模式的设置方法
        public ButtonStyle withColors(int primary, int secondary) {
            this.primaryColor = primary;
            this.secondaryColor = secondary;
            return this;
        }
        
        public ButtonStyle withHoverColor(int color) {
            this.hoverColor = color;
            return this;
        }
        
        public ButtonStyle withPressedColor(int color) {
            this.pressedColor = color;
            return this;
        }
        
        public ButtonStyle withBorder(int color, int width) {
            this.borderColor = color;
            this.borderWidth = width;
            this.drawBorder = true;
            return this;
        }
        
        public ButtonStyle withTexture(ResourceLocation texture, float alpha) {
            this.texture = texture;
            this.textureAlpha = alpha;
            this.useTexture = true;
            return this;
        }
        
        public ButtonStyle withGradient(boolean gradient) {
            this.useGradient = gradient;
            return this;
        }
        
        public ButtonStyle withShadow(boolean shadow) {
            this.drawShadow = shadow;
            return this;
        }
        
        public ButtonStyle noBorder() {
            this.drawBorder = false;
            return this;
        }
        
        // Getters
        public ButtonShape getShape() { return shape; }
        public int getPrimaryColor() { return primaryColor; }
        public int getSecondaryColor() { return secondaryColor; }
        public int getHoverColor() { return hoverColor; }
        public int getPressedColor() { return pressedColor; }
        public int getBorderColor() { return borderColor; }
        public int getBorderWidth() { return borderWidth; }
        public ResourceLocation getTexture() { return texture; }
        public boolean useTexture() { return useTexture; }
        public float getTextureAlpha() { return textureAlpha; }
        public boolean useGradient() { return useGradient; }
        public boolean drawBorder() { return drawBorder; }
        public boolean drawShadow() { return drawShadow; }
    }
    
    private final ButtonStyle style;
    private final Runnable onPress;
    private boolean isPressed = false;
    
    public ShapeButton(int x, int y, int width, int height, Component message, ButtonStyle style, Runnable onPress) {
        super(x, y, width, height, message);
        this.style = style;
        this.onPress = onPress;
    }
    
    @Override
    protected void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 确定当前按钮状态的颜色
        int currentColor = getCurrentColor(mouseX, mouseY);
        
        // 绘制阴影（如果启用）
        if (style.drawShadow()) {
            drawShadow(guiGraphics, currentColor);
        }
        
        // 绘制按钮主体
        drawButtonShape(guiGraphics, currentColor);
        
        // 绘制贴图（如果有）
        if (style.useTexture() && style.getTexture() != null) {
            drawTexture(guiGraphics);
        }
        
        // 绘制边框（如果启用）
        if (style.drawBorder()) {
            drawBorder(guiGraphics);
        }
        
        // 绘制文本
        drawText(guiGraphics);
    }
    
    /**
     * 根据按钮状态获取当前颜色
     */
    private int getCurrentColor(int mouseX, int mouseY) {
        if (!this.active) {
            return applyAlpha(style.getPrimaryColor(), 0.5f);
        } else if (this.isHoveredOrFocused() && isPressed) {
            return style.getPressedColor();
        } else if (this.isHoveredOrFocused()) {
            return style.getHoverColor();
        } else {
            return style.getPrimaryColor();
        }
    }
    
    /**
     * 绘制按钮形状
     */
    private void drawButtonShape(GuiGraphics guiGraphics, int color) {
        switch (style.getShape()) {
            case RECTANGLE:
                drawRectangle(guiGraphics, color);
                break;
            case CIRCLE:
                drawCircle(guiGraphics, color);
                break;
            case HEXAGON:
                drawHexagon(guiGraphics, color);
                break;
            case DIAMOND:
                drawDiamond(guiGraphics, color);
                break;
            case OVAL:
                drawOval(guiGraphics, color);
                break;
            case HUMAN_HEAD:
                drawHumanHead(guiGraphics, color);
                break;
            case HUMAN_TORSO:
                drawHumanTorso(guiGraphics, color);
                break;
            case HUMAN_ARM:
                drawHumanArm(guiGraphics, color);
                break;
            case HUMAN_LEG:
                drawHumanLeg(guiGraphics, color);
                break;
            case HUMAN_FOOT:
                drawHumanFoot(guiGraphics, color);
                break;
            default:
                drawRectangle(guiGraphics, color);
                break;
        }
    }
    
    /**
     * 绘制矩形
     */
    private void drawRectangle(GuiGraphics guiGraphics, int color) {
        if (style.useGradient()) {
            drawGradientRect(guiGraphics, getX(), getY(), getX() + width, getY() + height, 
                           color, style.getSecondaryColor());
        } else {
            guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, color);
        }
    }
    
    /**
     * 绘制圆形
     */
    private void drawCircle(GuiGraphics guiGraphics, int color) {
        int centerX = getX() + width / 2;
        int centerY = getY() + height / 2;
        int radius = Math.min(width, height) / 2;
        
        // 使用像素填充来模拟圆形
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                if (x * x + y * y <= radius * radius) {
                    int pixelColor = color;
                    if (style.useGradient()) {
                        float distance = (float) Math.sqrt(x * x + y * y) / radius;
                        pixelColor = blendColors(color, style.getSecondaryColor(), distance);
                    }
                    guiGraphics.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, pixelColor);
                }
            }
        }
    }
    
    /**
     * 绘制六边形
     */
    private void drawHexagon(GuiGraphics guiGraphics, int color) {
        int centerX = getX() + width / 2;
        int centerY = getY() + height / 2;
        int radius = Math.min(width, height) / 2 - 2;
        
        // 六边形的六个顶点
        int[] xPoints = new int[6];
        int[] yPoints = new int[6];
        
        for (int i = 0; i < 6; i++) {
            double angle = 2.0 * Math.PI * i / 6.0;
            xPoints[i] = centerX + (int) (radius * Math.cos(angle));
            yPoints[i] = centerY + (int) (radius * Math.sin(angle));
        }
        
        drawPolygon(guiGraphics, xPoints, yPoints, color);
    }
    
    /**
     * 绘制菱形
     */
    private void drawDiamond(GuiGraphics guiGraphics, int color) {
        int centerX = getX() + width / 2;
        int centerY = getY() + height / 2;
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        
        int[] xPoints = {centerX, centerX + halfWidth, centerX, centerX - halfWidth};
        int[] yPoints = {centerY - halfHeight, centerY, centerY + halfHeight, centerY};
        
        drawPolygon(guiGraphics, xPoints, yPoints, color);
    }
    
    /**
     * 绘制椭圆
     */
    private void drawOval(GuiGraphics guiGraphics, int color) {
        int centerX = getX() + width / 2;
        int centerY = getY() + height / 2;
        int radiusX = width / 2;
        int radiusY = height / 2;
        
        for (int x = -radiusX; x <= radiusX; x++) {
            for (int y = -radiusY; y <= radiusY; y++) {
                double normalized = (double)(x * x) / (radiusX * radiusX) + (double)(y * y) / (radiusY * radiusY);
                if (normalized <= 1.0) {
                    int pixelColor = color;
                    if (style.useGradient()) {
                        pixelColor = blendColors(color, style.getSecondaryColor(), (float)normalized);
                    }
                    guiGraphics.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, pixelColor);
                }
            }
        }
    }
    
    /**
     * 绘制人头形状
     */
    private void drawHumanHead(GuiGraphics guiGraphics, int color) {
        int centerX = getX() + width / 2;
        int centerY = getY() + height / 3;
        int radius = Math.min(width, height) / 3;
        
        // 绘制头部圆形
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                if (x * x + y * y <= radius * radius) {
                    guiGraphics.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, color);
                }
            }
        }
        
        // 绘制下巴部分
        int jawWidth = radius;
        int jawHeight = height / 4;
        guiGraphics.fill(centerX - jawWidth/2, centerY + radius, 
                        centerX + jawWidth/2, centerY + radius + jawHeight, color);
    }
    
    /**
     * 绘制人躯干形状
     */
    private void drawHumanTorso(GuiGraphics guiGraphics, int color) {
        int torsoWidth = width * 2 / 3;
        int torsoHeight = height * 4 / 5;
        int centerX = getX() + width / 2;
        int startY = getY() + height / 10;
        
        // 主躯干
        guiGraphics.fill(centerX - torsoWidth/2, startY, 
                        centerX + torsoWidth/2, startY + torsoHeight, color);
        
        // 肩膀部分
        int shoulderWidth = width;
        int shoulderHeight = height / 5;
        guiGraphics.fill(centerX - shoulderWidth/2, startY, 
                        centerX + shoulderWidth/2, startY + shoulderHeight, color);
    }
    
    /**
     * 绘制人手臂形状
     */
    private void drawHumanArm(GuiGraphics guiGraphics, int color) {
        int armWidth = width / 3;
        int armHeight = height * 4 / 5;
        int centerX = getX() + width / 2;
        int startY = getY() + height / 10;
        
        // 上臂
        guiGraphics.fill(centerX - armWidth/2, startY, 
                        centerX + armWidth/2, startY + armHeight/2, color);
        
        // 前臂（稍细一些）
        int forearmWidth = armWidth * 3 / 4;
        guiGraphics.fill(centerX - forearmWidth/2, startY + armHeight/2, 
                        centerX + forearmWidth/2, startY + armHeight, color);
        
        // 手部
        int handSize = width / 4;
        guiGraphics.fill(centerX - handSize/2, startY + armHeight, 
                        centerX + handSize/2, startY + armHeight + handSize, color);
    }
    
    /**
     * 绘制人腿部形状
     */
    private void drawHumanLeg(GuiGraphics guiGraphics, int color) {
        int legWidth = width / 2;
        int legHeight = height * 4 / 5;
        int centerX = getX() + width / 2;
        int startY = getY() + height / 10;
        
        // 大腿
        guiGraphics.fill(centerX - legWidth/2, startY, 
                        centerX + legWidth/2, startY + legHeight/2, color);
        
        // 小腿
        int calfWidth = legWidth * 3 / 4;
        guiGraphics.fill(centerX - calfWidth/2, startY + legHeight/2, 
                        centerX + calfWidth/2, startY + legHeight, color);
    }
    
    /**
     * 绘制人脚部形状
     */
    private void drawHumanFoot(GuiGraphics guiGraphics, int color) {
        int footWidth = width * 3 / 4;
        int footHeight = height / 3;
        int centerX = getX() + width / 2;
        int centerY = getY() + height / 2;
        
        // 脚掌
        guiGraphics.fill(centerX - footWidth/2, centerY - footHeight/2, 
                        centerX + footWidth/2, centerY + footHeight/2, color);
        
        // 脚趾部分
        int toeWidth = footWidth / 2;
        int toeHeight = footHeight / 2;
        guiGraphics.fill(centerX - toeWidth/2, centerY - footHeight/2 - toeHeight, 
                        centerX + toeWidth/2, centerY - footHeight/2, color);
    }
    
    /**
     * 绘制多边形
     */
    private void drawPolygon(GuiGraphics guiGraphics, int[] xPoints, int[] yPoints, int color) {
        if (xPoints.length != yPoints.length || xPoints.length < 3) return;
        
        // 简单的扫描线填充算法
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        
        for (int y : yPoints) {
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }
        
        for (int y = minY; y <= maxY; y++) {
            int[] intersections = new int[xPoints.length];
            int intersectionCount = 0;
            
            for (int i = 0; i < xPoints.length; i++) {
                int j = (i + 1) % xPoints.length;
                if ((yPoints[i] <= y && y < yPoints[j]) || (yPoints[j] <= y && y < yPoints[i])) {
                    int x = xPoints[i] + (y - yPoints[i]) * (xPoints[j] - xPoints[i]) / (yPoints[j] - yPoints[i]);
                    intersections[intersectionCount++] = x;
                }
            }
            
            // 排序交点
            java.util.Arrays.sort(intersections, 0, intersectionCount);
            
            // 填充交点之间的区域
            for (int i = 0; i < intersectionCount; i += 2) {
                if (i + 1 < intersectionCount) {
                    guiGraphics.fill(intersections[i], y, intersections[i + 1], y + 1, color);
                }
            }
        }
    }
    
    /**
     * 绘制贴图
     */
    private void drawTexture(GuiGraphics guiGraphics) {
        if (style.getTexture() != null) {
            // 保存当前的渲染状态
            guiGraphics.setColor(1.0f, 1.0f, 1.0f, style.getTextureAlpha());
            
            // 绘制贴图
            guiGraphics.blit(style.getTexture(), getX(), getY(), 0, 0, width, height, width, height);
            
            // 恢复渲染状态
            guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
    
    /**
     * 绘制边框
     */
    private void drawBorder(GuiGraphics guiGraphics) {
        int borderColor = style.getBorderColor();
        int borderWidth = style.getBorderWidth();
        
        for (int i = 0; i < borderWidth; i++) {
            // 上边框
            guiGraphics.fill(getX() - i, getY() - i, getX() + width + i, getY() - i + 1, borderColor);
            // 下边框
            guiGraphics.fill(getX() - i, getY() + height + i - 1, getX() + width + i, getY() + height + i, borderColor);
            // 左边框
            guiGraphics.fill(getX() - i, getY() - i, getX() - i + 1, getY() + height + i, borderColor);
            // 右边框
            guiGraphics.fill(getX() + width + i - 1, getY() - i, getX() + width + i, getY() + height + i, borderColor);
        }
    }
    
    /**
     * 绘制阴影
     */
    private void drawShadow(GuiGraphics guiGraphics, int baseColor) {
        int shadowColor = applyAlpha(0x000000, 0.3f);
        int shadowOffset = 2;
        
        guiGraphics.fill(getX() + shadowOffset, getY() + shadowOffset, 
                        getX() + width + shadowOffset, getY() + height + shadowOffset, shadowColor);
    }
    
    /**
     * 绘制文本
     */
    private void drawText(GuiGraphics guiGraphics) {
        int textColor = this.active ? 0xFFFFFF : 0x808080;
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, this.getMessage(), 
                                     getX() + width / 2, getY() + height / 2 - 4, textColor);
    }
    
    /**
     * 绘制渐变矩形
     */
    private void drawGradientRect(GuiGraphics guiGraphics, int left, int top, int right, int bottom, int startColor, int endColor) {
        guiGraphics.fillGradient(left, top, right, bottom, startColor, endColor);
    }
    
    /**
     * 混合两种颜色
     */
    private int blendColors(int color1, int color2, float ratio) {
        ratio = Math.max(0, Math.min(1, ratio));
        
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        
        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        
        int a = (int) (a1 + (a2 - a1) * ratio);
        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    /**
     * 应用透明度到颜色
     */
    private int applyAlpha(int color, float alpha) {
        alpha = Math.max(0, Math.min(1, alpha));
        int originalAlpha = (color >> 24) & 0xFF;
        int newAlpha = (int) (originalAlpha * alpha);
        return (color & 0x00FFFFFF) | (newAlpha << 24);
    }
    
    @Override
    public void onPress() {
        if (onPress != null) {
            onPress.run();
        }
    }
    
    @Override
    protected void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
