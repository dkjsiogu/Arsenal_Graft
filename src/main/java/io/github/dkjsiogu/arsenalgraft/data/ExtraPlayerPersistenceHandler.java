package io.github.dkjsiogu.arsenalgraft.data;

import io.github.dkjsiogu.arsenalgraft.core.service.ServiceRegistry;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class ExtraPlayerPersistenceHandler {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        handlePlayerLoad(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.Clone event) {
        // 在玩家克隆时也尝试恢复
        handlePlayerLoad(event.getOriginal());
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        try {
            var player = event.getEntity();
            ModificationManager modManager = ServiceRegistry.getInstance().getService(ModificationManager.class);
            if (modManager != null) {
                // 确保菜单写回 (如果 GUI 正在开着 removed 逻辑已触发, 此处只是兜底)
                modManager.savePlayerData(player);
            }
        } catch (Exception ignored) {}
    }

    private static void handlePlayerLoad(Player player) {
        try {
            ModificationManager modManager = ServiceRegistry.getInstance().getService(ModificationManager.class);
            if (modManager == null) return;

            // 通过统一的数据管理器加载数据并将插槽放入缓存
            var data = DataPersistenceManager.loadPlayerData(player);
            if (data == null) return;

            // 利用getAllInstalledSlots触发加载到缓存并忽略返回值
            modManager.getAllInstalledSlots(player);

            // 同步到客户端
            if (player instanceof ServerPlayer serverPlayer) {
                DataPersistenceManager.syncToClient(serverPlayer);
            }
        } catch (Exception e) {
            // 不阻塞登录流程
        }
    }
}
