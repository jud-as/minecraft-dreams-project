package com.project.dreams.config.mixin;

import com.project.dreams.client.CameraHandler;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = net.minecraft.client.Camera.class, priority = 2000)
public abstract class CameraMixin {
    @Shadow protected abstract void setPosition(double x, double y, double z);
    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V", shift = At.Shift.AFTER))
    private void dreams$onSetPosition(CallbackInfo ci) {
        if (CameraHandler.isLocked) {
            this.setPosition(CameraHandler.lockX, CameraHandler.lockY, CameraHandler.lockZ);
        }
    }

    @Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", shift = At.Shift.AFTER))
    private void dreams$onSetRotation(CallbackInfo ci) {
        if (CameraHandler.isLocked) {
            this.setRotation(CameraHandler.lockYaw, CameraHandler.lockPitch);
        }
    }
}
