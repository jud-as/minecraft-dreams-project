package com.project.dreams.config.mixin;

import com.project.dreams.client.CameraHandler;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void setPosition(double x, double y, double z);
    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"))
    private void dreams$onSetPosition(Camera instance, double x, double y, double z) {
        if (CameraHandler.isLocked) {
            this.setPosition(CameraHandler.lockX, CameraHandler.lockY, CameraHandler.lockZ);
        } else {
            this.setPosition(x, y, z);
        }
    }

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V"))
    private void dreams$onSetRotation(Camera instance, float yaw, float pitch) {
        if (CameraHandler.isLocked) {
            this.setRotation(CameraHandler.lockYaw, CameraHandler.lockPitch);
        } else {
            this.setRotation(yaw, pitch);
        }
    }
}
