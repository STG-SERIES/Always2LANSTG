package com.stghecker.always2lanstg;

public final class WorldSettings {
    public static final int DEFAULT_PORT = 25565;
    public static final int MIN_PORT = 1;
    public static final int MAX_PORT = 65535;

    public boolean enabled = false;
    public int port = DEFAULT_PORT;

    public WorldSettings copy() {
        WorldSettings copy = new WorldSettings();
        copy.enabled = this.enabled;
        copy.port = this.port;
        return copy;
    }

    public void copyFrom(WorldSettings other) {
        this.enabled = other.enabled;
        this.port = other.port;
    }

    public static int clampPort(int port) {
        if (port < MIN_PORT || port > MAX_PORT) {
            return DEFAULT_PORT;
        }
        return port;
    }
}
