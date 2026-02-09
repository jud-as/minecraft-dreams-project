package com.project.dreams.command;

import com.project.dreams.network.payload.CameraPayload;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.AngleArgument;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import com.project.dreams.Dreams;

import java.util.Collection;

@EventBusSubscriber(modid = Dreams.MOD_ID)
public class DreamsCommands {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("dreams")
            .then(Commands.literal("camera")
                .then(Commands.literal("lock")
                    .then(Commands.argument("players", EntityArgument.players())
                        .executes(context -> {
                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "players");
                            for (ServerPlayer player : players) {
                                PacketDistributor.sendToPlayer(player, new CameraPayload(true, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), player.getYRot(), player.getXRot()));
                            }
                            return 1;
                        })
                            .then(Commands.argument("pos", Vec3Argument.vec3())
                                .then(Commands.argument("yaw", AngleArgument.angle())
                                    .then(Commands.argument("pitch", AngleArgument.angle())
                                        .executes(context -> {
                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "players");
                                            Vec3 pos = Vec3Argument.getVec3(context, "pos");
                                            float yaw = AngleArgument.getAngle(context, "yaw");
                                            float pitch = AngleArgument.getAngle(context, "pitch");

                                            for (ServerPlayer player : players) {
                                                PacketDistributor.sendToPlayer(player, new CameraPayload(true, pos.x, pos.y, pos.z, yaw, pitch));
                                            }
                                            return 1;
                                        })
                                        .then(Commands.argument("center", Vec3Argument.vec3())
                                            .then(Commands.argument("radius", DoubleArgumentType.doubleArg(0))
                                                .executes(context -> {
                                                    Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "players");
                                                    Vec3 pos = Vec3Argument.getVec3(context, "pos");
                                                    float yaw = AngleArgument.getAngle(context, "yaw");
                                                    float pitch = AngleArgument.getAngle(context, "pitch");
                                                    Vec3 center = Vec3Argument.getVec3(context, "center");
                                                    double radius = DoubleArgumentType.getDouble(context, "radius");

                                                    for (ServerPlayer player : players) {
                                                        PacketDistributor.sendToPlayer(player, new CameraPayload(
                                                                true, pos.x, pos.y, pos.z, yaw, pitch,
                                                                true, center.x, center.y, center.z, radius
                                                        ));
                                                    }
                                                    return 1;
                                                }))))))))
            .then(Commands.literal("unlock")
                .then(Commands.argument("players", EntityArgument.players())
                    .executes(context -> {
                        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "players");
                        for (ServerPlayer player : players) {
                            PacketDistributor.sendToPlayer(player, new CameraPayload(false, 0, 0, 0, 0, 0));
                        }
                        return 1;
                    })))));
    }
}
