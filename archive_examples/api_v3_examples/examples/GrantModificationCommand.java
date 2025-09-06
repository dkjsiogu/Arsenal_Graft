package io.github.dkjsiogu.arsenalgraft.api.v3.examples;

import io.github.dkjsiogu.arsenalgraft.api.v3.ArsenalGraftAPI;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

/**
 * 改造授予指令示例
 * 
 * 展示如何通过指令使用统一API授予改造。
 * 这是"统一接口应用示例"的具体实现。
 */
public class GrantModificationCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("arsenalgraft")
            .requires(source -> source.hasPermission(2)) // 需要管理员权限
            .then(Commands.literal("grant")
                .then(Commands.argument("player", EntityArgument.players())
                    .then(Commands.argument("modification", ResourceLocationArgument.id())
                        .executes(GrantModificationCommand::grantSingle)
                    )
                )
            )
            .then(Commands.literal("remove")
                .then(Commands.argument("player", EntityArgument.players())
                    .then(Commands.argument("modification", ResourceLocationArgument.id())
                        .executes(GrantModificationCommand::removeSingle)
                    )
                )
            )
            .then(Commands.literal("list")
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(GrantModificationCommand::listModifications)
                )
            )
            .then(Commands.literal("debug")
                .then(Commands.literal("templates")
                    .executes(GrantModificationCommand::listTemplates)
                )
            )
        );
    }
    
    /**
     * 授予改造指令执行
     */
    private static int grantSingle(CommandContext<CommandSourceStack> context) {
        try {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "player");
            ResourceLocation modId = ResourceLocationArgument.getId(context, "modification");
            
            final int[] successCount = {0}; // 使用数组来避免final问题
            
            for (ServerPlayer player : players) {
                // 调用统一API - 这是框架的唯一入口点！
                boolean success = ArsenalGraftAPI.grantModification(player, modId);
                
                if (success) {
                    successCount[0]++;
                    context.getSource().sendSuccess(
                        () -> Component.literal("成功授予玩家 " + player.getName().getString() + " 改造：" + modId),
                        true
                    );
                } else {
                    context.getSource().sendFailure(
                        Component.literal("无法授予玩家 " + player.getName().getString() + " 改造：" + modId)
                    );
                }
            }
            
            if (successCount[0] > 0) {
                context.getSource().sendSuccess(
                    () -> Component.literal("总共成功授予 " + successCount[0] + " 名玩家改造"),
                    true
                );
            }
            
            return successCount[0];
            
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("执行指令时发生错误：" + e.getMessage()));
            return 0;
        }
    }
    
    /**
     * 移除改造指令执行
     */
    private static int removeSingle(CommandContext<CommandSourceStack> context) {
        try {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "player");
            ResourceLocation modId = ResourceLocationArgument.getId(context, "modification");
            
            int successCount = 0;
            
            for (ServerPlayer player : players) {
                // 调用统一API
                boolean success = ArsenalGraftAPI.removeModification(player, modId);
                
                if (success) {
                    successCount++;
                    context.getSource().sendSuccess(
                        () -> Component.literal("成功移除玩家 " + player.getName().getString() + " 的改造：" + modId),
                        true
                    );
                } else {
                    context.getSource().sendFailure(
                        Component.literal("无法移除玩家 " + player.getName().getString() + " 的改造：" + modId)
                    );
                }
            }
            
            return successCount;
            
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("执行指令时发生错误：" + e.getMessage()));
            return 0;
        }
    }
    
    /**
     * 列出玩家改造指令执行
     */
    private static int listModifications(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            
            var modifications = ArsenalGraftAPI.getAllModifications(player);
            
            if (modifications.isEmpty()) {
                context.getSource().sendSuccess(
                    () -> Component.literal("玩家 " + player.getName().getString() + " 没有任何改造"),
                    false
                );
            } else {
                context.getSource().sendSuccess(
                    () -> Component.literal("玩家 " + player.getName().getString() + " 的改造列表："),
                    false
                );
                
                for (var slot : modifications) {
                    String status = slot.isInstalled() ? "§a已安装" : "§c未安装";
                    context.getSource().sendSuccess(
                        () -> Component.literal("  - " + slot.getTemplate().getId() + " " + status),
                        false
                    );
                }
            }
            
            return modifications.size();
            
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("执行指令时发生错误：" + e.getMessage()));
            return 0;
        }
    }
    
    /**
     * 列出所有改造模板
     */
    private static int listTemplates(CommandContext<CommandSourceStack> context) {
        try {
            var templates = ArsenalGraftAPI.getAllRegisteredModifications();
            
            if (templates.isEmpty()) {
                context.getSource().sendSuccess(
                    () -> Component.literal("没有注册任何改造模板"),
                    false
                );
            } else {
                context.getSource().sendSuccess(
                    () -> Component.literal("已注册的改造模板（共 " + templates.size() + " 个）："),
                    false
                );
                
                for (ResourceLocation id : templates) {
                    context.getSource().sendSuccess(
                        () -> Component.literal("  - " + id),
                        false
                    );
                }
            }
            
            return templates.size();
            
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("执行指令时发生错误：" + e.getMessage()));
            return 0;
        }
    }
}
