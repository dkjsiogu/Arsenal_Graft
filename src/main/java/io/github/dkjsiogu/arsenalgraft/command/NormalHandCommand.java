package io.github.dkjsiogu.arsenalgraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import io.github.dkjsiogu.arsenalgraft.api.v3.ArsenalGraftAPI;

/**
 * /normalhand grant 给予玩家一个“普通手”改造。
 */
public class NormalHandCommand {

    private static final ResourceLocation NORMAL_HAND_ID = ResourceLocation.fromNamespaceAndPath("arsenalgraft","normal_hand");

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("normalhand")
            .requires(src -> src.hasPermission(2))
            .then(Commands.literal("grant")
                .executes(NormalHandCommand::grantSelf)
            )
        );
    }

    private static int grantSelf(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        Player player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            source.sendFailure(Component.literal("需要玩家执行"));
            return 0;
        }

        boolean ok = ArsenalGraftAPI.grantModification(player, NORMAL_HAND_ID);
        if (ok) {
            source.sendSuccess(() -> Component.literal("授予普通手改造成功"), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("授予失败(可能已拥有或达到限制)"));
            return 0;
        }
    }
}
