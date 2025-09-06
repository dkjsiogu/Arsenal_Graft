package io.github.dkjsiogu.arsenalgraft.api.v3;

import io.github.dkjsiogu.arsenalgraft.api.v3.modification.InstalledSlot;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationManager;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationTemplate;
import io.github.dkjsiogu.arsenalgraft.core.service.ServiceRegistry;
import io.github.dkjsiogu.arsenalgraft.network.NetworkHandler;
import io.github.dkjsiogu.arsenalgraft.network.ModificationSyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Arsenal Graft 3.0 统一API入口
 *
 * 这是所有外部交互的唯一入口点。无论是通过物品、命令、还是 KubeJS 事件，
 * 都应调用这个类进行改造的授予/移除/查询等操作。
 */
public final class ArsenalGraftAPI {

    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean grantModification(Player player, ResourceLocation modificationId) {
        if (player == null || modificationId == null) {
            LOGGER.warn("Invalid params: player={}, modificationId={}", player, modificationId);
            return false;
        }

        try {
            ModificationManager modManager = ServiceRegistry.getInstance().getService(ModificationManager.class);
            if (modManager == null) {
                LOGGER.error("ModificationManager service not initialized");
                return false;
            }

            Optional<ModificationTemplate> template = modManager.getTemplate(modificationId);
            if (template.isEmpty()) {
                LOGGER.warn("Modification template not found: {}", modificationId);
                return false;
            }

            if (hasModification(player, modificationId)) {
                LOGGER.info("Player {} already has modification: {}", player.getName().getString(), modificationId);
                return false;
            }

            List<InstalledSlot> existingSlots = getAllModifications(player);
            if (existingSlots.size() >= getMaxSlots(player)) {
                LOGGER.warn("Player {} slot limit reached", player.getName().getString());
                return false;
            }

            InstalledSlot newSlot = new InstalledSlot(template.get());
            boolean success = modManager.installSlot(player, newSlot);
            if (!success) {
                LOGGER.warn("Failed to install slot: player={}, modificationId={}", player.getName().getString(), modificationId);
                return false;
            }

            if (player instanceof ServerPlayer serverPlayer) {
                try {
                    syncModificationToClient(serverPlayer, newSlot);
                    syncAllModificationsToClient(serverPlayer);
                } catch (Exception e) {
                    LOGGER.error("Network sync failed: player={}, modificationId={}", player.getName().getString(), modificationId, e);
                }
            }

            ModificationGrantedEvent event = new ModificationGrantedEvent(player, modificationId, newSlot);
            MinecraftForge.EVENT_BUS.post(event);

            LOGGER.info("Granted modification {} to player {}", modificationId, player.getName().getString());
            return true;

        } catch (Exception e) {
            LOGGER.error("Error granting modification: player={}, modificationId={}", player.getName().getString(), modificationId, e);
            return false;
        }
    }

    public static boolean hasModification(Player player, ResourceLocation modificationId) {
        if (player == null || modificationId == null) return false;
        ModificationManager modManager = ServiceRegistry.getInstance().getService(ModificationManager.class);
        if (modManager == null) return false;
        return modManager.hasModification(player, modificationId);
    }

    @Nullable
    public static InstalledSlot getModification(Player player, ResourceLocation modificationId) {
        if (player == null || modificationId == null) return null;
        ModificationManager modManager = ServiceRegistry.getInstance().getService(ModificationManager.class);
        if (modManager == null) return null;
        return modManager.getAllInstalledSlots(player)
                .stream()
                .filter(slot -> slot.getTemplate().getId().equals(modificationId))
                .findFirst()
                .orElse(null);
    }

    public static boolean removeModification(Player player, ResourceLocation modificationId) {
        if (player == null || modificationId == null) return false;
        ModificationManager modManager = ServiceRegistry.getInstance().getService(ModificationManager.class);
        if (modManager == null) return false;
        InstalledSlot slot = getModification(player, modificationId);
        if (slot == null) return false;
        boolean success = modManager.uninstallSlot(player, slot);
        if (!success) return false;
        if (player instanceof ServerPlayer serverPlayer) {
            try { syncModificationRemovalToClient(serverPlayer, slot); } catch (Exception e) { LOGGER.error("Sync removal failed", e); }
        }
        ModificationRemovedEvent event = new ModificationRemovedEvent(player, modificationId, slot);
        MinecraftForge.EVENT_BUS.post(event);
        LOGGER.info("Removed modification {} from player {}", modificationId, player.getName().getString());
        return true;
    }

