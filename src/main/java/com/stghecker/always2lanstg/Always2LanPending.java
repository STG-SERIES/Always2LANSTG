package com.stghecker.always2lanstg;

public final class Always2LanPending {
    private static final WorldSettings CURRENT = new WorldSettings();
    private static WorldSettings applyOnNextWorld;

    private Always2LanPending() {
    }

    public static void beginCreate() {
        CURRENT.enabled = false;
        CURRENT.port = WorldSettings.DEFAULT_PORT;
        applyOnNextWorld = null;
    }

    public static WorldSettings current() {
        return CURRENT;
    }

    public static void update(WorldSettings settings) {
        CURRENT.copyFrom(settings);
    }

    public static void markForNextWorld() {
        applyOnNextWorld = CURRENT.copy();
    }

    public static WorldSettings takeForNextWorld() {
        WorldSettings pending = applyOnNextWorld;
        applyOnNextWorld = null;
        return pending;
    }
}
