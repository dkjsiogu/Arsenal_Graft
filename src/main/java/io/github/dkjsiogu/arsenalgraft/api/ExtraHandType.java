package io.github.dkjsiogu.arsenalgraft.api;

/**
 * 额外手的类型枚举
 */
public enum ExtraHandType {
    /**
     * 普通手 - 可以持有任何物品，提供基础功能
     */
    NORMAL("normal", "普通手"),
    
    /**
     * 战斗手 - 专门用于战斗，可以实现多重攻击
     */
    COMBAT("combat", "战斗手"),
    
    /**
     * 智能手 - 自动防御，智能举盾
     */
    INTELLIGENT("intelligent", "智能手"),
    
    /**
     * 托举手 - 增强被动效果，如独眼巨人之眼的效果放大
     */
    SUPPORT("support", "托举手");
    
    private final String id;
    private final String displayName;
    
    ExtraHandType(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 从ID获取类型
     * @param id 类型ID
     * @return 对应的类型，如果找不到则返回NORMAL
     */
    public static ExtraHandType fromId(String id) {
        for (ExtraHandType type : values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        return NORMAL; // 默认返回普通手
    }
}