    public static List<InstalledSlot> getAllModifications(Player player) {
        if (player == null) return List.of();
        ModificationManager modManager = ServiceRegistry.getInstance().getService(ModificationManager.class);
        if (modManager == null) return List.of();
        return modManager.getAllInstalledSlots(player);
    }

    public static ModificationTemplate getModificationTemplate(ResourceLocation id) {
        ModificationManager modManager = ServiceRegistry.getInstance().getService(ModificationManager.class);
        if (modManager == null) return null;
        return modManager.getTemplate(id).orElse(null);
    }

    public static List<ResourceLocation> getAllRegisteredModifications() {
        ModificationManager modManager = ServiceRegistry.getInstance().getService(ModificationManager.class);
        if (modManager == null) return List.of();
        return modManager.getAllTemplateIds();
    }

    public static void registerModificationTemplate(ResourceLocation id, ModificationTemplate template) {
        ModificationManager modManager = ServiceRegistry.getInstance().getService(ModificationManager.class);
        if (modManager != null) {
            modManager.registerTemplate(id, template);
        }
    }

    public static int getMaxSlots(Player player) {
        int baseLimit = 10;
        return baseLimit;
    }

    public static class ModificationGrantedEvent extends Event {
        private final Player player;
        private final ResourceLocation modificationId;
        private final InstalledSlot installedSlot;

        public ModificationGrantedEvent(Player player, ResourceLocation modificationId, InstalledSlot installedSlot) {
            this.player = player;
            this.modificationId = modificationId;
            this.installedSlot = installedSlot;
        }

        public Player getPlayer() { return player; }
        public ResourceLocation getModificationId() { return modificationId; }
        public InstalledSlot getInstalledSlot() { return installedSlot; }
    }

    public static class ModificationRemovedEvent extends Event {
        private final Player player;
        private final ResourceLocation modificationId;
        private final InstalledSlot removedSlot;

        public ModificationRemovedEvent(Player player, ResourceLocation modificationId, InstalledSlot removedSlot) {
            this.player = player;
            this.modificationId = modificationId;
            this.removedSlot = removedSlot;
        }

        public Player getPlayer() { return player; }
        public ResourceLocation getModificationId() { return modificationId; }
        public InstalledSlot getRemovedSlot() { return removedSlot; }
    }

    private static void syncModificationToClient(ServerPlayer player, InstalledSlot slot) {
        if (player == null || slot == null) return;
        try {
            CompoundTag slotData = slot.serializeNBT();
            ModificationSyncPacket packet = new ModificationSyncPacket(player.getUUID(), slot.getSlotId().toString(), slotData);
            NetworkHandler.sendToPlayer(packet, player);
            LOGGER.debug("Synced modification to client: player={}, slotId={}", player.getName().getString(), slot.getSlotId());
        } catch (Exception e) { LOGGER.error("Sync failed", e); }
    }

    private static void syncAllModificationsToClient(ServerPlayer player) {
        if (player == null) return;
        try {
            ModificationManager modManager = ServiceRegistry.getInstance().getService(ModificationManager.class);
            if (modManager == null) return;
            List<InstalledSlot> slots = modManager.getAllInstalledSlots(player);
            Map<String, CompoundTag> allData = new HashMap<>();
            for (InstalledSlot slot : slots) { allData.put(slot.getSlotId().toString(), slot.serializeNBT()); }
            ModificationSyncPacket packet = new ModificationSyncPacket(player.getUUID(), allData);
            NetworkHandler.sendToPlayer(packet, player);
            LOGGER.debug("Synced all modifications to client: player={}, count={}", player.getName().getString(), slots.size());
        } catch (Exception e) { LOGGER.error("Full sync failed", e); }
    }

    private static void syncModificationRemovalToClient(ServerPlayer player, InstalledSlot removedSlot) {
        if (player == null || removedSlot == null) return;
        try {
            CompoundTag emptyData = new CompoundTag();
            emptyData.putBoolean("removed", true);
            emptyData.putLong("removalTime", System.currentTimeMillis());
            ModificationSyncPacket packet = new ModificationSyncPacket(player.getUUID(), removedSlot.getSlotId().toString(), emptyData);
            NetworkHandler.sendToPlayer(packet, player);
            LOGGER.debug("Synced removal to client: player={}, slotId={}", player.getName().getString(), removedSlot.getSlotId());
        } catch (Exception e) { LOGGER.error("Removal sync failed", e); }
    }

}
