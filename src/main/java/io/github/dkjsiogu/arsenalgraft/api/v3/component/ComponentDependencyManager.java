package io.github.dkjsiogu.arsenalgraft.api.v3.component;

import io.github.dkjsiogu.arsenalgraft.api.v3.modification.InstalledSlot;
import net.minecraft.world.entity.player.Player;

import java.util.*;

/**
 * 组件依赖关系管理器
 * 
 * 处理组件之间的依赖、冲突和交互关系
 */
public class ComponentDependencyManager {
    
    private static final Map<String, Set<String>> dependencies = new HashMap<>();
    private static final Map<String, Set<String>> conflicts = new HashMap<>();
    private static final Map<String, Set<String>> synergies = new HashMap<>();
    
    static {
        initializeDefaultRelationships();
    }
    
    /**
     * 初始化默认的组件关系
     */
    private static void initializeDefaultRelationships() {
        // 技能组件可能需要属性组件的支持
        addDependency("skill", "attribute_modification");
        
        // 某些特效可能与其他特效冲突
        addConflict("effect", "skill"); // 示例：某些特效可能与技能冲突
        
        // 协同效应：属性修改 + 特效 = 更强的效果
        addSynergy("attribute_modification", "effect");
    }
    
    /**
     * 添加依赖关系
     */
    public static void addDependency(String component, String requiredComponent) {
        dependencies.computeIfAbsent(component, k -> new HashSet<>()).add(requiredComponent);
        System.out.println("[ComponentDependencyManager] 添加依赖: " + component + " 需要 " + requiredComponent);
    }
    
    /**
     * 添加冲突关系
     */
    public static void addConflict(String component1, String component2) {
        conflicts.computeIfAbsent(component1, k -> new HashSet<>()).add(component2);
        conflicts.computeIfAbsent(component2, k -> new HashSet<>()).add(component1);
        System.out.println("[ComponentDependencyManager] 添加冲突: " + component1 + " 与 " + component2 + " 冲突");
    }
    
    /**
     * 添加协同关系
     */
    public static void addSynergy(String component1, String component2) {
        synergies.computeIfAbsent(component1, k -> new HashSet<>()).add(component2);
        synergies.computeIfAbsent(component2, k -> new HashSet<>()).add(component1);
        System.out.println("[ComponentDependencyManager] 添加协同: " + component1 + " 与 " + component2 + " 有协同效应");
    }
    
    /**
     * 检查组件依赖是否满足
     */
    public static DependencyCheckResult checkDependencies(Player player, String componentType, List<InstalledSlot> existingSlots) {
        Set<String> requiredComponents = dependencies.get(componentType);
        if (requiredComponents == null || requiredComponents.isEmpty()) {
            return new DependencyCheckResult(true, "无依赖要求");
        }
        
        // 获取玩家现有的组件类型
        Set<String> existingComponentTypes = new HashSet<>();
        for (InstalledSlot slot : existingSlots) {
            slot.getComponents().values().forEach(component -> 
                existingComponentTypes.add(component.getComponentType())
            );
        }
        
        // 检查是否满足所有依赖
        Set<String> missingDependencies = new HashSet<>(requiredComponents);
        missingDependencies.removeAll(existingComponentTypes);
        
        if (missingDependencies.isEmpty()) {
            return new DependencyCheckResult(true, "依赖检查通过");
        } else {
            return new DependencyCheckResult(false, "缺少依赖组件: " + String.join(", ", missingDependencies));
        }
    }
    
    /**
     * 检查组件冲突
     */
    public static ConflictCheckResult checkConflicts(Player player, String componentType, List<InstalledSlot> existingSlots) {
        Set<String> conflictingComponents = conflicts.get(componentType);
        if (conflictingComponents == null || conflictingComponents.isEmpty()) {
            return new ConflictCheckResult(false, "无冲突");
        }
        
        // 获取玩家现有的组件类型
        Set<String> existingComponentTypes = new HashSet<>();
        for (InstalledSlot slot : existingSlots) {
            slot.getComponents().values().forEach(component -> 
                existingComponentTypes.add(component.getComponentType())
            );
        }
        
        // 检查是否存在冲突
        Set<String> foundConflicts = new HashSet<>(conflictingComponents);
        foundConflicts.retainAll(existingComponentTypes);
        
        if (foundConflicts.isEmpty()) {
            return new ConflictCheckResult(false, "无冲突");
        } else {
            return new ConflictCheckResult(true, "与现有组件冲突: " + String.join(", ", foundConflicts));
        }
    }
    
