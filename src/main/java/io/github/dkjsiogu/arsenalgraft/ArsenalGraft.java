
package io.github.dkjsiogu.arsenalgraft;

import io.github.dkjsiogu.arsenalgraft.core.service.ServiceRegistry;
import io.github.dkjsiogu.arsenalgraft.menu.ArsenalMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import io.github.dkjsiogu.arsenalgraft.client.gui.screen.HandInventoryMenuScreen;
import io.github.dkjsiogu.arsenalgraft.network.NetworkHandler;
import io.github.dkjsiogu.arsenalgraft.api.v3.ArsenalGraftV3Initializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Arsenal Graft 3.0 - 统一架构版本
 * 
 * 架构重构完成:
 * - 移除了所有v1/v2兼容层和过时依赖
 * - 统一使用v3组件化改造系统
 * - 线程安全的依赖注入架构
 * - 完整的组件生命周期管理
 * - 完善的网络通信和数据持久化系统
 */
@Mod(ArsenalGraft.MODID)
public class ArsenalGraft {
    public static final String MODID = "arsenalgraft";
    public static final Logger LOGGER = LogManager.getLogger();

    public ArsenalGraft(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

    // 注册DeferredRegister (菜单等)
    ArsenalMenus.MENUS.register(modEventBus);

    // 注册setup事件
        modEventBus.addListener(this::commonSetup);
        
        // 注册服务器事件
        MinecraftForge.EVENT_BUS.register(this);

        // 客户端setup
        if (FMLLoader.getDist() == Dist.CLIENT) {
            modEventBus.addListener(this::clientSetup);
        }

        LOGGER.info("Arsenal Graft 3.0 正在加载...");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Arsenal Graft 3.0 正在初始化...");
        
    event.enqueueWork(() -> {
            // 初始化网络系统
            NetworkHandler.register();
            
            // 初始化服务注册表
            ServiceRegistry.getInstance().initializeCoreServices();
            
            LOGGER.info("Arsenal Graft 3.0 核心服务初始化完成");

            // 初始化 v3 系统（如果尚未初始化）
            try {
                ArsenalGraftV3Initializer.initialize();
            } catch (Exception e) {
                LOGGER.error("初始化v3系统失败", e);
            }

            // JSON + KubeJS 将在资源重载阶段自动注册改造模板，不再手动调用 ModificationRegistry
        });
    }
    
    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Arsenal Graft 3.0 客户端初始化...");
        
        event.enqueueWork(() -> {
            // 客户端特定的初始化
            // 按键绑定会通过@Mod.EventBusSubscriber自动注册
            try {
                MenuScreens.register(ArsenalMenus.HAND_INVENTORY.get(), HandInventoryMenuScreen::new);
            } catch (Exception e) {
                LOGGER.error("注册菜单Screen失败", e);
            }
            LOGGER.info("Arsenal Graft 3.0 客户端初始化完成");
        });
    }
    
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        LOGGER.info("正在注册Arsenal Graft命令...");
        
        // 注册统一API示例命令（如果示例类存在则注册，否则跳过）
        try {
            Class<?> cls = Class.forName("io.github.dkjsiogu.arsenalgraft.api.v3.examples.GrantModificationCommand");
            var method = cls.getMethod("register", com.mojang.brigadier.CommandDispatcher.class);
            method.invoke(null, event.getDispatcher());
            LOGGER.info("✓ 统一API命令注册成功 (通过反射)");
        } catch (ClassNotFoundException cnf) {
            LOGGER.info("统一API命令示例类未找到，跳过示例命令注册");
        } catch (Exception e) {
            LOGGER.warn("统一API命令注册失败", e);
        }

    // 旧的 normalhand 命令已移除，使用 /arsena 统一管理测试命令
        // 注册 arsena 统一测试命令
        try {
            io.github.dkjsiogu.arsenalgraft.command.ArsenaCommand.register(event.getDispatcher());
            LOGGER.info("✓ arsena 命令注册成功");
        } catch (Exception e) {
            LOGGER.warn("注册 arsena 命令失败", e);
        }
    }
}
