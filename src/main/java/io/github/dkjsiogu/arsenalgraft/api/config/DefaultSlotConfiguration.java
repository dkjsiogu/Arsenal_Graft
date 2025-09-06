package io.github.dkjsiogu.arsenalgraft.api.config;

/**
 * 默认槽位配置实现
 * 提供合理的默认值，支持自定义覆盖
 */
public class DefaultSlotConfiguration implements SlotConfiguration {
    
    private final int slotCount;
    private final boolean shiftClickEnabled;
    private final boolean hasItemRestrictions;
    private final String[] allowedItemTags;
    private final String[] forbiddenItemTags;
    private final int maxStackSize;
    private final boolean visibleInGui;
    private final int guiXOffset;
    private final int guiYOffset;
    private final SlotArrangement arrangement;
    
    public DefaultSlotConfiguration(Builder builder) {
        this.slotCount = builder.slotCount;
        this.shiftClickEnabled = builder.shiftClickEnabled;
        this.hasItemRestrictions = builder.hasItemRestrictions;
        this.allowedItemTags = builder.allowedItemTags;
        this.forbiddenItemTags = builder.forbiddenItemTags;
        this.maxStackSize = builder.maxStackSize;
        this.visibleInGui = builder.visibleInGui;
        this.guiXOffset = builder.guiXOffset;
        this.guiYOffset = builder.guiYOffset;
        this.arrangement = builder.arrangement;
    }
    
    @Override
    public int getSlotCount() {
        return slotCount;
    }
    
    @Override
    public boolean isShiftClickEnabled() {
        return shiftClickEnabled;
    }
    
    @Override
    public boolean hasItemRestrictions() {
        return hasItemRestrictions;
    }
    
    @Override
    public String[] getAllowedItemTags() {
        return allowedItemTags != null ? allowedItemTags.clone() : new String[0];
    }
    
    @Override
    public String[] getForbiddenItemTags() {
        return forbiddenItemTags != null ? forbiddenItemTags.clone() : new String[0];
    }
    
    @Override
    public int getMaxStackSize() {
        return maxStackSize;
    }
    
    @Override
    public boolean isVisibleInGui() {
        return visibleInGui;
    }
    
    @Override
    public int getGuiXOffset() {
        return guiXOffset;
    }
    
    @Override
    public int getGuiYOffset() {
        return guiYOffset;
    }
    
    @Override
    public SlotArrangement getArrangement() {
        return arrangement;
    }
    
    /**
     * 建造者模式用于创建配置
     */
    public static class Builder {
        private int slotCount = 1;
        private boolean shiftClickEnabled = true;
        private boolean hasItemRestrictions = false;
        private String[] allowedItemTags = new String[0];
        private String[] forbiddenItemTags = new String[0];
        private int maxStackSize = 64;
        private boolean visibleInGui = true;
        private int guiXOffset = 0;
        private int guiYOffset = 0;
        private SlotArrangement arrangement = SlotArrangement.HORIZONTAL;
        
        public Builder slotCount(int count) {
            this.slotCount = count;
            return this;
        }
        
        public Builder shiftClickEnabled(boolean enabled) {
            this.shiftClickEnabled = enabled;
            return this;
        }
        
        public Builder hasItemRestrictions(boolean hasRestrictions) {
            this.hasItemRestrictions = hasRestrictions;
            return this;
        }
        
        public Builder allowedItemTags(String... tags) {
            this.allowedItemTags = tags;
            return this;
        }
        
        public Builder forbiddenItemTags(String... tags) {
            this.forbiddenItemTags = tags;
            return this;
        }
        
        public Builder maxStackSize(int size) {
            this.maxStackSize = size;
            return this;
        }
        
        public Builder visibleInGui(boolean visible) {
            this.visibleInGui = visible;
            return this;
        }
        
        public Builder guiOffset(int xOffset, int yOffset) {
            this.guiXOffset = xOffset;
            this.guiYOffset = yOffset;
            return this;
        }
        
        public Builder arrangement(SlotArrangement arrangement) {
            this.arrangement = arrangement;
            return this;
        }
        
        public DefaultSlotConfiguration build() {
            return new DefaultSlotConfiguration(this);
        }
    }
    
    /**
     * 创建额外手槽位的默认配置
     */
    public static SlotConfiguration createExtraHandConfig() {
        return new Builder()
            .slotCount(4) // 默认4个额外手槽位
            .shiftClickEnabled(true)
            .hasItemRestrictions(false) // 额外手可以拿任何物品
            .maxStackSize(1) // 手只能拿一个物品
            .visibleInGui(true)
            .guiOffset(0, 0)
            .arrangement(SlotArrangement.HORIZONTAL)
            .build();
    }
    
    /**
     * 创建装甲槽位的默认配置（示例）
     */
    public static SlotConfiguration createArmorSlotConfig() {
        return new Builder()
            .slotCount(4) // 头盔、胸甲、护腿、靴子
            .shiftClickEnabled(true)
            .hasItemRestrictions(true)
            .allowedItemTags("forge:armor") // 只允许装甲
            .maxStackSize(1)
            .visibleInGui(true)
            .guiOffset(0, -80)
            .arrangement(SlotArrangement.VERTICAL)
            .build();
    }
}