    /**
     * 检查协同效应
     */
    public static SynergyCheckResult checkSynergies(Player player, String componentType, List<InstalledSlot> existingSlots) {
        Set<String> synergyComponents = synergies.get(componentType);
        if (synergyComponents == null || synergyComponents.isEmpty()) {
            return new SynergyCheckResult(Collections.emptySet(), "无协同效应");
        }
        
        // 获取玩家现有的组件类型
        Set<String> existingComponentTypes = new HashSet<>();
        for (InstalledSlot slot : existingSlots) {
            slot.getComponents().values().forEach(component -> 
                existingComponentTypes.add(component.getComponentType())
            );
        }
        
        // 查找协同组件
        Set<String> foundSynergies = new HashSet<>(synergyComponents);
        foundSynergies.retainAll(existingComponentTypes);
        
        if (foundSynergies.isEmpty()) {
            return new SynergyCheckResult(Collections.emptySet(), "无协同效应");
        } else {
            return new SynergyCheckResult(foundSynergies, "发现协同效应: " + String.join(", ", foundSynergies));
        }
    }
    
    /**
     * 综合检查（依赖、冲突、协同）
     */
    public static ComponentCompatibilityResult checkCompatibility(Player player, String componentType, List<InstalledSlot> existingSlots) {
        DependencyCheckResult dependencyResult = checkDependencies(player, componentType, existingSlots);
        ConflictCheckResult conflictResult = checkConflicts(player, componentType, existingSlots);
        SynergyCheckResult synergyResult = checkSynergies(player, componentType, existingSlots);
        
        boolean canInstall = dependencyResult.satisfied && !conflictResult.hasConflict;
        
        return new ComponentCompatibilityResult(
            canInstall,
            dependencyResult,
            conflictResult,
            synergyResult
        );
    }
    
    /**
     * 依赖检查结果
     */
    public static class DependencyCheckResult {
        public final boolean satisfied;
        public final String message;
        
        public DependencyCheckResult(boolean satisfied, String message) {
            this.satisfied = satisfied;
            this.message = message;
        }
    }
    
    /**
     * 冲突检查结果
     */
    public static class ConflictCheckResult {
        public final boolean hasConflict;
        public final String message;
        
        public ConflictCheckResult(boolean hasConflict, String message) {
            this.hasConflict = hasConflict;
            this.message = message;
        }
    }
    
    /**
     * 协同检查结果
     */
    public static class SynergyCheckResult {
        public final Set<String> synergyComponents;
        public final String message;
        
        public SynergyCheckResult(Set<String> synergyComponents, String message) {
            this.synergyComponents = synergyComponents;
            this.message = message;
        }
    }
    
    /**
     * 综合兼容性检查结果
     */
    public static class ComponentCompatibilityResult {
        public final boolean canInstall;
        public final DependencyCheckResult dependencyResult;
        public final ConflictCheckResult conflictResult;
        public final SynergyCheckResult synergyResult;
        
        public ComponentCompatibilityResult(boolean canInstall, DependencyCheckResult dependencyResult, 
                                          ConflictCheckResult conflictResult, SynergyCheckResult synergyResult) {
            this.canInstall = canInstall;
            this.dependencyResult = dependencyResult;
            this.conflictResult = conflictResult;
            this.synergyResult = synergyResult;
        }
        
        public String getDetailedMessage() {
            StringBuilder sb = new StringBuilder();
            sb.append("安装检查: ").append(canInstall ? "通过" : "失败").append("\n");
            sb.append("依赖: ").append(dependencyResult.message).append("\n");
            sb.append("冲突: ").append(conflictResult.message).append("\n");
            sb.append("协同: ").append(synergyResult.message);
            return sb.toString();
        }
    }
}
