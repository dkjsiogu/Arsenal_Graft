package io.github.dkjsiogu.arsenalgraft.api;

/**
 * 身体部位枚举
 */
public enum BodyPart {
    HEAD("head", "头部", 88, 8, 16, 16),
    CHEST("chest", "胸部", 88, 24, 16, 24),
    LEFT_ARM("left_arm", "左臂", 64, 24, 16, 24),
    RIGHT_ARM("right_arm", "右臂", 112, 24, 16, 24),
    LEFT_HAND("left_hand", "左手", 64, 48, 8, 8),
    RIGHT_HAND("right_hand", "右手", 120, 48, 8, 8),
    LEGS("legs", "腿部", 88, 48, 16, 24),
    LEFT_FOOT("left_foot", "左脚", 88, 72, 8, 8),
    RIGHT_FOOT("right_foot", "右脚", 96, 72, 8, 8);
    
    private final String id;
    private final String displayName;
    private final int x, y, width, height;
    
    BodyPart(String id, String displayName, int x, int y, int width, int height) {
        this.id = id;
        this.displayName = displayName;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    /**
     * 检查坐标是否在这个身体部位内
     */
    public boolean contains(int mouseX, int mouseY, int guiLeft, int guiTop) {
        int absoluteX = guiLeft + x;
        int absoluteY = guiTop + y;
        return mouseX >= absoluteX && mouseX < absoluteX + width && 
               mouseY >= absoluteY && mouseY < absoluteY + height;
    }
}
