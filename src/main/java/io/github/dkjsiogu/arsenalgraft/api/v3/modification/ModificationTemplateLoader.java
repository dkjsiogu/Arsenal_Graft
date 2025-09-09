package io.github.dkjsiogu.arsenalgraft.api.v3.modification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.IModificationComponent;
import io.github.dkjsiogu.arsenalgraft.core.service.ServiceRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 改造模板加载器
 * 
 * 统一处理JSON和KubeJS定义的改造模板。
 * 实现"单一数据后端"原则：
 * - JSON文件和KubeJS脚本都解析成同一种内存结构
 * - KubeJS API本质上是代码形式的"JSON构建器"
 * - 两种方式调用完全一致的底层逻辑
 */
public class ModificationTemplateLoader extends SimpleJsonResourceReloadListener {
    
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    
    public ModificationTemplateLoader() {
        super(GSON, "modifications"); // data/arsenalgraft/modifications/*.json
    }
    
    @Override
    protected void apply(@Nonnull Map<ResourceLocation, JsonElement> object, @Nonnull ResourceManager resourceManager, @Nonnull ProfilerFiller profiler) {
        LOGGER.info("开始加载Arsenal Graft改造模板... (命名空间过滤: data/*/modifications/*.json under arsenalgraft)");
        if (object.isEmpty()) {
            LOGGER.warn("未发现任何改造模板 JSON (检查: data/arsenalgraft/modifications/*.json 路径是否存在以及资源是否打进数据包)");
        } else {
            LOGGER.debug("发现模板文件列表: {}", object.keySet());
        }
        
        ModificationManager manager = ServiceRegistry.getInstance().getService(ModificationManager.class);
        if (manager == null) {
            LOGGER.error("ModificationManager服务未初始化，无法加载改造模板");
            return;
        }
        
    // 清除旧模板 (完整重载)
    try { manager.clearTemplates(); } catch (Exception e) { LOGGER.warn("无法清除旧模板", e); }
        
        int loaded = 0;
        int failed = 0;
        
        for (Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
            ResourceLocation id = entry.getKey();
            JsonElement json = entry.getValue();
            
            try {
                if (json.isJsonObject()) {
                    ModificationTemplate template = parseTemplate(id, json.getAsJsonObject());
                    if (template != null) {
                        manager.registerTemplate(id, template);
                        loaded++;
                        LOGGER.debug("成功加载改造模板: {}", id);
                    } else {
                        LOGGER.warn("解析改造模板失败: {}", id);
                        failed++;
                    }
                } else {
                    LOGGER.warn("改造模板不是有效的JSON对象: {}", id);
                    failed++;
                }
            } catch (Exception e) {
                LOGGER.error("加载改造模板时发生错误: {}", id, e);
                failed++;
            }
        }
        
        LOGGER.info("Arsenal Graft改造模板加载完成: 成功 {}, 失败 {}", loaded, failed);
    }
    
    /**
     * 解析JSON为改造模板
     * 这个方法同时被JSON加载器和KubeJS构建器使用
     * 
     * @param id 模板ID
     * @param json JSON对象
     * @return 解析的模板，失败时返回null
     */
    public static ModificationTemplate parseTemplate(ResourceLocation id, JsonObject json) {
        try {
            ModificationTemplate.Builder builder = new ModificationTemplate.Builder(id);
            
            // 解析基础属性
            if (json.has("name")) {
                builder.displayName(Component.literal(json.get("name").getAsString()));
            } else {
                builder.displayName(Component.literal(id.getPath()));
            }
            
            if (json.has("description")) {
                String desc = json.get("description").getAsString();
                builder.description(List.of(Component.literal(desc)));
            }
            
            if (json.has("slotType")) {
                builder.slotType(json.get("slotType").getAsString());
            }
            
            if (json.has("maxInstances")) {
                builder.maxInstallCount(json.get("maxInstances").getAsInt());
            }
            
            if (json.has("requirements")) {
                // TODO: 未来可添加 requirements 校验逻辑
            }
            
            // 解析组件
            if (json.has("components")) {
                JsonObject components = json.getAsJsonObject("components");
                for (String componentType : components.keySet()) {
                    JsonObject componentConfig = components.getAsJsonObject(componentType);
                    
                    IModificationComponent component = parseComponent(componentType, componentConfig);
                    if (component != null) {
                        builder.addComponent(componentType, component);
                    } else {
                        LOGGER.warn("无法解析组件: {} in template: {}", componentType, id);
                    }
                }
            }
            
            return builder.build();
            
        } catch (Exception e) {
            LOGGER.error("解析改造模板失败: {}", id, e);
            return null;
        }
    }
    
