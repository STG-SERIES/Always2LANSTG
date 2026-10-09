package com.stghecker.always2lanstg.mixin;

import com.stghecker.always2lanstg.Always2LanPending;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void always2lanstg$resetPending(CallbackInfo ci) {
        Always2LanPending.beginCreate();
    }

    @Inject(method = "onCreate", at = @At("HEAD"))
    private void always2lanstg$captureSettings(CallbackInfo ci) {
        Always2LanPending.markForNextWorld();
    }
}
