package io.github.dkjsiogu.arsenalgraft.data;

import io.github.dkjsiogu.arsenalgraft.ArsenalGraft;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationTemplateLoader;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 数据驱动: 在资源重载时加载 data/arsenalgraft/modifications/*.json
 */
@Mod.EventBusSubscriber(modid = ArsenalGraft.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModificationTemplateReloadListener extends ModificationTemplateLoader {
    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new ModificationTemplateReloadListener());
        ArsenalGraft.LOGGER.info("注册 ModificationTemplateReloadListener (SimpleJsonResourceReloadListener)");
    }
}
