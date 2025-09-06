package io.github.dkjsiogu.arsenalgraft.api.config;

/**
 * 额外槽位配置接口
 * 提供槽位行为的可配置性，避免硬编码
 */
public interface SlotConfiguration {
    
    /**
     * 获取槽位数量
     * @return 槽位数量
     */
    int getSlotCount();
    
    /**
     * 是否允许 Shift+Click 快速移动
     * @return 是否允许快速移动
     */
    boolean isShiftClickEnabled();
    
    /**
     * 是否限制特定物品类型
     * @return 是否有物品限制
     */
    boolean hasItemRestrictions();
    
    /**
     * 获取允许的物品标签
     * @return 物品标签数组，为空则无限制
     */
    String[] getAllowedItemTags();
    
    /**
     * 获取禁止的物品标签
     * @return 物品标签数组，为空则无禁止
     */
    String[] getForbiddenItemTags();
    
    /**
     * 获取最大堆叠数量
     * @return 最大堆叠数量
     */
    int getMaxStackSize();
    
    /**
     * 是否在GUI中显示
     * @return 是否显示
     */
    boolean isVisibleInGui();
    
    /**
     * 获取GUI中的X位置偏移
     * @return X偏移
     */
    int getGuiXOffset();
    
    /**
     * 获取GUI中的Y位置偏移
     * @return Y偏移
     */
    int getGuiYOffset();
    
    /**
     * 获取槽位在GUI中的排列方式
     * @return 排列方式（HORIZONTAL, VERTICAL, GRID）
     */
    SlotArrangement getArrangement();
    
    /**
     * 槽位排列方式枚举
     */
    enum SlotArrangement {
        HORIZONTAL,  // 水平排列
        VERTICAL,    // 垂直排列
        GRID         // 网格排列
    }
}
