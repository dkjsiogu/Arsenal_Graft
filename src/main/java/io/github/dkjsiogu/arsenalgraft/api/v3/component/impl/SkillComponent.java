package io.github.dkjsiogu.arsenalgraft.api.v3.component.impl;

import io.github.dkjsiogu.arsenalgraft.api.v3.component.IModificationComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 技能组件
 * 
 * 提供特殊技能和能力，如双重跳跃、冲刺、隐身等
 */
public class SkillComponent implements IModificationComponent {
    
    private static final String COMPONENT_TYPE = "skill";
    
    private boolean active = false;
    private final Map<String, Object> skillData = new HashMap<>();
    private final Map<String, Consumer<Player>> skills = new HashMap<>();
    
    // 技能冷却时间管理
    private final Map<String, Long> cooldowns = new HashMap<>();
    
    public SkillComponent() {
        initializeDefaultSkills();
    }
    
    /**
     * 初始化默认技能
     */
    private void initializeDefaultSkills() {
        // 双重跳跃技能
        skills.put("double_jump", player -> {
            if (canUseSkill("double_jump", 1000)) { // 1秒冷却
                // 给予向上的速度
                player.setDeltaMovement(player.getDeltaMovement().add(0, 0.5, 0));
                player.hasImpulse = true;
                setCooldown("double_jump");
                System.out.println("[SkillComponent] " + player.getName().getString() + " 使用了双重跳跃");
            }
        });
        
        // 快速冲刺技能
        skills.put("dash", player -> {
            if (canUseSkill("dash", 3000)) { // 3秒冷却
                // 在玩家面向的方向上给予推进力
                double speed = 1.5;
                double yaw = Math.toRadians(player.getYRot());
                double dx = -Math.sin(yaw) * speed;
                double dz = Math.cos(yaw) * speed;
                
                player.setDeltaMovement(player.getDeltaMovement().add(dx, 0, dz));
                player.hasImpulse = true;
                setCooldown("dash");
                System.out.println("[SkillComponent] " + player.getName().getString() + " 使用了冲刺");
            }
        });
        
        // 临时隐身技能
        skills.put("stealth", player -> {
            if (canUseSkill("stealth", 30000)) { // 30秒冷却
                // 给予隐身效果
                player.setInvisible(true);
                // 5秒后移除隐身
                // 注意：在实际实现中，你需要使用适当的调度器
                System.out.println("[SkillComponent] " + player.getName().getString() + " 使用了隐身");
                setCooldown("stealth");
            }
        });
    }
    
    /**
     * 添加自定义技能
     */
    public void addSkill(String skillName, Consumer<Player> skillAction) {
        skills.put(skillName, skillAction);
    }
    
    /**
     * 移除技能
     */
    public void removeSkill(String skillName) {
        skills.remove(skillName);
        cooldowns.remove(skillName);
    }
    
    /**
     * 使用技能
     */
    public void useSkill(String skillName, Player player) {
        if (!active) return;
        
        Consumer<Player> skill = skills.get(skillName);
        if (skill != null) {
            skill.accept(player);
        }
    }
    
    /**
     * 激活技能（网络调用接口）
     * 这个方法被网络包调用，提供额外的验证和日志
     */
    public void activateSkill(Player player, String skillName) {
        if (!active) {
            System.out.println("[SkillComponent] 技能组件未激活，无法使用技能: " + skillName);
            return;
        }
        
        if (!skills.containsKey(skillName)) {
            System.out.println("[SkillComponent] 未知技能: " + skillName);
            return;
        }
        
        // 记录技能使用
        System.out.println("[SkillComponent] " + player.getName().getString() + " 尝试激活技能: " + skillName);
        
        // 执行技能
        useSkill(skillName, player);
    }
    
    /**
     * 检查技能是否可用（冷却时间）
     */
    private boolean canUseSkill(String skillName, long cooldownMs) {
        Long lastUsed = cooldowns.get(skillName);
        if (lastUsed == null) return true;
        
        return System.currentTimeMillis() - lastUsed >= cooldownMs;
    }
    
