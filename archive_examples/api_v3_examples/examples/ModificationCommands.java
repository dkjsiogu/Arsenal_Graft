package io.github.dkjsiogu.arsenalgraft.api.v3.examples;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.dkjsiogu.arsenalgraft.api.v3.ArsenalGraftAPI;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * 改造管理指令示例
 * 
 * 展示如何通过指令使用统一的 ArsenalGraftAPI 接口
 */
public class ModificationCommands {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("arsenalgraft")
                .requires(source -> source.hasPermission(2)) // OP权限
                .then(Commands.literal("grant")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("modification", ResourceLocationArgument.id())
                            .executes(ModificationCommands::grantModification)
                        )
                    )
                )
                .then(Commands.literal("remove")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("modification", ResourceLocationArgument.id())
                            .executes(ModificationCommands::removeModification)
                        )
                    )
                )
                .then(Commands.literal("list")
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(ModificationCommands::listModifications)
                    )
                )
                .then(Commands.literal("clear")
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(ModificationCommands::clearModifications)
                    )
                )
        );
    }
    
    /**
     * 授予改造指令：/arsenalgraft grant <player> <modification>
     */
    private static int grantModification(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
            ResourceLocation modificationId = ResourceLocationArgument.getId(context, "modification");
            
            // 调用统一API
            boolean success = ArsenalGraftAPI.grantModification(targetPlayer, modificationId);
            
            if (success) {
                context.getSource().sendSuccess(
                    () -> Component.literal("成功为 " + targetPlayer.getName().getString() + " 授予改造: " + modificationId),
                    true
                );
                
                targetPlayer.displayClientMessage(
                    Component.literal("管理员为您授予了改造: " + modificationId),
                    false
                );
                
            } else {
                context.getSource().sendFailure(
                    Component.literal("无法为 " + targetPlayer.getName().getString() + " 授予改造: " + modificationId)
                );
            }
            
            return success ? 1 : 0;
            
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("指令执行失败: " + e.getMessage()));
            return 0;
        }
    }
    
    /**
     * 移除改造指令：/arsenalgraft remove <player> <modification>
     */
    private static int removeModification(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
            ResourceLocation modificationId = ResourceLocationArgument.getId(context, "modification");
            
            // 调用统一API
            boolean success = ArsenalGraftAPI.removeModification(targetPlayer, modificationId);
            
            if (success) {
                context.getSource().sendSuccess(
                    () -> Component.literal("成功为 " + targetPlayer.getName().getString() + " 移除改造: " + modificationId),
                    true
                );
                
                targetPlayer.displayClientMessage(
                    Component.literal("管理员移除了您的改造: " + modificationId),
                    false
                );
                
            } else {
                context.getSource().sendFailure(
                    Component.literal(targetPlayer.getName().getString() + " 没有改造: " + modificationId)
                );
            }
            
            return success ? 1 : 0;
            
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("指令执行失败: " + e.getMessage()));
            return 0;
        }
    }
    
    /**
     * 列出改造指令：/arsenalgraft list <player>
     */
    private static int listModifications(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
            
            var modifications = ArsenalGraftAPI.getAllModifications(targetPlayer);
            
            if (modifications.isEmpty()) {
                context.getSource().sendSuccess(
                    () -> Component.literal(targetPlayer.getName().getString() + " 没有任何改造"),
                    false
                );
            } else {
                context.getSource().sendSuccess(
                    () -> Component.literal(targetPlayer.getName().getString() + " 的改造列表:"),
                    false
                );
                
                for (var slot : modifications) {
                    ResourceLocation id = slot.getTemplate().getId();
                    String status = slot.isInstalled() ? "已启用" : "未启用";
                    context.getSource().sendSuccess(
                        () -> Component.literal("  - " + id + " (" + status + ")"),
                        false
                    );
                }
            }
            
            return 1;
            
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("指令执行失败: " + e.getMessage()));
            return 0;
        }
    }
    
    /**
     * 清空改造指令：/arsenalgraft clear <player>
     */
    private static int clearModifications(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
            
            var modifications = ArsenalGraftAPI.getAllModifications(targetPlayer);
            final int[] countArray = {0};
            
            for (var slot : modifications) {
                if (ArsenalGraftAPI.removeModification(targetPlayer, slot.getTemplate().getId())) {
                    countArray[0]++;
                }
            }
            
            final int count = countArray[0];
            context.getSource().sendSuccess(
                () -> Component.literal("成功为 " + targetPlayer.getName().getString() + " 清空了 " + count + " 个改造"),
                true
            );
            
            if (count > 0) {
                targetPlayer.displayClientMessage(
                    Component.literal("管理员清空了您的所有改造"),
                    false
                );
            }
            
            return 1;
            
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("指令执行失败: " + e.getMessage()));
            return 0;
        }
    }
}
