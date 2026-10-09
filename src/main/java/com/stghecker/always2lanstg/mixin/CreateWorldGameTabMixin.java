package com.stghecker.always2lanstg.mixin;

import com.stghecker.always2lanstg.Always2LanPending;
import com.stghecker.always2lanstg.ClientScreens;
import com.stghecker.always2lanstg.screen.Always2LanConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$GameTab")
public abstract class CreateWorldGameTabMixin extends GridLayoutTab {
    public CreateWorldGameTabMixin(Component title) {
        super(title);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void always2lanstg$addButton(CallbackInfo ci) {
        int[] nextRow = {0};
        this.layout.visitChildren(child -> nextRow[0]++);
        this.layout.addChild(
            Button.builder(Component.translatable("always2lanstg.button"), button -> {
                Minecraft minecraft = Minecraft.getInstance();
                ClientScreens.open(minecraft, new Always2LanConfigScreen(ClientScreens.current(minecraft), Always2LanPending.current(), true));
            }).width(210).build(),
            nextRow[0],
            0
        );
    }
}
