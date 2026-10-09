package com.stghecker.always2lanstg.screen;

import com.stghecker.always2lanstg.Always2LanPending;
import com.stghecker.always2lanstg.Always2LanRuntime;
import com.stghecker.always2lanstg.ClientScreens;
import com.stghecker.always2lanstg.WorldSettings;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

public class Always2LanConfigScreen extends Screen {
    private final Screen lastScreen;
    private final WorldSettings settings;
    private final boolean creatingWorld;
    private EditBox portBox;

    public Always2LanConfigScreen(Screen lastScreen, WorldSettings settings, boolean creatingWorld) {
        super(Component.translatable("always2lanstg.screen.title"));
        this.lastScreen = lastScreen;
        this.settings = settings.copy();
        this.creatingWorld = creatingWorld;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        this.addRenderableWidget(new StringWidget(centerX - 150, 20, 300, 20, this.title, this.font));

        this.addRenderableWidget(Button.builder(toggleLabel(), button -> {
            this.settings.enabled = !this.settings.enabled;
            button.setMessage(toggleLabel());
        }).bounds(centerX - 100, 70, 200, 20).build());

        this.addRenderableWidget(new StringWidget(centerX - 100, 100, 200, 20, Component.translatable("always2lanstg.port"), this.font));

        this.portBox = new EditBox(this.font, centerX - 100, 122, 200, 20, Component.translatable("always2lanstg.port"));
        this.portBox.setMaxLength(5);
        this.portBox.setHint(Component.literal(Integer.toString(WorldSettings.DEFAULT_PORT)));
        this.portBox.setValue(Integer.toString(this.settings.port));
        //? if <26.1 {
        this.portBox.setFilter(value -> value.isEmpty() || value.chars().allMatch(Character::isDigit));
        //?}
        this.addRenderableWidget(this.portBox);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
            .bounds(centerX - 100, this.height - 40, 200, 20)
            .build());
    }

    @Override
    public void onClose() {
        if (!applyPort()) {
            return;
        }
        if (this.creatingWorld) {
            Always2LanPending.update(this.settings);
        } else if (this.minecraft != null) {
            MinecraftServer server = this.minecraft.getSingleplayerServer();
            if (server != null) {
                Always2LanRuntime.save(server, this.settings);
                Always2LanRuntime.applyNow(server);
            }
        }
        ClientScreens.open(this.minecraft, this.lastScreen);
    }

    private boolean applyPort() {
        try {
            int port = Integer.parseInt(this.portBox.getValue());
            if (port < WorldSettings.MIN_PORT || port > WorldSettings.MAX_PORT) {
                this.portBox.setTextColor(0xFF5555);
                return false;
            }
            this.settings.port = port;
            this.portBox.setTextColor(0xE0E0E0);
            return true;
        } catch (NumberFormatException exception) {
            this.portBox.setTextColor(0xFF5555);
            return false;
        }
    }

    private Component toggleLabel() {
        return Component.translatable(this.settings.enabled ? "always2lanstg.toggle.on" : "always2lanstg.toggle.off");
    }
}
