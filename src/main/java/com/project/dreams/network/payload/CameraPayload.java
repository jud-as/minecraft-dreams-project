package com.project.dreams.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import com.project.dreams.Dreams;
import org.jspecify.annotations.NonNull;

public record CameraPayload(
        boolean active,
        double x, double y, double z,
        float yaw, float pitch,
        boolean useArea,
        double centerX, double centerY, double centerZ,
        double radius
) implements CustomPacketPayload {
    public static final Type<CameraPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Dreams.MOD_ID, "camera_payload"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CameraPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, CameraPayload::active,
            ByteBufCodecs.DOUBLE, CameraPayload::x,
            ByteBufCodecs.DOUBLE, CameraPayload::y,
            ByteBufCodecs.DOUBLE, CameraPayload::z,
            ByteBufCodecs.FLOAT, CameraPayload::yaw,
            ByteBufCodecs.FLOAT, CameraPayload::pitch,
            ByteBufCodecs.BOOL, CameraPayload::useArea,
            ByteBufCodecs.DOUBLE, CameraPayload::centerX,
            ByteBufCodecs.DOUBLE, CameraPayload::centerY,
            ByteBufCodecs.DOUBLE, CameraPayload::centerZ,
            ByteBufCodecs.DOUBLE, CameraPayload::radius,
            CameraPayload::new
    );

    // Constructor for simple lock/unlock
    public  CameraPayload(boolean active, double x, double y, double z, float yaw, float pitch) {
        this(active, x, y, z, yaw, pitch, false, 0, 0, 0, 0);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
