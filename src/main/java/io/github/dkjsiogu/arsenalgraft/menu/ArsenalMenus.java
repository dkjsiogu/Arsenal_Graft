package io.github.dkjsiogu.arsenalgraft.menu;

import io.github.dkjsiogu.arsenalgraft.ArsenalGraft;
import io.github.dkjsiogu.arsenalgraft.api.v3.ArsenalGraftAPI;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.InstalledSlot;
import io.github.dkjsiogu.arsenalgraft.menu.impl.HandInventoryMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;

/**
 * 菜单注册
 */
public class ArsenalMenus {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ArsenalGraft.MODID);

    public static final RegistryObject<MenuType<HandInventoryMenu>> HAND_INVENTORY = MENUS.register("hand_inventory",
            () -> IForgeMenuType.create((windowId, inv, buf) -> {
                UUID slotId = buf.readUUID();
                Player player = inv.player;
                // 客户端侧根据 slotId 查找对应插槽
                InstalledSlot slot = ArsenalGraftAPI.getAllModifications(player).stream()
                        .filter(s -> s.getSlotId().equals(slotId))
                        .findFirst().orElse(null);
                return new HandInventoryMenu(windowId, inv, slot);
            }));
}