    /**
     * 设置技能冷却
     */
    private void setCooldown(String skillName) {
        cooldowns.put(skillName, System.currentTimeMillis());
    }
    
    /**
     * 获取技能剩余冷却时间（毫秒）
     */
    public long getRemainingCooldown(String skillName, long totalCooldownMs) {
        Long lastUsed = cooldowns.get(skillName);
        if (lastUsed == null) return 0;
        
        long elapsed = System.currentTimeMillis() - lastUsed;
        return Math.max(0, totalCooldownMs - elapsed);
    }
    
    /**
     * 设置技能数据
     */
    public void setSkillData(String key, Object value) {
        skillData.put(key, value);
    }
    
    /**
     * 获取技能数据
     */
    @SuppressWarnings("unchecked")
    public <T> T getSkillData(String key, Class<T> type) {
        Object value = skillData.get(key);
        if (type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
    
    @Override
    public String getComponentType() {
        return COMPONENT_TYPE;
    }
    
    @Override
    public boolean isActive() {
        return active;
    }
    
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public void onInstall(Player player) {
        System.out.println("[SkillComponent] 为 " + player.getName().getString() + " 安装技能组件");
        System.out.println("[SkillComponent] 可用技能: " + String.join(", ", skills.keySet()));
    }
    
    @Override
    public void onUninstall(Player player) {
        System.out.println("[SkillComponent] 为 " + player.getName().getString() + " 卸载技能组件");
        // 清理所有冷却时间
        cooldowns.clear();
    }
    
    @Override
    public void tick(Player player) {
        if (!active) return;
        
        // 可以在这里处理需要持续更新的技能效果
        // 例如：检查隐身状态的持续时间
    }
    
    @Override
    public IModificationComponent copy() {
        SkillComponent copy = new SkillComponent();
        copy.active = this.active;
        copy.skillData.putAll(this.skillData);
        // 注意：技能函数本身不会被复制，因为它们是在构造函数中初始化的
        return copy;
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("componentType", COMPONENT_TYPE);
        tag.putBoolean("active", active);
        
        // 序列化技能数据
        CompoundTag dataTag = new CompoundTag();
        for (Map.Entry<String, Object> entry : skillData.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                dataTag.putString(entry.getKey(), (String) value);
            } else if (value instanceof Integer) {
                dataTag.putInt(entry.getKey(), (Integer) value);
            } else if (value instanceof Double) {
                dataTag.putDouble(entry.getKey(), (Double) value);
            } else if (value instanceof Boolean) {
                dataTag.putBoolean(entry.getKey(), (Boolean) value);
            }
        }
        tag.put("skillData", dataTag);
        
        // 序列化冷却时间
        CompoundTag cooldownTag = new CompoundTag();
        for (Map.Entry<String, Long> entry : cooldowns.entrySet()) {
            cooldownTag.putLong(entry.getKey(), entry.getValue());
        }
        tag.put("cooldowns", cooldownTag);
        
        return tag;
    }
    
    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.active = tag.getBoolean("active");
        
        // 反序列化技能数据
        if (tag.contains("skillData")) {
            CompoundTag dataTag = tag.getCompound("skillData");
            skillData.clear();
            
            for (String key : dataTag.getAllKeys()) {
                Tag valueTag = dataTag.get(key);
                // 根据标签类型恢复数据
                if (valueTag instanceof net.minecraft.nbt.StringTag) {
                    skillData.put(key, dataTag.getString(key));
                } else if (valueTag instanceof net.minecraft.nbt.IntTag) {
                    skillData.put(key, dataTag.getInt(key));
                } else if (valueTag instanceof net.minecraft.nbt.DoubleTag) {
                    skillData.put(key, dataTag.getDouble(key));
                } else if (valueTag instanceof net.minecraft.nbt.ByteTag) {
                    skillData.put(key, dataTag.getBoolean(key));
                }
            }
        }
        
        // 反序列化冷却时间
        if (tag.contains("cooldowns")) {
            CompoundTag cooldownTag = tag.getCompound("cooldowns");
            cooldowns.clear();
            
            for (String key : cooldownTag.getAllKeys()) {
                cooldowns.put(key, cooldownTag.getLong(key));
            }
        }
    }
}
