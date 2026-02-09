package com.project.dreams.network;

import com.project.dreams.network.payload.CameraPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import com.project.dreams.Dreams;

public class NetworkHandler {
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Dreams.MOD_ID);
        registrar.playToClient(
                CameraPayload.TYPE,
                CameraPayload.CODEC,
                (payload, context) -> {
                    // This lambda is safe to exist on both sides, 
                    // but we must ensure we only call client code when on the client.
                    if (context.flow().isClientbound()) {
                        com.project.dreams.client.CameraHandler.handleCameraPacket(payload, context);
                    }
                }
        );
    }
}
