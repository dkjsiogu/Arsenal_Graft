package io.github.dkjsiogu.arsenalgraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 * 键绑定注册和管理
 */
@Mod.EventBusSubscriber(modid = "arsenalgraft", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {
    
    public static final String CATEGORY = "key.categories.arsenalgraft";
    
    // 打开主界面的键绑定
    public static final KeyMapping OPEN_MAIN_GUI = new KeyMapping(
        "key.arsenalgraft.open_main_gui",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_B,
        CATEGORY
    );
    
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_MAIN_GUI);
    }
}