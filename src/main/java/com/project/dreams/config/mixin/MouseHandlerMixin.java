package com.project.dreams.config.mixin;

import com.project.dreams.client.CameraHandler;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Redirect(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
    private void dreams$onTurnPlayer(LocalPlayer instance, double yRot, double xRot) {
        if (!CameraHandler.isLocked) {
            instance.turn(yRot, xRot);
        }
    }
}
