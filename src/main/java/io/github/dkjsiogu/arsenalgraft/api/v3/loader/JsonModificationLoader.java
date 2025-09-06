package io.github.dkjsiogu.arsenalgraft.api.v3.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.dkjsiogu.arsenalgraft.api.v3.ArsenalGraftAPI;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationTemplate;
import io.github.dkjsiogu.arsenalgraft.api.v3.component.impl.InventoryComponentImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * JSON改造模板加载器
 * 
 * 负责从数据包中的JSON文件加载改造模板定义。
 * 这展示了"单一数据后端"的设计原则：
 * JSON和KubeJS最终都会创建相同的ModificationTemplate对象。
 */
public class JsonModificationLoader {
    
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    
    /**
     * 从资源管理器加载所有改造模板
     */
    public static void loadModifications(ResourceManager resourceManager) {
        System.out.println("[JsonModificationLoader] 开始加载JSON改造模板...");
        
        // 扫描所有 data/*/arsenalgraft/modifications/*.json 文件
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(
            "arsenalgraft/modifications", 
            location -> location.getPath().endsWith(".json")
        );
        
        int loadedCount = 0;
        int errorCount = 0;
        
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();
            Resource resource = entry.getValue();
            
            try {
                // 解析文件名获取改造ID
                String path = resourceLocation.getPath();
                String filename = path.substring(path.lastIndexOf('/') + 1, path.length() - 5); // 移除.json
                ResourceLocation modificationId = ResourceLocation.fromNamespaceAndPath(
                    resourceLocation.getNamespace(), 
                    filename
                );
                
                // 读取JSON内容
                JsonObject jsonObject;
                try (InputStreamReader reader = new InputStreamReader(resource.open())) {
                    jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                }
                
                // 解析为改造模板
                ModificationTemplate template = parseModificationTemplate(modificationId, jsonObject);
                
                // 注册到系统
                ArsenalGraftAPI.registerModificationTemplate(modificationId, template);
                
                System.out.println("[JsonModificationLoader] 成功加载: " + modificationId);
                loadedCount++;
                
            } catch (Exception e) {
                System.err.println("[JsonModificationLoader] 加载失败: " + resourceLocation + " - " + e.getMessage());
                e.printStackTrace();
                errorCount++;
            }
        }
        
        System.out.println("[JsonModificationLoader] 加载完成。成功: " + loadedCount + ", 失败: " + errorCount);
    }
    
    /**
     * 解析JSON对象为改造模板
     */
    private static ModificationTemplate parseModificationTemplate(ResourceLocation id, JsonObject json) {
        ModificationTemplate.Builder builder = new ModificationTemplate.Builder(id);
        
        // 解析基本信息
        if (json.has("display_name")) {
            Component displayName = Component.literal(json.get("display_name").getAsString());
            builder.displayName(displayName);
        } else {
            throw new IllegalArgumentException("Missing required field: display_name");
        }
        
        if (json.has("description")) {
            List<Component> description = new ArrayList<>();
            if (json.get("description").isJsonArray()) {
                json.getAsJsonArray("description").forEach(element -> {
                    description.add(Component.literal(element.getAsString()));
                });
            } else {
                description.add(Component.literal(json.get("description").getAsString()));
            }
            builder.description(description);
        }
        
        if (json.has("slot_type")) {
            builder.slotType(json.get("slot_type").getAsString());
        }
        
        if (json.has("max_install_count")) {
            builder.maxInstallCount(json.get("max_install_count").getAsInt());
        }
        
        if (json.has("has_configuration")) {
            builder.hasConfiguration(json.get("has_configuration").getAsBoolean());
        }
        
        // 解析组件
        if (json.has("components")) {
            JsonObject components = json.getAsJsonObject("components");
            
            // 解析物品栏组件
            if (components.has("inventory")) {
                JsonObject inventoryConfig = components.getAsJsonObject("inventory");
                
                int slotCount = inventoryConfig.has("slot_count") ? 
                    inventoryConfig.get("slot_count").getAsInt() : 1;
                    
                String slotType = inventoryConfig.has("slot_type") ? 
                    inventoryConfig.get("slot_type").getAsString() : "default";
                
                InventoryComponentImpl inventoryComponent = new InventoryComponentImpl(slotCount, slotType);
                builder.addComponent("inventory", inventoryComponent);
            }
            
            // 可以在这里添加更多组件类型的解析
            // 如: attribute, effect, automation 等
        }
        
        return builder.build();
    }
    
    /**
     * 创建示例JSON文件内容
     */
    public static String createExampleJson() {
        JsonObject example = new JsonObject();
        example.addProperty("display_name", "示例改造");
        example.addProperty("slot_type", "hand");
        example.addProperty("max_install_count", 2);
        example.addProperty("has_configuration", true);
        
        // 描述
        example.add("description", GSON.toJsonTree(List.of(
            "这是一个示例改造",
            "它展示了JSON格式的定义方式",
            "可以通过数据包进行自定义"
        )));
        
        // 组件
        JsonObject components = new JsonObject();
        JsonObject inventory = new JsonObject();
        inventory.addProperty("slot_count", 1);
        inventory.addProperty("slot_type", "hand");
        components.add("inventory", inventory);
        example.add("components", components);
        
        return GSON.toJson(example);
    }
}
