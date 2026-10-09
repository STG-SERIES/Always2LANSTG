package com.stghecker.always2lanstg.mixin;

import com.stghecker.always2lanstg.Always2LanRuntime;
import com.stghecker.always2lanstg.ClientScreens;
import com.stghecker.always2lanstg.screen.Always2LanConfigScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
//? if >=26.2 {
/*import net.minecraft.client.gui.screens.MultiplayerOptionsScreen;*/
//?} else
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=26.2 {
/*@Mixin(MultiplayerOptionsScreen.class)*/
//?} else
@Mixin(ShareToLanScreen.class)
public abstract class ShareToLanScreenMixin extends Screen {
    protected ShareToLanScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void always2lanstg$addButton(CallbackInfo ci) {
        this.addRenderableWidget(Button.builder(Component.translatable("always2lanstg.button"), button ->
            ClientScreens.open(this.minecraft, new Always2LanConfigScreen((Screen) (Object) this, Always2LanRuntime.currentOrDefault(), false))
        ).bounds(this.width / 2 - 155, this.height - 56, 310, 20).build());
    }
}
