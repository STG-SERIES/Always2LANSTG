package com.stghecker.always2lanstg;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public final class ClientScreens {
    private static final int MIN_PLAYER_TICKS = 20;

    private ClientScreens() {
    }

    public static void open(Minecraft minecraft, @Nullable Screen screen) {
        //? if >=26.2 {
        /*minecraft.gui.setScreen(screen);*/
        //?} else
        minecraft.setScreen(screen);
    }

    @Nullable
    public static Screen current(Minecraft minecraft) {
        //? if >=26.2 {
        /*return minecraft.gui.screen();*/
        //?} else
        return minecraft.screen;
    }

    public static void addSystemMessage(Minecraft minecraft, Component message) {
        //? if >=26.2 {
        /*minecraft.gui.hud.getChat().addClientSystemMessage(message);*/
        //?} else
        minecraft.gui.getChat().addMessage(message);
    }

    public static void updateMultiplayerPresence(Minecraft minecraft) {
        //? if >=26.2 {
        /*minecraft.getPlayerSocialManager().getPresenceHandler().tryUpdatePresence();*/
        //?}
    }

    public static boolean isWorldReady(Minecraft minecraft) {
        if (minecraft.getConnection() == null || minecraft.player == null || minecraft.level == null) {
            return false;
        }
        if (overlay(minecraft) != null) {
            return false;
        }
        return minecraft.player.tickCount >= MIN_PLAYER_TICKS;
    }

    @Nullable
    private static Overlay overlay(Minecraft minecraft) {
        //? if >=26.2 {
        /*return minecraft.gui.overlay();*/
        //?} else
        return minecraft.getOverlay();
    }
}
