package com.project.dreams.config.mixin;

import com.project.dreams.client.CameraHandler;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MouseHandler.class, priority = 2000)
public class MouseHandlerMixin {
    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void dreams$onTurnPlayer(CallbackInfo ci) {
        if (CameraHandler.isLocked) {
            ci.cancel();
        }
    }
}
