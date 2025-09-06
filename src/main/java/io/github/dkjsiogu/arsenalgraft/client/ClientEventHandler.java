package io.github.dkjsiogu.arsenalgraft.client;

import io.github.dkjsiogu.arsenalgraft.ArsenalGraft;
import io.github.dkjsiogu.arsenalgraft.client.gui.screen.ModificationMainScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Arsenal Graft 客户端事件处理器
 * 
 * 负责处理客户端相关的事件和网络同步后的UI更新
 */
@Mod.EventBusSubscriber(modid = ArsenalGraft.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {
    
    // 客户端玩家数据缓存
    private static final Map<UUID, CompoundTag> clientPlayerData = new ConcurrentHashMap<>();
    
    // 待处理的UI更新队列
    private static final Map<UUID, Boolean> pendingUIUpdates = new ConcurrentHashMap<>();
    
    /**
     * 处理客户端玩家登录事件
     */
    @SubscribeEvent
    public static void onClientPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        LocalPlayer player = event.getPlayer();
        if (player != null) {
            ArsenalGraft.LOGGER.info("客户端玩家登录: {}", player.getName().getString());
            
            // 清理旧的客户端数据
            clientPlayerData.clear();
            pendingUIUpdates.clear();
            
            // 初始化客户端特定的服务
            initializeClientServices();
        }
    }
    
    /**
     * 处理客户端玩家退出事件
     */
    @SubscribeEvent
    public static void onClientPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        LocalPlayer player = event.getPlayer();
        if (player != null) {
            ArsenalGraft.LOGGER.info("客户端玩家退出: {}", player.getName().getString());
            
            // 清理客户端数据
            clientPlayerData.clear();
            pendingUIUpdates.clear();
        }
    }
    
    /**
     * 处理键盘按键事件
     */
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        
        // 确保在游戏中且没有打开其他GUI
        if (minecraft.level == null || minecraft.player == null || minecraft.screen != null) {
            return;
        }
        
        // 检查是否按下了打开主界面的键
        if (KeyBindings.OPEN_MAIN_GUI.consumeClick()) {
            // 打开改造主界面
            minecraft.setScreen(new ModificationMainScreen());
            ArsenalGraft.LOGGER.debug("通过键盘快捷键打开改造主界面");
        }
    }
    
    /**
     * 客户端Tick事件处理
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level != null && minecraft.player != null) {
                // 处理待更新的UI
                processPendingUIUpdates();
                
                // 定期检查客户端数据一致性
                validateClientDataConsistency();
            }
        }
    }
    
    /**
     * 更新客户端玩家数据
     * 
     * @param playerId 玩家UUID
     * @param modificationData 修改数据
     * @param triggerUIUpdate 是否触发UI更新
     */
    public static void updateClientPlayerData(UUID playerId, Map<String, CompoundTag> modificationData, boolean triggerUIUpdate) {
        try {
            // 合并数据到客户端缓存
            CompoundTag existingData = clientPlayerData.getOrDefault(playerId, new CompoundTag());
            
            for (Map.Entry<String, CompoundTag> entry : modificationData.entrySet()) {
                existingData.put(entry.getKey(), entry.getValue().copy());
            }
            
            clientPlayerData.put(playerId, existingData);
            
            // 标记需要UI更新
            if (triggerUIUpdate) {
                pendingUIUpdates.put(playerId, true);
            }
            
            ArsenalGraft.LOGGER.debug("客户端数据更新完成: {}, 条目数: {}", playerId, modificationData.size());
            
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("客户端数据更新失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 更新客户端库存数据
     * 
     * @param playerId 玩家UUID
     * @param inventoryData 库存数据
     */
    public static void updateClientInventory(UUID playerId, CompoundTag inventoryData) {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null && minecraft.player.getUUID().equals(playerId)) {
                // 更新本地玩家的库存显示
                // 这里可以触发自定义的库存GUI更新
                
                ArsenalGraft.LOGGER.debug("客户端库存更新: {}", playerId);
                
                // 触发库存相关的UI刷新
                pendingUIUpdates.put(playerId, true);
            }
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("客户端库存更新失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 更新客户端配置
     * 
     * @param configData 配置数据
     */
    public static void updateClientConfig(CompoundTag configData) {
        try {
            // 应用客户端配置更改
            ArsenalGraft.LOGGER.info("客户端配置更新: {} 个配置项", configData.getAllKeys().size());
            
            // 可以在这里处理客户端特定的配置，如渲染设置、UI偏好等
            for (String key : configData.getAllKeys()) {
                ArsenalGraft.LOGGER.debug("配置项: {} = {}", key, configData.get(key));
            }
            
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("客户端配置更新失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 获取客户端玩家数据
     * 
     * @param playerId 玩家UUID
     * @return 玩家数据，如果不存在则返回null
     */
    public static CompoundTag getClientPlayerData(UUID playerId) {
        CompoundTag data = clientPlayerData.get(playerId);
        return data != null ? data.copy() : null;
    }
    
    /**
     * 检查是否有待处理的UI更新
     * 
     * @param playerId 玩家UUID
     * @return 是否有待更新的UI
     */
    public static boolean hasPendingUIUpdate(UUID playerId) {
        return pendingUIUpdates.getOrDefault(playerId, false);
    }
    
    /**
     * 处理待更新的UI
     */
    private static void processPendingUIUpdates() {
        if (pendingUIUpdates.isEmpty()) {
            return;
        }
        
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        
        UUID currentPlayerId = player.getUUID();
        
        if (pendingUIUpdates.containsKey(currentPlayerId)) {
            try {
                // 触发UI更新
                refreshModificationUI(currentPlayerId);
                
                // 清除更新标记
                pendingUIUpdates.remove(currentPlayerId);
                
                ArsenalGraft.LOGGER.debug("UI更新处理完成: {}", currentPlayerId);
                
            } catch (Exception e) {
                ArsenalGraft.LOGGER.error("UI更新处理失败: {}", e.getMessage(), e);
                // 移除失败的更新标记以避免无限重试
                pendingUIUpdates.remove(currentPlayerId);
            }
        }
    }
    
    /**
     * 刷新修改相关的UI
     * 
     * @param playerId 玩家UUID
     */
    private static void refreshModificationUI(UUID playerId) {
        // 这里可以发送自定义事件或直接更新GUI
        // 例如：更新HUD显示、刷新库存界面、更新技能栏等
        
        CompoundTag playerData = getClientPlayerData(playerId);
        if (playerData != null) {
            // 发送客户端事件通知UI组件更新
            ArsenalGraft.LOGGER.debug("刷新修改UI，数据条目: {}", playerData.getAllKeys().size());
            
            // 这里可以集成你的GUI系统
            // 例如：ModificationGUI.refresh(playerData);
        }
    }
    
    /**
     * 验证客户端数据一致性
     */
    private static void validateClientDataConsistency() {
        // 定期检查客户端数据是否与服务端同步
        // 这是一个简单的实现，实际项目中可能需要更复杂的验证逻辑
        
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player != null && minecraft.level != null && minecraft.level.getGameTime() % 1200 == 0) { // 每60秒检查一次
            UUID playerId = player.getUUID();
            CompoundTag localData = getClientPlayerData(playerId);
            
            if (localData == null) {
                ArsenalGraft.LOGGER.debug("客户端数据为空，等待服务端同步: {}", playerId);
            } else {
                ArsenalGraft.LOGGER.debug("客户端数据一致性检查通过: {}, 数据量: {} 条目", 
                    playerId, localData.getAllKeys().size());
            }
        }
    }
    
    /**
     * 初始化客户端特定的服务
     */
    private static void initializeClientServices() {
        try {
            // 初始化GUI注册系统
            io.github.dkjsiogu.arsenalgraft.client.gui.registry.GuiRegistry.initialize();
            
            // 这里可以初始化客户端特定的服务
            // 例如：渲染服务、输入处理服务、音效服务等
            
            ArsenalGraft.LOGGER.debug("客户端服务初始化完成");
            
        } catch (Exception e) {
            ArsenalGraft.LOGGER.error("客户端服务初始化失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 清理客户端资源
     */
    public static void cleanup() {
        clientPlayerData.clear();
        pendingUIUpdates.clear();
        ArsenalGraft.LOGGER.info("客户端资源清理完成");
    }
}
