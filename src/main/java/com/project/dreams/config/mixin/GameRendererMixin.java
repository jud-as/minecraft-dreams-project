package com.project.dreams.config.mixin;

import com.project.dreams.client.CameraHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void dreams$cancelBobView(PoseStack poseStack, float partialTicks, CallbackInfo ci) {
        if (CameraHandler.isLocked) {
            ci.cancel();
        }
    }
}
