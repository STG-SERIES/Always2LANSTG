package com.stghecker.always2lanstg;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Always2LanStg implements ClientModInitializer {
    public static final String MOD_ID = "always2lanstg";
    public static final String VERSION = /*$ mod_version*/ "1.0.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            A2LanCommands.register(dispatcher));

        ServerLifecycleEvents.SERVER_STARTED.register(Always2LanRuntime::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(Always2LanRuntime::onServerStopping);
        ServerTickEvents.END_SERVER_TICK.register(Always2LanRuntime::onServerTick);
    }
}
