package io.github.dkjsiogu.arsenalgraft.api;

import net.minecraft.world.item.ItemStack;
import java.util.Map;

/**
 * 身体改造数据接口
 */
public interface IBodyModification {
    
    /**
     * 获取指定身体部位的改造数据
     */
    ModificationData getModification(BodyPart bodyPart);
    
    /**
     * 设置指定身体部位的改造数据
     */
    void setModification(BodyPart bodyPart, ModificationData data);
    
    /**
     * 检查是否有改造
     */
    boolean hasModification(BodyPart bodyPart);
    
    /**
     * 获取所有改造数据
     */
    Map<BodyPart, ModificationData> getAllModifications();
    
    /**
     * 清除指定部位的改造
     */
    void clearModification(BodyPart bodyPart);
    
    /**
     * 改造数据类
     */
    class ModificationData {
        private final String modificationType;
        private final ItemStack equippedItem;
        private final Map<String, Object> properties;
        
        public ModificationData(String modificationType, ItemStack equippedItem, Map<String, Object> properties) {
            this.modificationType = modificationType;
            this.equippedItem = equippedItem.copy();
            this.properties = properties;
        }
        
        public String getModificationType() {
            return modificationType;
        }
        
        public ItemStack getEquippedItem() {
            return equippedItem.copy();
        }
        
        public Map<String, Object> getProperties() {
            return properties;
        }
        
        public Object getProperty(String key) {
            return properties.get(key);
        }
        
        public void setProperty(String key, Object value) {
            properties.put(key, value);
        }
    }
}
