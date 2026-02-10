package com.project.dreams.client;

import com.project.dreams.Dreams;
import com.project.dreams.config.mixin.ClientInputAccessor;
import com.project.dreams.network.payload.CameraPayload;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(modid = Dreams.MOD_ID, value = Dist.CLIENT)
public class CameraHandler {
    public static boolean isLocked = false;
    public static double lockX, lockY, lockZ;
    public static float lockYaw, lockPitch;
    private static CameraType oldCameraType = CameraType.FIRST_PERSON;

    // Area-based auto-unlock
    public static boolean useArea = false;
    public static double centerX, centerY, centerZ;
    public static double unlockRadius;

    public static void handleCameraPacket(final CameraPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            boolean wasLocked = isLocked;
            isLocked = payload.active();
            lockX = payload.x();
            lockY = payload.y();
            lockZ = payload.z();
            lockYaw = payload.yaw();
            lockPitch = payload.pitch();

            useArea = payload.useArea();
            centerX = payload.centerX();
            centerY = payload.centerY();
            centerZ = payload.centerZ();
            unlockRadius = payload.radius();

            Minecraft mc = Minecraft.getInstance();
            if (isLocked && !wasLocked) {
                oldCameraType = mc.options.getCameraType();
                mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
                mc.options.bobView().set(false);
            } else if (!isLocked && wasLocked) {
                mc.options.setCameraType(oldCameraType);
                mc.options.bobView().set(true);
            }
        });
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (isLocked) {
            event.setYaw(lockYaw);
            event.setPitch(lockPitch);
        }
    }

    @SubscribeEvent
    public static void onMovementInputUpdate(MovementInputUpdateEvent event) {
        if (isLocked) {
            ClientInput input = event.getInput();
            net.minecraft.world.entity.player.Input keys = input.keyPresses;
            
            float forwardImpulse = (keys.forward() ? 1.0f : 0.0f) - (keys.backward() ? 1.0f : 0.0f);
            
            if (input instanceof ClientInputAccessor accessor) {
                accessor.dreams$setMoveVector(new Vec2(0.0f, forwardImpulse));
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (isLocked) {
            // Auto-unlock logic
            if (useArea && mc.player != null) {
                double distSq = mc.player.distanceToSqr(centerX, centerY, centerZ);
                if (distSq > unlockRadius * unlockRadius) {
                    unlockLocal();
                }
            }

            if (isLocked && mc.player != null && mc.player.input != null) {
                net.minecraft.world.entity.player.Input keys = mc.player.input.keyPresses;
                float rotationSpeed = 4.0f; // velocidade de rotação do tanque
                
                if (keys.left()) {
                    mc.player.setYRot(mc.player.getYRot() - rotationSpeed);
                    mc.player.yRotO = mc.player.getYRot();
                }
                if (keys.right()) {
                    mc.player.setYRot(mc.player.getYRot() + rotationSpeed);
                    mc.player.yRotO = mc.player.getYRot();
                }
            }
        }
    }

    private static void unlockLocal() {
        Minecraft mc = Minecraft.getInstance();
        if (isLocked) {
            isLocked = false;
            useArea = false;
            mc.options.setCameraType(oldCameraType);
            mc.options.bobView().set(true);
        }
    }
}
