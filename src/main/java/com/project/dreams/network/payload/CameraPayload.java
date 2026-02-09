package com.project.dreams.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import com.project.dreams.Dreams;

public record CameraPayload(boolean active, double x, double y, double z, float yaw, float pitch) implements CustomPacketPayload {
    public static final Type<CameraPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Dreams.MOD_ID, "camera_payload"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CameraPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, CameraPayload::active,
            ByteBufCodecs.DOUBLE, CameraPayload::x,
            ByteBufCodecs.DOUBLE, CameraPayload::y,
            ByteBufCodecs.DOUBLE, CameraPayload::z,
            ByteBufCodecs.FLOAT, CameraPayload::yaw,
            ByteBufCodecs.FLOAT, CameraPayload::pitch,
            CameraPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