    /**
     * 解析组件配置
     * 
     * @param componentType 组件类型
     * @param config 配置JSON
     * @return 组件实例
     */
    private static IModificationComponent parseComponent(String componentType, JsonObject config) {
        try {
            return switch (componentType) {
                case "inventory" -> parseInventoryComponent(config);
                case "attribute" -> parseAttributeComponent(config);
                case "skill" -> parseSkillComponent(config);
                case "effect" -> parseEffectComponent(config);
                default -> {
                    LOGGER.warn("未知的组件类型: {}", componentType);
                    yield null;
                }
            };
        } catch (Exception e) {
            LOGGER.error("解析组件失败: type={}", componentType, e);
            return null;
        }
    }
    
    /**
     * 解析物品栏组件
     */
    private static IModificationComponent parseInventoryComponent(JsonObject config) {
        // 从配置创建物品栏组件
        Map<String, Object> props = new HashMap<>();
        
        if (config.has("size")) {
            props.put("size", config.get("size").getAsInt());
        } else {
            props.put("size", 27); // 默认大小
        }
        
        if (config.has("slotType")) {
            props.put("slotType", config.get("slotType").getAsString());
        }
        
        if (config.has("allowedItems")) {
            // 解析允许的物品列表
            // 暂时简化实现
        }
        
        // 通过ComponentFactory创建组件
        return ComponentFactory.createInventoryComponent(props);
    }
    
    /**
     * 解析属性组件
     */
    private static IModificationComponent parseAttributeComponent(JsonObject config) {
        Map<String, Object> props = new HashMap<>();
        
        if (config.has("attributes")) {
            JsonObject attributes = config.getAsJsonObject("attributes");
            Map<String, Double> attributeValues = new HashMap<>();
            
            for (String attrName : attributes.keySet()) {
                attributeValues.put(attrName, attributes.get(attrName).getAsDouble());
            }
            
            props.put("attributes", attributeValues);
        }
        
        return ComponentFactory.createAttributeComponent(props);
    }
    
    /**
     * 解析技能组件
     */
    private static IModificationComponent parseSkillComponent(JsonObject config) {
        Map<String, Object> props = new HashMap<>();
        
        if (config.has("skillId")) {
            props.put("skillId", config.get("skillId").getAsString());
        }
        
        if (config.has("cooldown")) {
            props.put("cooldown", config.get("cooldown").getAsInt());
        }
        
        if (config.has("manaCost")) {
            props.put("manaCost", config.get("manaCost").getAsInt());
        }
        
        return ComponentFactory.createSkillComponent(props);
    }
    
    /**
     * 解析效果组件
     */
    private static IModificationComponent parseEffectComponent(JsonObject config) {
        Map<String, Object> props = new HashMap<>();
        
        if (config.has("effects")) {
            // 解析药水效果列表
            // 暂时简化实现
        }
        
        return ComponentFactory.createEffectComponent(props);
    }
    
    /**
     * KubeJS集成：从JS对象创建模板
     * 这个方法提供给KubeJS使用，让JS脚本可以创建和JSON相同的模板结构
     * 
     * @param id 模板ID
     * @param jsConfig JS配置对象（转换为Map）
     * @return 创建的模板
     */
    public static ModificationTemplate createFromKubeJS(ResourceLocation id, Map<String, Object> jsConfig) {
        try {
            // 将Map转换为JsonObject
            JsonObject json = mapToJsonObject(jsConfig);
            return parseTemplate(id, json);
        } catch (Exception e) {
            LOGGER.error("从KubeJS创建改造模板失败: {}", id, e);
            return null;
        }
    }
    
    /**
     * 将Map转换为JsonObject
     */
    private static JsonObject mapToJsonObject(Map<String, Object> map) {
        return GSON.toJsonTree(map).getAsJsonObject();
    }
}
