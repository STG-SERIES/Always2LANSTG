package com.stghecker.always2lanstg;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
//? if >=26.2 {
/*import net.minecraft.network.chat.ComponentUtils;*/
//?}
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.level.GameType;

public final class LanOpener {
    private static final int PORT_ATTEMPTS = 51;

    private LanOpener() {
    }

    /**
     * Turns Minecraft's own Open to LAN on, using the same IntegratedServer APIs
     * as the pause-menu Open to LAN / Multiplayer Options screen.
     */
    public static boolean turnOn(MinecraftServer server, int requestedPort) {
        Minecraft client = Minecraft.getInstance();
        if (!ClientScreens.isWorldReady(client)) {
            Always2LanStg.LOGGER.warn("Skipped Open to LAN because the client world is not ready yet");
            return false;
        }
        if (!(server instanceof IntegratedServer integrated)) {
            return false;
        }

        int requested = WorldSettings.clampPort(requestedPort);
        if (integrated.isPublished()) {
            if (integrated.getPort() == requested) {
                ClientScreens.addSystemMessage(client, Component.translatable("always2lanstg.already_open", integrated.getPort()));
                return true;
            }
            if (!turnOff(integrated)) {
                ClientScreens.addSystemMessage(client, Component.translatable("always2lanstg.cannot_close", integrated.getPort()));
                return false;
            }
        }

        GameType gameType = integrated.getDefaultGameType();
        boolean cheats = integrated.getWorldData().isAllowCommands();
        int lastTried = requested;

        for (int offset = 0; offset < PORT_ATTEMPTS; offset++) {
            int port = requested + offset;
            if (port > WorldSettings.MAX_PORT) {
                break;
            }
            lastTried = port;
            if (!HttpUtil.isPortAvailable(port)) {
                continue;
            }
            if (publishVanilla(client, integrated, gameType, cheats, port)) {
                Always2LanStg.LOGGER.info("Opened Minecraft Open to LAN on port {}", port);
                if (port != requested) {
                    ClientScreens.addSystemMessage(client, Component.translatable("always2lanstg.opened.fallback", requested, port));
                }
                return true;
            }
        }

        ClientScreens.addSystemMessage(client, Component.translatable("commands.publish.failed"));
        Always2LanStg.LOGGER.warn("Minecraft Open to LAN failed for ports {}–{}", requested, lastTried);
        return false;
    }

    /**
     * Turns Minecraft's own Open to LAN off. 26.2 can unpublish in-place;
     * older versions keep LAN open until you leave the world.
     */
    public static boolean turnOff(MinecraftServer server) {
        if (!(server instanceof IntegratedServer integrated)) {
            return false;
        }
        return turnOff(integrated);
    }

    private static boolean turnOff(IntegratedServer integrated) {
        if (!integrated.isPublished()) {
            return true;
        }
        //? if >=26.2 {
        /*boolean stopped = integrated.unpublishServer();
        Minecraft client = Minecraft.getInstance();
        ClientScreens.updateMultiplayerPresence(client);
        if (stopped) {
            ClientScreens.addSystemMessage(client, Component.translatable("menu.multiplayerOptions.publish.stopped"));
        }
        return stopped;*/
        //?} else {
        Always2LanStg.LOGGER.info("This Minecraft version cannot close Open to LAN without leaving the world");
        return false;
        //?}
    }

    private static boolean publishVanilla(Minecraft client, IntegratedServer integrated, GameType gameType, boolean cheats, int port) {
        boolean published = false;
        //? if >=26.2 {
        /*integrated.setGameTypeForOtherPlayers(gameType);
        integrated.setCommandsAllowedForOtherPlayers(cheats);
        published = integrated.publishServer(MinecraftServer.MultiplayerScope.LAN, port);
        if (published) {
            ClientScreens.updateMultiplayerPresence(client);
            ClientScreens.addSystemMessage(client, Component.translatable(
                "menu.multiplayerOptions.publish.started.lan",
                ComponentUtils.copyOnClickText(String.valueOf(port))
            ));
        }*/
        //?} else {
        published = integrated.publishServer(gameType, cheats, port);
        if (published) {
            ClientScreens.addSystemMessage(client, Component.translatable("commands.publish.started", port));
        }
        //?}
        return published;
    }
}
