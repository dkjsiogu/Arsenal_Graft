package io.github.dkjsiogu.arsenalgraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import io.github.dkjsiogu.arsenalgraft.api.v3.ArsenalGraftAPI;

import java.util.List;

/**
 * 统一测试命令: /arsena list|check|grant|remove
 */
public class ArsenaCommand {

    // 补全：为 mod 参数提供已注册的改造 id 列表
    private static final SuggestionProvider<CommandSourceStack> MOD_SUGGESTER = (ctx, builder) -> {
        try {
            for (var id : ArsenalGraftAPI.getAllRegisteredModifications()) builder.suggest(id.toString());
        } catch (Exception ignored) {}
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("arsena")
            .then(Commands.literal("list")
                .executes(ArsenaCommand::listAll)
                .then(Commands.argument("player", EntityArgument.player()).executes(ArsenaCommand::listPlayer))
            )
            .then(Commands.literal("debug")
                .then(Commands.literal("templates").executes(ArsenaCommand::debugTemplates))
            )
            .then(Commands.literal("check")
                .then(Commands.argument("mod", ResourceLocationArgument.id())
                    .suggests(MOD_SUGGESTER)
                    .executes(ArsenaCommand::checkSelf)
                    .then(Commands.argument("player", EntityArgument.player()).executes(ArsenaCommand::checkOther))
                )
            )
            .then(Commands.literal("grant").requires(src -> src.hasPermission(2))
                .then(Commands.argument("mod", ResourceLocationArgument.id())
                    .suggests(MOD_SUGGESTER)
                    .executes(ArsenaCommand::grantSelf)
                    .then(Commands.argument("pl ayer", EntityArgument.player()).executes(ArsenaCommand::grantOther))
                )
            )
            .then(Commands.literal("remove").requires(src -> src.hasPermission(2))
                .then(Commands.argument("mod", ResourceLocationArgument.id())
                    .suggests(MOD_SUGGESTER)
                    .executes(ArsenaCommand::removeSelf)
                    .then(Commands.argument("player", EntityArgument.player()).executes(ArsenaCommand::removeOther))
                )
            )
        );
    }

    private static int listAll(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        List<ResourceLocation> ids = ArsenalGraftAPI.getAllRegisteredModifications();
        if (ids.isEmpty()) {
            src.sendSuccess(() -> Component.literal("未注册任何改造模板"), false);
            return 1;
        }
        src.sendSuccess(() -> Component.literal("已注册改造模板 (count=" + ids.size() + "):"), false);
        for (ResourceLocation id : ids) src.sendSuccess(() -> Component.literal(" - " + id.toString()), false);
        return 1;
    }

    private static int listPlayer(CommandContext<CommandSourceStack> ctx) {
        try {
            CommandSourceStack src = ctx.getSource();
            Player target = EntityArgument.getPlayer(ctx, "player");
            var slots = ArsenalGraftAPI.getAllModifications(target);
            src.sendSuccess(() -> Component.literal("玩家 " + target.getName().getString() + " 拥有改造: count=" + slots.size()), false);
            for (var s : slots) src.sendSuccess(() -> Component.literal(" - " + s.getTemplate().getId().toString() + " (slot=" + s.getSlotId() + ")"), false);
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("命令执行失败"));
            return 0;
        }
    }

    private static int checkSelf(CommandContext<CommandSourceStack> ctx) {
        try {
            CommandSourceStack src = ctx.getSource();
            Player player = src.getPlayerOrException();
            ResourceLocation id = ResourceLocationArgument.getId(ctx, "mod");
            boolean has = ArsenalGraftAPI.hasModification(player, id);
            src.sendSuccess(() -> Component.literal((has?"已拥有: ":"未拥有: ") + id.toString()), false);
            return has?1:0;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("命令执行失败"));
            return 0;
        }
    }

    private static int checkOther(CommandContext<CommandSourceStack> ctx) {
        try {
            CommandSourceStack src = ctx.getSource();
            Player target = EntityArgument.getPlayer(ctx, "player");
            ResourceLocation id = ResourceLocationArgument.getId(ctx, "mod");
            boolean has = ArsenalGraftAPI.hasModification(target, id);
            src.sendSuccess(() -> Component.literal((has?"玩家已拥有: ":"玩家未拥有: ") + id.toString()), false);
            return has?1:0;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("命令执行失败"));
            return 0;
        }
    }

    private static int grantSelf(CommandContext<CommandSourceStack> ctx) {
        try {
            CommandSourceStack src = ctx.getSource();
            Player player = src.getPlayerOrException();
            ResourceLocation id = ResourceLocationArgument.getId(ctx, "mod");
            boolean ok = ArsenalGraftAPI.grantModification(player, id);
            if (ok) src.sendSuccess(() -> Component.literal("授予成功: " + id.toString()), true);
            else src.sendFailure(Component.literal("授予失败: " + id.toString()));
            return ok?1:0;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("命令执行失败"));
            return 0;
        }
    }

    private static int grantOther(CommandContext<CommandSourceStack> ctx) {
        try {
            CommandSourceStack src = ctx.getSource();
            Player target = EntityArgument.getPlayer(ctx, "player");
            ResourceLocation id = ResourceLocationArgument.getId(ctx, "mod");
            boolean ok = ArsenalGraftAPI.grantModification(target, id);
            if (ok) src.sendSuccess(() -> Component.literal("授予玩家成功: " + id.toString()), true);
            else src.sendFailure(Component.literal("授予玩家失败: " + id.toString()));
            return ok?1:0;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("命令执行失败"));
            return 0;
        }
    }

    private static int removeSelf(CommandContext<CommandSourceStack> ctx) {
        try {
            CommandSourceStack src = ctx.getSource();
            Player player = src.getPlayerOrException();
            ResourceLocation id = ResourceLocationArgument.getId(ctx, "mod");
            boolean ok = ArsenalGraftAPI.removeModification(player, id);
            if (ok) src.sendSuccess(() -> Component.literal("移除成功: " + id.toString()), true);
            else src.sendFailure(Component.literal("移除失败: " + id.toString()));
            return ok?1:0;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("命令执行失败"));
            return 0;
        }
    }

    private static int removeOther(CommandContext<CommandSourceStack> ctx) {
        try {
            CommandSourceStack src = ctx.getSource();
            Player target = EntityArgument.getPlayer(ctx, "player");
            ResourceLocation id = ResourceLocationArgument.getId(ctx, "mod");
            boolean ok = ArsenalGraftAPI.removeModification(target, id);
            if (ok) src.sendSuccess(() -> Component.literal("移除玩家成功: " + id.toString()), true);
            else src.sendFailure(Component.literal("移除玩家失败: " + id.toString()));
            return ok?1:0;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("命令执行失败"));
            return 0;
        }
    }

    // 调试: 输出当前注册的模板ID
    private static int debugTemplates(CommandContext<CommandSourceStack> ctx) {
        try {
            var ids = ArsenalGraftAPI.getAllRegisteredModifications();
            CommandSourceStack src = ctx.getSource();
            src.sendSuccess(() -> Component.literal("[debug] 模板数量=" + ids.size()), false);
            for (var id : ids) src.sendSuccess(() -> Component.literal(" - " + id), false);
            return ids.size();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("debug 执行失败"));
            return 0;
        }
    }
}
