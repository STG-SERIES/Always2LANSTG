package com.stghecker.always2lanstg;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

public final class A2LanCommands {
    private A2LanCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("a2lan")
                .executes(A2LanCommands::status)
                .then(Commands.literal("on").executes(A2LanCommands::enable))
                .then(Commands.literal("off").executes(A2LanCommands::disable))
                .then(Commands.literal("status").executes(A2LanCommands::status))
                .then(Commands.literal("setport")
                    .then(Commands.argument("port", IntegerArgumentType.integer(WorldSettings.MIN_PORT, WorldSettings.MAX_PORT))
                        .executes(A2LanCommands::setPort)))
        );
    }

    private static int enable(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        MinecraftServer server = requireSingleplayer(source);
        if (server == null) {
            return 0;
        }
        WorldSettings settings = Always2LanRuntime.currentOrDefault();
        settings.enabled = true;
        Always2LanRuntime.save(server, settings);
        source.sendSuccess(() -> Component.translatable("always2lanstg.command.on", settings.port), false);
        Always2LanRuntime.tryOpenNow(server);
        return 1;
    }

    private static int disable(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        MinecraftServer server = requireSingleplayer(source);
        if (server == null) {
            return 0;
        }
        WorldSettings settings = Always2LanRuntime.currentOrDefault();
        settings.enabled = false;
        Always2LanRuntime.save(server, settings);
        source.sendSuccess(() -> Component.translatable("always2lanstg.command.off"), false);
        Always2LanRuntime.closeNow(server);
        return 1;
    }

    private static int setPort(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        MinecraftServer server = requireSingleplayer(source);
        if (server == null) {
            return 0;
        }
        int port = IntegerArgumentType.getInteger(context, "port");
        WorldSettings settings = Always2LanRuntime.currentOrDefault();
        settings.port = port;
        Always2LanRuntime.save(server, settings);
        source.sendSuccess(() -> Component.translatable("always2lanstg.command.port", port), false);
        Always2LanRuntime.applyNow(server);
        return 1;
    }

    private static int status(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        MinecraftServer server = requireSingleplayer(source);
        if (server == null) {
            return 0;
        }
        WorldSettings settings = Always2LanRuntime.currentOrDefault();
        String stateKey = settings.enabled ? "always2lanstg.status.on" : "always2lanstg.status.off";
        Component extra = server.isPublished()
            ? Component.translatable("always2lanstg.command.status.open", server.getPort())
            : Component.translatable("always2lanstg.command.status.closed");
        source.sendSuccess(
            () -> Component.translatable("always2lanstg.command.status", Component.translatable(stateKey), settings.port, extra),
            false
        );
        return 1;
    }

    private static MinecraftServer requireSingleplayer(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        if (server.isDedicatedServer()) {
            source.sendFailure(Component.translatable("always2lanstg.command.singleplayer_only"));
            return null;
        }
        return server;
    }
}
