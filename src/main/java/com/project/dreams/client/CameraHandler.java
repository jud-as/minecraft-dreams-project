package com.project.dreams.client;

import com.project.dreams.network.payload.CameraPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;
import com.project.dreams.Dreams;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(modid = Dreams.MOD_ID, value = Dist.CLIENT)
public class CameraHandler {
    public static boolean isLocked = false;
    public static double lockX, lockY, lockZ;
    public static float lockYaw, lockPitch;

    public static void handleCameraPacket(final CameraPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            isLocked = payload.active();
            lockX = payload.x();
            lockY = payload.y();
            lockZ = payload.z();
            lockYaw = payload.yaw();
            lockPitch = payload.pitch();
        });
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (isLocked) {
            event.setYaw(lockYaw);
            event.setPitch(lockPitch);
        }
    }
}
