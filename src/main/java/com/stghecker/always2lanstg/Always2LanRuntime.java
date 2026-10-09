package com.stghecker.always2lanstg;

import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

public final class Always2LanRuntime {
    private static WorldSettings current;
    private static volatile boolean attemptedThisSession;
    private static volatile boolean opening;

    private Always2LanRuntime() {
    }

    public static WorldSettings currentOrDefault() {
        return current != null ? current : new WorldSettings();
    }

    public static void onServerStarted(MinecraftServer server) {
        if (server.isDedicatedServer()) {
            return;
        }
        attemptedThisSession = false;
        opening = false;
        Path file = WorldSettingsStore.file(server);
        WorldSettings pending = Always2LanPending.takeForNextWorld();
        if (pending != null) {
            current = pending;
            WorldSettingsStore.save(file, current);
        } else {
            current = WorldSettingsStore.load(file);
        }
    }

    public static void onServerTick(MinecraftServer server) {
        if (attemptedThisSession || opening || server.isDedicatedServer() || current == null || !current.enabled) {
            return;
        }
        if (server.isPublished()) {
            attemptedThisSession = true;
            return;
        }
        if (server.getPlayerCount() <= 0) {
            return;
        }
        if (!ClientScreens.isWorldReady(Minecraft.getInstance())) {
            return;
        }
        scheduleTurnOn(server, current.port);
    }

    public static void onServerStopping(MinecraftServer server) {
        attemptedThisSession = false;
        opening = false;
        current = null;
    }

    public static void save(MinecraftServer server, WorldSettings settings) {
        current = settings.copy();
        WorldSettingsStore.save(WorldSettingsStore.file(server), current);
    }

    public static void applyNow(MinecraftServer server) {
        if (current == null) {
            current = WorldSettingsStore.load(WorldSettingsStore.file(server));
        }
        if (current.enabled) {
            tryOpenNow(server);
        } else {
            closeNow(server);
        }
    }

    public static boolean tryOpenNow(MinecraftServer server) {
        if (current == null) {
            current = WorldSettingsStore.load(WorldSettingsStore.file(server));
        }
        if (!current.enabled) {
            return false;
        }
        if (!ClientScreens.isWorldReady(Minecraft.getInstance())) {
            return false;
        }
        scheduleTurnOn(server, current.port);
        return true;
    }

    public static void closeNow(MinecraftServer server) {
        attemptedThisSession = true;
        Minecraft.getInstance().execute(() -> {
            try {
                LanOpener.turnOff(server);
            } catch (RuntimeException exception) {
                Always2LanStg.LOGGER.error("Failed to close Minecraft Open to LAN", exception);
            }
        });
    }

    private static void scheduleTurnOn(MinecraftServer server, int port) {
        opening = true;
        Minecraft.getInstance().execute(() -> {
            try {
                if (!ClientScreens.isWorldReady(Minecraft.getInstance())) {
                    return;
                }
                attemptedThisSession = true;
                LanOpener.turnOn(server, port);
            } catch (RuntimeException exception) {
                attemptedThisSession = true;
                Always2LanStg.LOGGER.error("Failed to open Minecraft Open to LAN", exception);
            } finally {
                opening = false;
            }
        });
    }
}
