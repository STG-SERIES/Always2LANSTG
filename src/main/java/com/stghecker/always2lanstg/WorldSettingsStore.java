package com.stghecker.always2lanstg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

public final class WorldSettingsStore {
    public static final String FILE_NAME = "always2lanstg.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private WorldSettingsStore() {
    }

    public static Path file(MinecraftServer server) {
        return server.getWorldPath(LevelResource.ROOT).resolve(FILE_NAME);
    }

    public static WorldSettings load(Path file) {
        if (!Files.isRegularFile(file)) {
            return new WorldSettings();
        }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            WorldSettings settings = GSON.fromJson(reader, WorldSettings.class);
            if (settings == null) {
                return new WorldSettings();
            }
            settings.port = WorldSettings.clampPort(settings.port);
            return settings;
        } catch (Exception exception) {
            Always2LanStg.LOGGER.warn("Failed to read {}", file, exception);
            return new WorldSettings();
        }
    }

    public static void save(Path file, WorldSettings settings) {
        settings.port = WorldSettings.clampPort(settings.port);
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(settings, writer);
            }
        } catch (IOException exception) {
            Always2LanStg.LOGGER.warn("Failed to write {}", file, exception);
        }
    }
}
